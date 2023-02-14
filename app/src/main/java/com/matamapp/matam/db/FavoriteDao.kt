package com.matamapp.matam.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.matamapp.matam.CommonData

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM ${CommonData.FAVORITE_TABLE}")
    fun getAllFavorite(): List<FavoriteData>

    @Query("SELECT * FROM ${CommonData.FAVORITE_TABLE} WHERE trackId = :id")
    fun isFavorite(id: String): List<FavoriteData>?

    @Insert
    fun addToFavorite(data: FavoriteData)

    @Query("DELETE FROM  ${CommonData.FAVORITE_TABLE} WHERE trackId = :id")
    fun removeFromFavorite(id: String)
}