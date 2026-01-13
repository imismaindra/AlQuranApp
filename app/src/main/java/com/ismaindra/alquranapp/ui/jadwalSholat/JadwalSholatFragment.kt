package com.ismaindra.alquranapp.ui.jadwalSholat

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.ismaindra.alquranapp.R
import com.ismaindra.alquranapp.data.api.ExsternalRetrofitClient
import com.ismaindra.alquranapp.data.model.Kota
import com.ismaindra.alquranapp.data.repository.SholatRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class JadwalSholatFragment : Fragment(R.layout.fragment_jadwalsholat) {

    private companion object {
        const val DEFAULT_KOTA_ID = "1638"
        const val DEFAULT_LOKASI = "KOTA SURABAYA"
    }

    private lateinit var rvJadwal: RecyclerView
    private lateinit var rvBulanan: RecyclerView
    private lateinit var tvLokasi: TextView
    private lateinit var tvNextPrayer: TextView
    private lateinit var tvNextPrayerTime: TextView
    private lateinit var tvCountdown: TextView
    private lateinit var etSearchKota: EditText
    private lateinit var headerContainer: LinearLayout

    private val jadwalAdapter = JadwalAdapter()
    private val bulananAdapter = JadwalBulananAdapter()

    private val requestLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getDeviceLocation()
        } else {
            Toast.makeText(requireContext(), "Izin lokasi ditolak, menggunakan default.", Toast.LENGTH_SHORT).show()
            loadDefaultLocation()
        }
    }

    private val viewModel: JadwalSholatViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = SholatRepository(ExsternalRetrofitClient.api)
                @Suppress("UNCHECKED_CAST")
                return JadwalSholatViewModel(repo) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView(view)
        setupRecycler()
        observeViewModel()
        setupSearch()
        setupClick()
        checkLocationPermission()
    }

    private fun bindView(view: View) {
        rvJadwal = view.findViewById(R.id.rvJadwal)
        rvBulanan = view.findViewById(R.id.rvJadwalBulanan)
        tvLokasi = view.findViewById(R.id.tvLokasi)
        tvNextPrayer = view.findViewById(R.id.tvNextPrayer)
        tvNextPrayerTime = view.findViewById(R.id.tvNextPrayerTime)
        tvCountdown = view.findViewById(R.id.tvCountdown)
        etSearchKota = view.findViewById(R.id.etSearchKota)
        headerContainer = view.findViewById(R.id.headerContainer)
    }

    private fun setupSearch() {
        etSearchKota.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length >= 3) {
                    viewModel.searchKota(query)
                } else if (query.isEmpty()) {
                    viewModel.clearSearch()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupRecycler() {
        rvJadwal.layoutManager = LinearLayoutManager(requireContext())
        rvJadwal.adapter = jadwalAdapter

        rvBulanan.layoutManager = LinearLayoutManager(requireContext())
        rvBulanan.adapter = bulananAdapter
    }

    private fun observeViewModel() {
        viewModel.jadwal.observe(viewLifecycleOwner) {
            jadwalAdapter.submit(it)
            if (it != null) {
                updateUIWithJadwal(it)
            }
        }

        viewModel.jadwalBulanan.observe(viewLifecycleOwner) {
            bulananAdapter.submit(it)
        }

        viewModel.selectedKota.observe(viewLifecycleOwner) { kota ->
            if (kota != null) {
                tvLokasi.text = kota.lokasi
            }
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (!results.isNullOrEmpty()) {
                PilihKotaBottomSheet(results) { kota ->
                    viewModel.selectKota(kota)
                    etSearchKota.text.clear()
                }.show(parentFragmentManager, "search_results")
            }
        }
    }

    private fun setupClick() {
        tvLokasi.setOnClickListener {
            viewModel.loadAllKota()
            Toast.makeText(requireContext(), "Memuat daftar kota...", Toast.LENGTH_SHORT).show()

            viewModel.kotaList.observe(viewLifecycleOwner, object : androidx.lifecycle.Observer<List<Kota>> {
                override fun onChanged(list: List<Kota>) {
                    if (!list.isNullOrEmpty()) {
                        PilihKotaBottomSheet(list) { kota ->
                            viewModel.selectKota(kota)
                        }.show(parentFragmentManager, "kota")
                        viewModel.kotaList.removeObserver(this)
                    }
                }
            })
        }
    }

    private fun loadDefaultLocation() {
        val defaultKota = Kota(DEFAULT_KOTA_ID, DEFAULT_LOKASI)
        viewModel.selectKota(defaultKota)
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getDeviceLocation()
        } else {
            requestLocationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun updateUIWithJadwal(jadwal: com.ismaindra.alquranapp.data.model.Jadwal) {
        try {
            val now = Calendar.getInstance()
            val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

            // Parse time "HH:mm" to minutes
            fun toMinutes(time: String): Int {
                return try {
                    val split = time.split(":")
                    if (split.size >= 2) {
                        split[0].toInt() * 60 + split[1].toInt()
                    } else 0
                } catch (e: Exception) {
                    0
                }
            }

            val subuh = toMinutes(jadwal.subuh)
            val dhuha = toMinutes(jadwal.dhuha)
            val dzuhur = toMinutes(jadwal.dzuhur)
            val ashar = toMinutes(jadwal.ashar)
            val maghrib = toMinutes(jadwal.maghrib)
            val isya = toMinutes(jadwal.isya)

            var nextPrayerName = ""
            var nextPrayerTimeStr = ""
            var nextPrayerMinutes = 0
            val bgDrawable: Int

            when {
                currentMinutes < subuh -> {
                    // Sebelum Subuh (Dini Hari) -> Tema Isya (Gelap/Malam)
                    bgDrawable = R.drawable.header_isya
                    nextPrayerName = "Subuh"
                    nextPrayerTimeStr = jadwal.subuh
                    nextPrayerMinutes = subuh
                }
                currentMinutes < dhuha -> {
                    // Subuh - Dhuha -> Tema Subuh (Fajar)
                    bgDrawable = R.drawable.header_subuh
                    nextPrayerName = "Dhuha"
                    nextPrayerTimeStr = jadwal.dhuha
                    nextPrayerMinutes = dhuha
                }
                currentMinutes < dzuhur -> {
                    // Dhuha - Dzuhur -> Tema Dzuhur (Siang Terang)
                    bgDrawable = R.drawable.header_dzuhur
                    nextPrayerName = "Dzuhur"
                    nextPrayerTimeStr = jadwal.dzuhur
                    nextPrayerMinutes = dzuhur
                }
                currentMinutes < ashar -> {
                    // Dzuhur - Ashar -> Tema Dzuhur
                    bgDrawable = R.drawable.header_dzuhur
                    nextPrayerName = "Ashar"
                    nextPrayerTimeStr = jadwal.ashar
                    nextPrayerMinutes = ashar
                }
                currentMinutes < maghrib -> {
                    // Ashar - Maghrib -> Tema Ashar (Sore)
                    bgDrawable = R.drawable.header_ashar
                    nextPrayerName = "Maghrib"
                    nextPrayerTimeStr = jadwal.maghrib
                    nextPrayerMinutes = maghrib
                }
                currentMinutes < isya -> {
                    // Maghrib - Isya -> Tema Maghrib (Senja)
                    bgDrawable = R.drawable.header_maghrib
                    nextPrayerName = "Isya"
                    nextPrayerTimeStr = jadwal.isya
                    nextPrayerMinutes = isya
                }
                else -> {
                    // Setelah Isya -> Tema Isya (Malam)
                    bgDrawable = R.drawable.header_isya
                    nextPrayerName = "Subuh"
                    nextPrayerTimeStr = jadwal.subuh
                    nextPrayerMinutes = subuh + (24 * 60) // Subuh besok (+24 jam)
                }
            }

            // Terapkan Tema
            headerContainer.setBackgroundResource(bgDrawable)
            setNextPrayerInfo(nextPrayerName, nextPrayerTimeStr)

            // Hitung Countdown
            val diffMinutes = nextPrayerMinutes - currentMinutes
            val hours = diffMinutes / 60
            val minutes = diffMinutes % 60
            
            val countdownText = if (hours > 0) {
                "$hours jam $minutes menit lagi"
            } else {
                "$minutes menit lagi"
            }
            tvCountdown.text = countdownText

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback jika terjadi error parsing waktu
            headerContainer.setBackgroundResource(R.drawable.header_gradient)
        }
    }

    private fun setNextPrayerInfo(name: String, time: String) {
        tvNextPrayer.text = name
        tvNextPrayerTime.text = "$time WIB"
    }

    private fun getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val client = LocationServices.getFusedLocationProviderClient(requireContext())
        client.lastLocation.addOnSuccessListener { loc ->
            if (loc == null) {
                loadDefaultLocation()
                return@addOnSuccessListener
            }

            val geo = Geocoder(requireContext(), Locale("id", "ID"))
            try {
                // Gunakan scope coroutine atau thread background sebaiknya, tapi Geocoder sering ok di main thread utk test simple
                val addresses = geo.getFromLocation(loc.latitude, loc.longitude, 1)
                val city = addresses?.firstOrNull()?.subAdminArea ?: addresses?.firstOrNull()?.adminArea

                if (city.isNullOrBlank()) {
                    loadDefaultLocation()
                } else {
                    val keyword = city.replace("Kota ", "").replace("Kabupaten ", "")
                    viewModel.searchCityByLocation(keyword)
                }
            } catch (e: Exception) {
                loadDefaultLocation()
            }
        }.addOnFailureListener {
            loadDefaultLocation()
        }
    }
}