package com.karrad.ticketsclient

import com.karrad.ticketsclient.di.AppContainer
import com.karrad.ticketsclient.di.initReal

fun initContainer(baseUrl: String) {
    AppContainer.initReal(baseUrl)
}
