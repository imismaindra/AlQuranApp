package com.ismaindra.alquranapp.ui.jadwalSholat

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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
    private lateinit var tvCountdown: TextView
    private lateinit var etSearchKota: EditText
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
        setupClick() // FIX: Tambahkan ini
        checkLocationPermission()
    }

    private fun bindView(view: View) {
        rvJadwal = view.findViewById(R.id.rvJadwal)
        rvBulanan = view.findViewById(R.id.rvJadwalBulanan)
        tvLokasi = view.findViewById(R.id.tvLokasi)
        tvNextPrayer = view.findViewById(R.id.tvNextPrayer)
        tvCountdown = view.findViewById(R.id.tvCountdown)
        etSearchKota = view.findViewById(R.id.etSearchKota)
    }

    private fun setupSearch() {
        etSearchKota.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length >= 3) {
                    viewModel.searchKota(query)
                } else if (query.isEmpty()) {
                    // Kosongkan hasil search saat field kosong
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
        }

        viewModel.jadwalBulanan.observe(viewLifecycleOwner) {
            bulananAdapter.submit(it)
        }

        viewModel.selectedKota.observe(viewLifecycleOwner) { kota ->
            if (kota != null) {
                tvLokasi.text = kota.lokasi
            }
        }

        // Observer untuk hasil search (tampilkan di bottom sheet)
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (!results.isNullOrEmpty()) {
                // Tampilkan hasil search dalam bottom sheet
                PilihKotaBottomSheet(results) { kota ->
                    viewModel.selectKota(kota)
                    etSearchKota.text.clear() // Clear search setelah pilih
                }.show(parentFragmentManager, "search_results")
            }
        }
    }

    private fun setupClick() {
        tvLokasi.setOnClickListener {
            // Muat semua kota untuk bottom sheet
            viewModel.loadAllKota()
            Toast.makeText(requireContext(), "Memuat daftar kota...", Toast.LENGTH_SHORT).show()

            // Observe kotaList untuk bottom sheet pemilihan manual
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
                val addresses = geo.getFromLocation(loc.latitude, loc.longitude, 1)
                val city = addresses?.firstOrNull()?.subAdminArea ?: addresses?.firstOrNull()?.adminArea

                if (city.isNullOrBlank()) {
                    loadDefaultLocation()
                } else {
                    val keyword = city.replace("Kota ", "").replace("Kabupaten ", "")
                    Toast.makeText(requireContext(), "Mencari lokasi: $city", Toast.LENGTH_SHORT).show()
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