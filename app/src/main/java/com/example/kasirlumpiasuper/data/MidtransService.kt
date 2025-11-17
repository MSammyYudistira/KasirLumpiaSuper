//package com.example.kasirlumpiasuper.data
//
//import com.example.kasirlumpiasuper.data.model.MidtransApi
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object MidtransService {
//    private const val BASE_URL = "https://midtrans-backend-c7ey.onrender.com/" // <— URL Render kamu
//
//    private val logger = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }
//
//    private val client = OkHttpClient.Builder()
//        .addInterceptor(logger)
//        .build()
//
//    val api: MidtransApi by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .build()
//            .create(MidtransApi::class.java)
//    }
//}
