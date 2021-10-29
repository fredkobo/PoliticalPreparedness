package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class RepresentativeViewModel(val repository: ElectionsRepository) : ViewModel() {

    companion object {
        private const val TAG = "RepresentativeViewModel"
    }

    private val _representatives = MutableLiveData<List<Representative>>()
    val showSnackBarErrorMessage: SingleLiveEvent<Int> = SingleLiveEvent()

    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _loading = MutableLiveData<Boolean>()

    val loading: LiveData<Boolean>
        get() = _loading

    val address = MutableLiveData<Address>()

    init {
        _loading.value = false
        address.value = Address("","","","","")
    }


    fun getRepresentatives(address: Address?) {
        address?.apply {
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
                        e.message?.let { Log.e(TAG, it) }
                        showSnackBarErrorMessage.value = R.string.error_could_not_load_reps
                    }
                    _loading.postValue(false)
                }
            }
        }
    }

    private fun validateEnteredData(address: Address): Boolean {
        when {
            address.line1.isEmpty() -> {
                showSnackBarErrorMessage.value = R.string.error_enter_address_line_1
                return false
            }
            address.city.isEmpty() -> {
                showSnackBarErrorMessage.value = R.string.error_enter_city
                return false
            }
            address.state.isEmpty() -> {
                showSnackBarErrorMessage.value = R.string.error_enter_state
                return false
            }
            address.zip.isEmpty() -> {
                showSnackBarErrorMessage.value = R.string.error_enter_zip
                return false
            }
            else -> return true
        }
    }
}
