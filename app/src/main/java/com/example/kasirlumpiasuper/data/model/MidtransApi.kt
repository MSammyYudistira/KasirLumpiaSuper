package com.example.kasirlumpiasuper.data.model

import retrofit2.http.*

//data class CreatePaymentRequest(
//    val orderId: String,
//    val amount: Int,
//    val customerName: String? = null
//)
//
//data class CreatePaymentResponse(
//    val token: String,
//    val redirectUrl: String
//)
//
data class StatusResponse(
    val status: String
)
//
//interface MidtransApi {
//    @POST("create-payment")
//    suspend fun createPayment(@Body body: CreatePaymentRequest): CreatePaymentResponse
//
//    @GET("status/{orderId}")
//    suspend fun getStatus(@Path("orderId") orderId: String): StatusResponse
//}

data class CreateQrisRequest(
    val orderId: String,
    val amount: Int,
    val customerName: String? = null
)

data class CreateQrisResponse(
    val orderId: String,
    val amount: Int,
    val qrUrl: String,
    val status: String
)

interface MidtransApi {
    @POST("create-qris")
    suspend fun createQris(@Body body: CreateQrisRequest): CreateQrisResponse

    @GET("status/{orderId}")
    suspend fun getStatus(@Path("orderId") orderId: String): StatusResponse
}
