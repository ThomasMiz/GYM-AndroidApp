package com.grupo14.gym_androidapp.api.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.85.29:8080/api/"

    private val okHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(RetrofitAuthorizationInterceptor)
        .addInterceptor(RetrofitRequestInterceptor)
        .build()

    fun getClient(): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
}

object RetrofitRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request();
        println("Outgoing request to ${request.url()}");
        return chain.proceed(request);
    }
}

object RetrofitAuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithHeader = chain.request()
            .newBuilder()
            .header(
                "Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjksImlhdCI6MTY2ODI2MTk5NzQwNiwiZXhwIjoxNjY4MjY0NTg5NDA2fQ.-OjnvkmF3qy69SSy-GN1V1RuRNUl8ziU9hKKawu3vAE"
            ).build()
        return chain.proceed(requestWithHeader)

    }
}