package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch

class ElectionsViewModel(private val repository: ElectionsRepository) : ViewModel() {

    companion object {
        private const val TAG = "ElectionsViewModel";
    }

    init {
        getUpcomingElections()
    }

    private val _upcomingElections = MutableLiveData<List<Election>>()

    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    val savedElections = repository.getSavedElections()


    private fun getUpcomingElections() {
        viewModelScope.launch {
            try {
                val response = repository.getUpcomingElections()
                _upcomingElections.value = response.elections
                Log.d(TAG, response.toString())
            } catch (e: Exception) {
                e.message?.let { Log.d(TAG, it) }
            }
        }
    }

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo
        get() = _navigateToVoterInfo

    fun onElectionClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun navigateToVoterInfoComplete() {
        _navigateToVoterInfo.value = null
    }

}