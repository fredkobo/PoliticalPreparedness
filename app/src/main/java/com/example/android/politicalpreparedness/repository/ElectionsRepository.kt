package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionsRepository constructor(val electionDao: ElectionDao) {

    // database methods
    suspend fun saveElection(election: Election) {
        withContext(Dispatchers.IO) {
            electionDao.insertAll(election)
        }
    }

    suspend fun deleteElection(election: Election) {
        withContext(Dispatchers.IO) {
            electionDao.delete(election)
        }
    }

    fun getSavedElections(): LiveData<List<Election>> =
        electionDao.getElections()

    fun getElection(electionId: Int): LiveData<Election?> =
        electionDao.getElection(electionId)


    // api methods
    suspend fun getUpcomingElections(): ElectionResponse =
        withContext(Dispatchers.IO) {
            CivicsApi.retrofitService.getElections()
        }

    suspend fun getVoterInfo(address: String, electionId: Int): VoterInfoResponse =
        withContext(Dispatchers.IO) {
            CivicsApi.retrofitService.getVoterInfo(address, electionId)
        }

    suspend fun getRepresentatives(address: String): RepresentativeResponse =
        withContext(Dispatchers.IO) {
            CivicsApi.retrofitService.getRepresentatives(address)
        }
}