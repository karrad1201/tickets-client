package com.karrad.ticketsclient.data.api

class ApiException(message: String, val statusCode: Int = 0) : Exception(message)
