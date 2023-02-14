package com.matamapp.matam.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.matamapp.matam.CommonData

@Entity(tableName = CommonData.FAVORITE_TABLE)
data class FavoriteData(
    @PrimaryKey
    val trackId: String,
    val trackTitle: String,
    val trackURL: String,
    val trackImage: String,
    val artistId: String,
    val artistName: String,
    val artistImage: String,
    val artistNationality: String,
    val yearAD: String,
    val yearHijri: String
)