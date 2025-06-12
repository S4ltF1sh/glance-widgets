package com.s4ltf1sh.glance_widgets.network

import com.s4ltf1sh.glance_widgets.model.quotes.PicsumInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PicsumService {
    @GET("id/{id}/info")
    suspend fun getImageInfo(@Path("id") imageId: Int): Response<PicsumInfo>
}