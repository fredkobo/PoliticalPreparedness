package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val repository: ElectionsRepository,
    val election: Election
) : ViewModel() {

    companion object {
        const val TAG = "VoterInfoViewModel"
    }

    private val _voterInfo = MutableLiveData<VoterInfoResponse?>()

    val voterInfo: LiveData<VoterInfoResponse?>
        get() = _voterInfo

    private val _urlBrowser = MutableLiveData<String?>()

    val urlBrowser: LiveData<String?>
        get() = _urlBrowser

    val isFollowing: LiveData<Boolean> =
        Transformations.map(repository.getElection(election.id)) { it != null }


    init {
        viewModelScope.launch {
            try {
                val address = if (election.division.state.isNotBlank())
                    "${election.division.country}, ${election.division.state}"
                else election.division.country
                val response = repository.getVoterInfo(address, election.id)
                _voterInfo.value = response
            } catch (e: Exception) {
                _voterInfo.value = VoterInfoResponse(election)
            }
        }
    }

    fun loadUrl(url: String?) {
        _urlBrowser.value = url
    }

    fun loadUrlCompleted() {
        _urlBrowser.value = null
    }

    fun toggleFollow(election: Election, isFollowing: Boolean) {
        viewModelScope.launch {
            if (!isFollowing)
                repository.saveElection(election)
            else
                repository.deleteElection(election)
        }

    }
}