package com.ismaindra.alquranapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ismaindra.alquranapp.data.api.ApiService
import com.ismaindra.alquranapp.data.model.Doa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val api: ApiService): ViewModel(){
    private val _doaList = MutableStateFlow<List<Doa>>(emptyList())
    val doaList: StateFlow<List<Doa>> get() = _doaList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun loadDoaAcak(){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value =null

            try {
                val response = api.getRandomDoa()
                if(response.status){
                    _doaList.value =response.data
                }else{
                    _errorMessage.value = response.message
                }
            }catch (e: Exception){
                _errorMessage.value = e.message
            }finally {
                _isLoading.value = false
            }

        }
    }

}