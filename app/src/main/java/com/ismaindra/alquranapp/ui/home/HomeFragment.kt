package com.ismaindra.alquranapp.ui.detail

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.ismaindra.alquranapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocation: FusedLocationProviderClient
    private val scope = CoroutineScope(Dispatchers.Main)
    private var countdownJob: Job? = null
    private var nextName: String? = null
    private var nextTimeMillis: Long? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        checkGPS()
    }
    override fun onPause() {
        super.onPause()
        countdownJob?.cancel()
    }


    // ---------------------------------------------------
    // CEK GPS ON
    // ---------------------------------------------------
    private fun checkGPS() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { getUserLocation() }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(requireActivity(), 1001)
                } catch (_: IntentSender.SendIntentException) {}
            }
        }
    }

    // ---------------------------------------------------
    // AMBIL LOKASI
    // ---------------------------------------------------
    private fun getUserLocation() {
        val fine = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }

        fusedLocation.lastLocation.addOnSuccessListener { last ->
            if (last != null) {
                getAddressInfo(last.latitude, last.longitude)
            } else {
                fusedLocation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { current ->
                        if (current != null) {
                            getAddressInfo(current.latitude, current.longitude)
                        } else {
                            binding.tvDate.text = "Lokasi tidak ditemukan"
                        }
                    }
            }
        }
    }
    private fun getAddressInfo(lat: Double, lon: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val handler = { list: List<Address>? ->
            val addr = list?.firstOrNull()
            val city = if (addr != null) extractCity(addr) else "Tidak diketahui"

            binding.tvDate.text = "$city, ${getTodayDate()}"

            fetchCityId(city) { id ->
                if (id != null) {
                    fetchJadwal(id) { jadwal ->
                        if (jadwal != null) {

                            val next = getNextSholat(jadwal)

                            if (next != null) {
                                nextName = next.first
                                nextTimeMillis = System.currentTimeMillis() + next.second
                                startCountdown()

                                val namesMap = mapOf(
                                    "subuh" to "Subuh",
                                    "dzuhur" to "Dzuhur",
                                    "ashar" to "Ashar",
                                    "maghrib" to "Maghrib",
                                    "isya" to "Isya"
                                )

                                val displayName =
                                    namesMap[nextName] ?: nextName?.replaceFirstChar { it.uppercase() }

                                binding.tvPrayerName.text =
                                    "$displayName ${jadwal.getString(nextName)} WIB"
                            }
                        }
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= 33) {
            // Android 13+ sudah async
            geocoder.getFromLocation(lat, lon, 1) { handler(it) }
        } else {
            // Android 12 ke bawah → WAJIB async
            scope.launch(Dispatchers.IO) {
                val list = try {
                    geocoder.getFromLocation(lat, lon, 1)
                } catch (_: Exception) {
                    null
                }

                withContext(Dispatchers.Main) {
                    handler(list)
                }
            }
        }
    }


    private fun startCountdown() {
        countdownJob?.cancel()

        countdownJob = scope.launch {
            while (isActive) {
                if (nextName != null && nextTimeMillis != null) {
                    val remaining = nextTimeMillis!! - System.currentTimeMillis()

                    if (remaining <= 0) {
                        binding.tvPrayerCountdown.text = "Sebentar lagi"
                        break
                    }

                    val hours = remaining / (1000 * 60 * 60)
                    val minutes = (remaining / (1000 * 60)) % 60
                    val seconds = (remaining / 1000) % 60

                    binding.tvPrayerCountdown.text =
                        String.format("%02d:%02d:%02d Menjelang Azan", hours, minutes, seconds)
                }
                delay(1000)
            }
        }
    }


    // ---------------------------------------------------
    // NORMALISASI KOTA (Sukolilo → Surabaya)
    // ---------------------------------------------------
    private fun extractCity(addr: Address): String {
        val locality = addr.locality ?: ""
        val subAdmin = addr.subAdminArea ?: ""

        val surabayaDistricts = listOf(
            "Sukolilo","Rungkut","Gubeng","Tambaksari","Genteng","Tenggilis",
            "Wonokromo","Wonocolo","Mulyorejo","Sambikerep","Lakarsantri",
            "Gayungan","Wiyung","Bulak","Kenjeran","Asemrowo","Karang Pilang",
            "Sawahan","Simokerto","Tandes","Gunung Anyar","Benowo","Pabean",
            "Semampir","Krembangan","Pakal","Klampis","Ngasem"
        )

        return when {
            subAdmin.contains("surabaya", true) -> "Surabaya"
            locality.contains("surabaya", true) -> "Surabaya"
            surabayaDistricts.any { locality.contains(it, true) } -> "Surabaya"
            else -> subAdmin.ifEmpty { locality }
        }
    }

    // ---------------------------------------------------
    // API: cari ID kota
    // ---------------------------------------------------
    private fun fetchCityId(city: String, callback: (String?) -> Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val url = "https://api.myquran.com/v2/sholat/kota/cari/${city.lowercase()}"
                val response = URL(url).readText()
                val json = JSONObject(response)
                val id = json.getJSONArray("data").getJSONObject(0).getString("id")

                withContext(Dispatchers.Main) { callback(id) }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback(null) }
            }
        }
    }

    // ---------------------------------------------------
    // API: ambil jadwal harian
    // ---------------------------------------------------
    private fun fetchJadwal(id: String, callback: (JSONObject?) -> Unit) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID")).format(Date())

        scope.launch(Dispatchers.IO) {
            try {
                val url = "https://api.myquran.com/v2/sholat/jadwal/$id/$today"
                val response = URL(url).readText()
                val json = JSONObject(response)
                val jadwal = json.getJSONObject("data").getJSONObject("jadwal")

                withContext(Dispatchers.Main) { callback(jadwal) }

            } catch (_: Exception) {
                withContext(Dispatchers.Main) { callback(null) }
            }
        }
    }

    private fun getTodayDate(): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return formatter.format(Date())
    }

    private fun getNextSholat(jadwal: JSONObject): Pair<String, Long>? {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID")).format(Date())
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("id", "ID")) // Tambahkan HH:mm

        val list = listOf(
            "imsak",
            "subuh",
            "terbit",
            "dhuha",
            "dzuhur",
            "ashar",
            "maghrib",
            "isya"
        )

        val now = Date()

        for (name in list) {
            try {
                val timeString = jadwal.getString(name)
                val date = format.parse("$today $timeString") // Format: "2025-11-18 04:30"
                if (date != null && date.after(now)) {
                    val difMillis = date.time - now.time
                    return Pair(name, difMillis)
                }
            } catch (e: Exception) {
                // Skip jika parsing gagal
                continue
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        scope.cancel()
    }
}
