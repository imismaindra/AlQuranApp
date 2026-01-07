package com.ismaindra.alquranapp.ui.home

import com.ismaindra.alquranapp.R
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.ismaindra.alquranapp.data.api.RetrofitClient
import com.ismaindra.alquranapp.data.model.Hadist
import com.ismaindra.alquranapp.databinding.FragmentHomeBinding
import com.ismaindra.alquranapp.ui.hadist.HadistAdapter
import com.ismaindra.alquranapp.ui.hadist.HadistViewModel
import com.ismaindra.alquranapp.utils.AuthManager
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var countdownJob: Job? = null
    private var nextName: String? = null
    private var nextTimeMillis: Long? = null
    private val viewModel: HomeViewModel by lazy {
        val apiService = RetrofitClient.apiService
        HomeViewModel(apiService, AuthManager(requireContext()))
    }
    private lateinit var hadistViewModel: HadistViewModel
    private lateinit var hadistAdapter: HadistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())

        // Setup ViewModel untuk Hadist
        setupHadistViewModel()

        // Navigasi ke doa
        binding.menuDoaPage.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_doaList)
        }
        binding.menuJadwalSholat.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_jadwalSholat)
        }
        binding.menuHadist.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_hadist)
        }

        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.TvNamaHome.text = name ?: "Sahabat"
        }

        setupObservers()
        setupHadistObservers()
        checkGPS()
        viewModel.loadDoaAcak()

        // Load hadist acak
        hadistViewModel.getRandomHadist()
    }

    private fun setupHadistViewModel() {
        hadistViewModel = ViewModelProvider(this)[HadistViewModel::class.java]

        // Setup RecyclerView untuk Hadist
        binding.rvHadistUntukmu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHadistUntukmu.setHasFixedSize(true)
    }

    private fun setupHadistObservers() {
        // Observe hadist list
        hadistViewModel.hadistList.observe(viewLifecycleOwner) { hadistList ->
            if (hadistList.isNotEmpty()) {
                setupHadistAdapter(hadistList)
            }
        }

        // Observe loading state
        hadistViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Anda bisa tambahkan loading indicator di sini jika diperlukan
            // binding.progressBarHadist.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error message
        hadistViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupHadistAdapter(hadistList: List<Hadist>) {
        hadistAdapter = HadistAdapter(hadistList) { hadist ->
            onHadistClick(hadist)
        }
        binding.rvHadistUntukmu.adapter = hadistAdapter
    }

    private fun onHadistClick(hadist: Hadist) {
        val bundle = Bundle().apply {
            putParcelable("hadist_data", hadist)
        }
        findNavController().navigate(R.id.action_home_to_hadist, bundle)
        // Note: For HomeFragment, actually the user wanted "baca selengkapnya" on the page *hadist* (the list page). 
        // But since this method `onHadistClick` is in HomeFragment (for "Hadist Untukmu"), we should probably point it to the detail too if that's the intention.
        // However, the user specifically asked "pada halaman hadist...". 
        // Let's ensure the HadistFragment (FULL LIST) handles this correctly.
        // But for HomeFragment "Hadist Untukmu" items, let's also allow opening detail.
        
        // Wait, R.id.action_home_to_hadist goes to HadistFragment (List).
        // To go to detail, we need a different action or pass generic bundle.
        // Since I haven't defined action_home_to_hadistDetail in nav_graph, let's fix HadistFragment first.
    }

    private fun setupObservers() {
        // Observe doa list
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.doaList.collect { doaList ->
                    if (doaList.isNotEmpty()) {
                        updateDoaCards(doaList)
                    }
                }
            }
        }

        // Observe loading state
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // Observe error
        lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateDoaCards(doaList: List<com.ismaindra.alquranapp.data.model.Doa>) {
        val adapter = DoaAdapter(doaList) { doa ->
            Toast.makeText(context, "di klik", Toast.LENGTH_SHORT).show()
        }

        binding.rvDoaHariIni.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        binding.rvDoaHariIni.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        countdownJob?.cancel()
    }

    private fun checkGPS() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            if (isAdded && context != null) {
                getUserLocation()
            }
        }

        task.addOnFailureListener { e ->
            if (isAdded) {
                if (e is ResolvableApiException) {
                    try {
                        e.startResolutionForResult(requireActivity(), 1001)
                    } catch (_: IntentSender.SendIntentException) {
                        useDefaultLocation()
                    }
                } else {
                    useDefaultLocation()
                }
            }
        }
    }

    private fun getUserLocation() {
        if (!isAdded || context == null) return

        val fine = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }

        fusedLocation.lastLocation.addOnSuccessListener { last ->
            if (!isAdded || context == null || _binding == null) return@addOnSuccessListener

            if (last != null) {
                getAddressInfo(last.latitude, last.longitude)
            } else {
                fusedLocation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { current ->
                        if (!isAdded || context == null || _binding == null) return@addOnSuccessListener

                        if (current != null) {
                            getAddressInfo(current.latitude, current.longitude)
                        } else {
                            useDefaultLocation()
                        }
                    }
                    .addOnFailureListener {
                        useDefaultLocation()
                    }
            }
        }.addOnFailureListener {
            useDefaultLocation()
        }
    }

    private fun getAddressInfo(lat: Double, lon: Double) {
        if (!isAdded || context == null) return

        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val handler: (List<Address>?) -> Unit = handler@{ list ->
            if (!isAdded || context == null || _binding == null) return@handler

            val addr = list?.firstOrNull()
            if (addr != null) {
                val city = extractCity(addr)
                binding.tvDate.text = "$city, ${getTodayDate()}"
                fetchCityId(city) { id ->
                    if (id != null) {
                        fetchJadwal(id) { jadwal ->
                            if (jadwal != null) {
                                processJadwal(jadwal)
                            } else {
                                useDefaultLocation()
                            }
                        }
                    } else {
                        useDefaultLocation()
                    }
                }
            } else {
                useDefaultLocation()
            }
        }

        if (Build.VERSION.SDK_INT >= 33) {
            try {
                geocoder.getFromLocation(lat, lon, 1) { handler(it) }
            } catch (e: Exception) {
                useDefaultLocation()
            }
        } else {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
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

    private fun useDefaultLocation() {
        if (!isAdded || context == null || _binding == null) return
        
        // Prevent multiple calls if already showing data
        if (binding.tvDate.text.contains("Jakarta")) return

        binding.tvDate.text = "Jakarta, ${getTodayDate()}"
        
        // ID Jakarta = 1301
        fetchJadwal("1301") { jadwal ->
            if (jadwal != null) {
                processJadwal(jadwal)
            }
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()

        countdownJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                if (_binding == null || !isAdded) break

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
                        String.format("%02d : %02d : %02d", hours, minutes, seconds)
                }
                delay(1000)
            }
        }
    }

    private fun processJadwal(jadwal: JSONObject) {
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

            val displayName = namesMap[nextName] ?: nextName?.replaceFirstChar { it.uppercase() }
            binding.tvPrayerName.text = displayName
        }
    }

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

    private fun fetchCityId(city: String, callback: (String?) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = "https://api.myquran.com/v2/sholat/kota/cari/${city.lowercase()}"
                val response = URL(url).readText()
                val json = JSONObject(response)
                // Check status first
                if (json.has("status") && json.getBoolean("status")) {
                     val data = json.getJSONArray("data")
                     if (data.length() > 0) {
                         val id = data.getJSONObject(0).getString("id")
                         withContext(Dispatchers.Main) {
                             if (isAdded && context != null) callback(id)
                         }
                         return@launch
                     }
                }
                withContext(Dispatchers.Main) { callback(null) }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded && context != null) {
                        callback(null)
                    }
                }
            }
        }
    }

    private fun fetchJadwal(id: String, callback: (JSONObject?) -> Unit) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID")).format(Date())

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = "https://api.myquran.com/v2/sholat/jadwal/$id/$today"
                val response = URL(url).readText()
                val json = JSONObject(response)
                 if (json.has("status") && json.getBoolean("status")) {
                    val jadwal = json.getJSONObject("data").getJSONObject("jadwal")
                    withContext(Dispatchers.Main) {
                        if (isAdded && context != null && _binding != null) {
                            callback(jadwal)
                        }
                    }
                 } else {
                     withContext(Dispatchers.Main) { callback(null) }
                 }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded && context != null) {
                        callback(null)
                    }
                }
            }
        }
    }

    private fun getTodayDate(): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return formatter.format(Date())
    }

    private fun getNextSholat(jadwal: JSONObject): Pair<String, Long>? {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID")).format(Date())
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("id", "ID"))

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
                val date = format.parse("$today $timeString")
                if (date != null && date.after(now)) {
                    val difMillis = date.time - now.time
                    return Pair(name, difMillis)
                }
            } catch (e: Exception) {
                continue
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownJob?.cancel()
        _binding = null
        // PERBAIKAN: Tidak perlu cancel scope manual karena menggunakan lifecycleScope
    }
}