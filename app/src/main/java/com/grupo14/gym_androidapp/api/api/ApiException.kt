package com.grupo14.gym_androidapp.api.api

import retrofit2.Response

class ApiException(
    val response: Response<*>? = null,
    cause: Throwable? = null
) : Exception(response?.message(), cause) {

}