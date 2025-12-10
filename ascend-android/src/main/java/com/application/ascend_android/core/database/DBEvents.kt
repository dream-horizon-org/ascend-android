package com.application.ascend_android

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pluggerEvents")
data class DBEvents(
    @ColumnInfo(name = "payload")
    val payload: String,

    @ColumnInfo(name = "time_stamp")
    val time_stamp: Long,

    @ColumnInfo(name = "eventType")
    val eventType: String,

    @PrimaryKey
    var id: String = "",

    @ColumnInfo(name = "status")
    val status: Int,
)