package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class RepresentativeViewModel(val repository: ElectionsRepository) : ViewModel() {

    private val _representatives = MutableLiveData<List<Representative>>()
    val showSnackBarErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()

    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _loading = MutableLiveData<Boolean>()

    val loading: LiveData<Boolean>
        get() = _loading

    val address = MutableLiveData<Address>()

    init {
        _loading.value = false
    }


    fun getRepresentatives(address: Address) {
        if (validateEnteredData(address)) {
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
                showSnackBarErrorMessage.value = "Enter address line 1"
                return false
            }
            address.city.isEmpty() -> {
                showSnackBarErrorMessage.value = "Enter city"
                return false
            }
            address.state.isEmpty() -> {
                showSnackBarErrorMessage.value = "Enter state"
                return false
            }
            address.zip.isEmpty() -> {
                showSnackBarErrorMessage.value = "Enter zip"
                return false
            }
            else -> return true
        }
    }
}
