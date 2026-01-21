package com.ismaindra.alquranapp.ui.jadwalSholat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ismaindra.alquranapp.data.model.Jadwal
import com.ismaindra.alquranapp.data.model.JadwalBulananItem
import com.ismaindra.alquranapp.data.model.Kota
import com.ismaindra.alquranapp.data.repository.SholatRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class JadwalSholatViewModel(
    private val repository: SholatRepository
) : ViewModel() {

    // List semua kota untuk BottomSheet pemilihan manual
    private val _kotaList = MutableLiveData<List<Kota>>(emptyList())
    val kotaList: LiveData<List<Kota>> = _kotaList

    // List hasil search (terpisah dari kotaList)
    private val _searchResults = MutableLiveData<List<Kota>>(emptyList())
    val searchResults: LiveData<List<Kota>> = _searchResults
    private var lastSearchKeyword: String = ""

    private val _jadwal = MutableLiveData<Jadwal>()
    val jadwal: LiveData<Jadwal> = _jadwal

    private val _jadwalBulanan = MutableLiveData<List<JadwalBulananItem>>()
    val jadwalBulanan: LiveData<List<JadwalBulananItem>> = _jadwalBulanan

    private val _selectedKota = MutableLiveData<Kota?>()
    val selectedKota: LiveData<Kota?> = _selectedKota

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Muat semua kota untuk bottom sheet
    fun loadAllKota() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getAllKota()
                _kotaList.value = result.data
            } catch (e: Exception) {
                _kotaList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Search kota dari GPS
    fun searchCityByLocation(keyword: String) {
        viewModelScope.launch {
            val cleanKeyword = keyword.uppercase().trim()

            runCatching {
                repository.searchKota(cleanKeyword)
            }.onSuccess { response ->
                val foundCity = response.data?.firstOrNull()

                if (foundCity != null) {
                    selectKota(foundCity)
                } else {
                    // Fallback: coba tanpa prefix KOTA/KABUPATEN
                    if (cleanKeyword.contains("KOTA") || cleanKeyword.contains("KABUPATEN")) {
                        val fallbackKeyword = cleanKeyword
                            .replace("KOTA", "")
                            .replace("KABUPATEN", "")
                            .trim()
                        searchCityByLocation(fallbackKeyword)
                    }
                }
            }.onFailure {
                // Handle error
            }
        }
    }

    // Search kota dari search bar (update searchResults)
    fun searchKota(keyword: String) {
        val cleanKeyword = keyword.trim()
        lastSearchKeyword = cleanKeyword
        viewModelScope.launch {
            runCatching {
                repository.searchKota(cleanKeyword)
            }.onSuccess { response ->
                // Update hasil search, BUKAN kotaList
                if (cleanKeyword == lastSearchKeyword) {
                    _searchResults.value = response.data ?: emptyList()
                }
            }.onFailure {
                if (cleanKeyword == lastSearchKeyword) {
                    _searchResults.value = emptyList()
                }
            }
        }
    }

    // Clear hasil search
    fun clearSearch() {
        lastSearchKeyword = ""
        _searchResults.value = emptyList()
    }

    fun selectKota(kota: Kota) {
        _selectedKota.value = kota
        loadJadwalHarian(kota.id)
        loadJadwalBulanan(kota.id)
    }

    fun loadJadwalHarian(kotaId: String) {
        val today = LocalDate.now()
        viewModelScope.launch {
            runCatching {
                repository.getJadwalHarian(
                    kotaId,
                    today.year.toString(),
                    "%02d".format(today.monthValue),
                    "%02d".format(today.dayOfMonth)
                )
            }.onSuccess {
                _jadwal.value = it.data.jadwal
            }
        }
    }

    fun loadJadwalBulanan(kotaId: String) {
        val now = LocalDate.now()
        viewModelScope.launch {
            runCatching {
                repository.getJadwalBulanan(
                    kotaId,
                    now.year.toString(),
                    "%02d".format(now.monthValue)
                )
            }.onSuccess {
                android.util.Log.d("JadwalViewModel", "Jadwal Bulanan Size: ${it.data.jadwal.size}")
                it.data.jadwal.forEachIndexed { index, item ->
                    android.util.Log.d("JadwalViewModel", "Item $index: ${item.tanggal} - Subuh: ${item.subuh}")
                }
                _jadwalBulanan.value = it.data.jadwal
            }.onFailure { e ->
                android.util.Log.e("JadwalViewModel", "Error loading jadwal bulanan: ${e.message}")
            }
        }
    }
}
