package com.example.data.remote.api

import com.example.data.remote.dto.ApiResponse
import com.example.data.remote.dto.BookingRequestDto
import com.example.data.remote.dto.BookingResponseDto
import com.example.data.remote.dto.MenuItemDto
import com.example.data.remote.dto.OfferDto
import com.example.data.remote.dto.RestaurantDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DineReserveApiService {

    @GET("api/v1/restaurants")
    suspend fun getRestaurants(
        @Query("query") query: String? = null,
        @Query("cuisine") cuisine: String? = null,
        @Query("tag") categoryTag: String? = null
    ): Response<ApiResponse<List<RestaurantDto>>>

    @GET("api/v1/restaurants/{id}")
    suspend fun getRestaurantDetail(
        @Path("id") id: String
    ): Response<ApiResponse<RestaurantDto>>

    @GET("api/v1/restaurants/{id}/menu")
    suspend fun getRestaurantMenu(
        @Path("id") id: String
    ): Response<ApiResponse<List<MenuItemDto>>>

    @GET("api/v1/offers")
    suspend fun getOffers(): Response<ApiResponse<List<OfferDto>>>

    @POST("api/v1/bookings")
    suspend fun createBooking(
        @Body bookingRequest: BookingRequestDto
    ): Response<ApiResponse<BookingResponseDto>>
}
