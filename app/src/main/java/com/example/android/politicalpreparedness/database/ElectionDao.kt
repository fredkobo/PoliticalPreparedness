package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: Election)

    @Query("select * from election_table order by electionDay")
    fun getElections(): LiveData<List<Election>>

    @Query("select * from election_table where id = :id")
    fun getElection(id: Int): LiveData<Election?>

    @Delete
    fun delete(election: Election)

    @Query("DELETE FROM election_table")
    fun clearAll()

}