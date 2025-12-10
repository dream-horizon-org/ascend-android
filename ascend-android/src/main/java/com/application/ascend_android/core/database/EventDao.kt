package com.application.ascend_android

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface EventDao {

    @Query("SELECT * FROM pluggerEvents  ORDER BY time_stamp ASC")
    fun getAllEvents(): Flow<List<DBEvents>>

    @Query("SELECT * FROM pluggerEvents WHERE eventType == :filter AND id not in (:eventIds) ORDER BY time_stamp ASC")
    fun getAllEventsWithFilter(filter: String, eventIds: ArrayList<String>): List<DBEvents>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(DBEvents: DBEvents)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<DBEvents>)

    @Query("DELETE FROM pluggerEvents")
    suspend fun deleteAll(): Int


    @Query("DELETE FROM pluggerEvents WHERE id in (:eventIds)  AND eventType == :filter AND status == :eventStatus")
    suspend fun deleterConditionally(
        eventIds: ArrayList<String>,
        filter: String,
        eventStatus: Int
    ): Int

    @Query("DELETE FROM pluggerEvents WHERE id in (:eventIds)  AND eventType == :filter")
    suspend fun deleterWithoutStatus(eventIds: ArrayList<String>, filter: String): Int

}
