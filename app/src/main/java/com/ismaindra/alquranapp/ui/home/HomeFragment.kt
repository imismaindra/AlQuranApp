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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.ismaindra.alquranapp.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocation: FusedLocationProviderClient

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

        checkGPS()
    }

    // -------------------------------------------------
    // CEK GPS AKTIF → munculkan dialog jika OFF
    // -------------------------------------------------
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
            getUserLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(requireActivity(), 1001)
                } catch (_: IntentSender.SendIntentException) {}
            }
        }
    }

    // -------------------------------------------------
    // MINTA LOKASI
    // -------------------------------------------------
    private fun getUserLocation() {
        val fine = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarse = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fine != PackageManager.PERMISSION_GRANTED &&
            coarse != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }

        fusedLocation.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                getAddressInfo(loc.latitude, loc.longitude)
            } else {
                fusedLocation.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener { current ->
                    if (current != null) {
                        getAddressInfo(current.latitude, current.longitude)
                    } else {
                        binding.tvDate.text = "Lokasi tidak ditemukan"
                    }
                }
            }
        }
    }

    // -------------------------------------------------
    // Ambil Alamat lengkap → proses city pakai extractCity()
    // -------------------------------------------------
    private fun getAddressInfo(lat: Double, lon: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        if (Build.VERSION.SDK_INT >= 33) {
            geocoder.getFromLocation(lat, lon, 1) { list ->
                val addr = list.firstOrNull()
                val city = if (addr != null) extractCity(addr) else "Tidak diketahui"
                binding.tvDate.text = "$city, ${getTodayDate()}"
            }
        } else {
            val list = geocoder.getFromLocation(lat, lon, 1)
            val addr = list?.firstOrNull()
            val city = if (addr != null) extractCity(addr) else "Tidak diketahui"
            binding.tvDate.text = "$city, ${getTodayDate()}"
        }
    }

    // -------------------------------------------------
    // Normalisasi nama kota → supaya tidak muncul kecamatan
    // -------------------------------------------------
    private fun extractCity(addr: Address): String {
        val locality = addr.locality
        val subAdmin = addr.subAdminArea
        val admin = addr.adminArea

        // Bersihin teks kecamatan
        val cleanLocality = locality
            ?.replace("Kecamatan", "", ignoreCase = true)
            ?.trim()

        // Bersihin Kota/Kab.
        val cleanSubAdmin = subAdmin
            ?.replace("Kota", "", ignoreCase = true)
            ?.replace("Kabupaten", "", ignoreCase = true)
            ?.replace("Kec.", "", ignoreCase = true)
            ?.replace("Kec", "", ignoreCase = true)
            ?.trim()

        // PRIORITAS pengambilan kota
        return when {
            // Jika locality bukan kecamatan → kota
            cleanLocality != null &&
                    !cleanLocality.contains("sukolilo", true) &&
                    !cleanLocality.contains("kecamatan", true) &&
                    cleanLocality.isNotEmpty() ->
                cleanLocality

            // subAdminArea mengandung Surabaya
            cleanSubAdmin != null && cleanSubAdmin.contains("surabaya", true) ->
                "Surabaya"

            // fallback
            subAdmin != null && subAdmin.contains("Surabaya", true) ->
                "Surabaya"

            // fallback locality
            locality != null -> locality

            // fallback provinsi
            admin != null -> admin

            else -> "Lokasi tidak dikenali"
        }
    }

    private fun getTodayDate(): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return formatter.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
