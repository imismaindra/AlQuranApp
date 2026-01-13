package com.ismaindra.alquranapp.ui.hadist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ismaindra.alquranapp.data.api.RetrofitClient
import com.ismaindra.alquranapp.data.model.Hadist
import kotlinx.coroutines.launch

class HadistViewModel : ViewModel() {

    private val _hadistList = MutableLiveData<List<Hadist>>()
    val hadistList: LiveData<List<Hadist>> = _hadistList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getRandomHadist() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = RetrofitClient.hadistApi.getRandomHadist()

                if (response.isSuccessful) {
                    response.body()?.let { hadistResponse ->
                        if (hadistResponse.status) {
                            _hadistList.value = hadistResponse.data
                        } else {
                            _errorMessage.value = hadistResponse.message
                        }
                    }
                } else {
                    _errorMessage.value = "Gagal memuat hadist: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllHadist() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = RetrofitClient.hadistApi.getAllHadist()

                if (response.isSuccessful) {
                    response.body()?.let { hadistResponse ->
                        if (hadistResponse.status) {
                            _hadistList.value = hadistResponse.data
                        } else {
                            _errorMessage.value = hadistResponse.message
                        }
                    }
                } else {
                    _errorMessage.value = "Gagal memuat hadist: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}