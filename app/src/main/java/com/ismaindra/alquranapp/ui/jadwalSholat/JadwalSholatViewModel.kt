package com.ismaindra.alquranapp.ui.jadwalSholat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ismaindra.alquranapp.data.model.Jadwal
import com.ismaindra.alquranapp.data.model.Kota
import com.ismaindra.alquranapp.data.repository.SholatRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class JadwalSholatViewModel(private val repository: SholatRepository): ViewModel() {
    private val _kota = MutableLiveData<List<Kota>>(emptyList())
    val kota: LiveData<List<Kota>> = _kota

    private val _jadwal = MutableLiveData<Jadwal>()
    val jadwal: LiveData<Jadwal> = _jadwal

    fun loadAllKota(){
        viewModelScope.launch {
            try {
                _kota.value = repository.getAllKota().data
            }catch (_: Exception){}
        }
    }

    fun searchKota(keyword:String){
        viewModelScope.launch {
            _kota.value = repository.searchKota(keyword).data
        }
    }

    fun loadJadwalHarian(kotaId: String, date: LocalDate){
        viewModelScope.launch {
            val response = repository.getJadwalHarian(
                kotaId,
                date.year.toString(),
                "%02d".format(date.monthValue),
                "%02d".format(date.dayOfMonth)
            )
            _jadwal.value = response.data.jadwal
        }
    }

}