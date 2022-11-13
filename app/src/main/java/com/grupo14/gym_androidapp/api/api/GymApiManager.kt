package com.grupo14.gym_androidapp.api.api

import com.grupo14.gym_androidapp.MainActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class GymApiManager(private val baseUrl: String = MainActivity.BASE_URL) {
    private var authToken: String? = null
    private val authRequestInterceptor = AuthorizationInterceptor()

    fun setAuthToken(newAuthToken: String?) {
        authToken = newAuthToken
        authRequestInterceptor.setAuthToken(authToken);
    }

    fun getAuthToken(): String? {
        return authToken
    }

    private val okHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(authRequestInterceptor)
        .addInterceptor(RequestInterceptor)
        .build()

    private val retrofitClient: Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()

    val gymApi: GymApi = retrofitClient.create(GymApi::class.java)

    private class AuthorizationInterceptor : Interceptor {
        private var authHeader: String? = null

        fun setAuthToken(newAuthToken: String?) {
            authHeader = if (newAuthToken == null) null else "Bearer ${newAuthToken}"
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            if (authHeader == null)
                return chain.proceed(chain.request())

            val requestWithHeader = chain.request()
                .newBuilder()
                .header(
                    "Authorization", authHeader!!
                ).build()
            return chain.proceed(requestWithHeader)

        }
    }

    private object RequestInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request();

            println("Outgoing request to ${request.url} with authorization \"${request.headers["Authorization"]}\"");
            return chain.proceed(request);
        }
    }
}