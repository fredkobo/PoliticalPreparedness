package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionsRepository {
    suspend fun getUpcomingElections(): ElectionResponse =
        withContext(Dispatchers.IO) {
            CivicsApi.retrofitService.getElections()
        }
}