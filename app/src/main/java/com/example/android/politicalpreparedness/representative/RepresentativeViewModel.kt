package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import java.lang.Exception

class RepresentativeViewModel(val repository: ElectionsRepository) : ViewModel() {

    private val _representatives = MutableLiveData<List<Representative>>()
    private val _showSnackBarErrorMessage = MutableLiveData<String>()

    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _loading = MutableLiveData<Boolean>()

    val loading: LiveData<Boolean>
        get() = _loading

    val showSnackBarErrorMessage: LiveData<String>
        get() = _showSnackBarErrorMessage

    val address = MutableLiveData<Address>()

    init {
        _loading.value = false
    }


    fun callGetRepresentative(address: Address) {
        if(validateEnteredData(address)) {
            _loading.postValue(true)
            viewModelScope.launch {
                try {
                    val response =
                        repository.getRepresentatives(address = address.toFormattedString())

                    _representatives.value =
                        response.offices.flatMap { office ->
                            office.getRepresentatives(response.officials)
                        }
                } catch (e: Exception) {
                    e.message
                }
                _loading.postValue(false)
            }
        }
    }

    private fun validateEnteredData(address: Address): Boolean {
        when {
            address.line1.isEmpty() -> {
                _showSnackBarErrorMessage.value = "Enter address line 1"
                return false
            }
            address.city.isEmpty() -> {
                _showSnackBarErrorMessage.value = "Enter city"
                return false
            }
            address.state.isEmpty() -> {
                _showSnackBarErrorMessage.value = "Enter state"
                return false
            }
            address.zip.isEmpty() -> {
                _showSnackBarErrorMessage.value = "Enter zip"
                return false
            }
            else -> return true
        }
    }

    fun showErrorDone() {
        _showSnackBarErrorMessage.value = null
    }

}
