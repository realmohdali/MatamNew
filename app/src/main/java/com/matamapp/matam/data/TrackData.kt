package com.matamapp.matam.data

data class TrackData(
    val id: String,
    val title: String,
    val trackURl: String,
    val trackImage: String,
    val artist: ArtistData,
    val year: YearData
)