package com.ismaindra.alquranapp.ui.jadwalSholat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.R
import com.ismaindra.alquranapp.data.api.ExsternalRetrofitClient
import com.ismaindra.alquranapp.data.repository.SholatRepository
import java.time.LocalDate
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import java.util.Locale

class JadwalSholatFragment : Fragment(R.layout.fragment_jadwalsholat) {

    private lateinit var rvJadwal: RecyclerView
    private lateinit var tvLokasi: TextView
    private lateinit var tvNextPrayer: TextView
    private lateinit var tvCountdown: TextView
    private lateinit var etSearchKota: EditText
    private var isLocationResolved = false

    private companion object {
        const val LOCATION_PERMISSION_REQUEST = 1001
    }


    private val jadwalAdapter = JadwalAdapter()

    private val viewModel: JadwalSholatViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                val repository = SholatRepository(
                    ExsternalRetrofitClient.api
                )

                @Suppress("UNCHECKED_CAST")
                return JadwalSholatViewModel(repository) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView(view)
        setupRecyclerView()
        observeViewModel()
        setupSearch()
        requestLocation()


        // load awal (default)
        viewModel.loadJadwalHarian(
            kotaId = "1632",
            date = LocalDate.now()
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocation()
        }
    }


    private fun bindView(view: View) {
        rvJadwal = view.findViewById(R.id.rvJadwal)
        tvLokasi = view.findViewById(R.id.tvLokasi)
        tvNextPrayer = view.findViewById(R.id.tvNextPrayer)
        tvCountdown = view.findViewById(R.id.tvCountdown)
        etSearchKota = view.findViewById(R.id.etSearchKota)
    }

    private fun setupRecyclerView() {
        rvJadwal.layoutManager = LinearLayoutManager(requireContext())
        rvJadwal.adapter = jadwalAdapter
    }

    private fun observeViewModel() {

        viewModel.jadwal.observe(viewLifecycleOwner) { jadwal ->
            jadwalAdapter.submit(jadwal)

            // header sementara (belum hitung sholat aktif)
            tvNextPrayer.text = "Ashar, ${jadwal.ashar}"
            tvCountdown.text = "Hari ini"
        }
        viewModel.kota.observe(viewLifecycleOwner) { kotaList ->
            if (kotaList.isNullOrEmpty()) return@observe
            if(isLocationResolved)return@observe

            val kota = kotaList.first()
            isLocationResolved = true
            tvLokasi.text = kota.lokasi

            viewModel.loadJadwalHarian(
                kotaId = kota.id,
                date = LocalDate.now()
            )
        }


    }

    private fun setupSearch() {
        etSearchKota.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val keyword = s?.toString()?.trim().orEmpty()
                if (keyword.length >= 3) {
                    isLocationResolved = false
                    viewModel.searchKota(keyword)
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun requestLocation() {
        if (isLocationResolved) return
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location ?: return@addOnSuccessListener

            val geocoder = Geocoder(requireContext(), Locale("id", "ID"))
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            val cityName = addresses
                ?.firstOrNull()
                ?.subAdminArea
                ?: return@addOnSuccessListener

            // auto search kota ke API
            viewModel.searchKota(cityName)
        }
    }

}
