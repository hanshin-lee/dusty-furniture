package com.dusty.di

import io.ktor.client.engine.js.Js
import org.koin.dsl.module

actual val platformModule = module {
    single { Js.create() }
}
