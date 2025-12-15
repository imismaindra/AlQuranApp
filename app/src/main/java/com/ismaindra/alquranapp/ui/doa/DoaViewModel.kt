package com.ismaindra.alquranapp.ui.doa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ismaindra.alquranapp.data.api.ApiService
import com.ismaindra.alquranapp.data.model.Doa
import kotlinx.coroutines.launch

class DoaViewModel (private val api: ApiService): ViewModel() {

    private val _listDoa = MutableLiveData<List<Doa>>()
    val listDoa: LiveData<List<Doa>> get() = _listDoa

    fun loadDoa() {
        viewModelScope.launch {
            try {
                val res = api.getAllDoa()
                if (res.status) {
                    _listDoa.value = res.data
                }
            } catch (_: Exception) { }
        }
    }
}
