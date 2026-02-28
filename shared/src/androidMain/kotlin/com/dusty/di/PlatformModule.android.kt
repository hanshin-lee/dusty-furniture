package com.dusty.di

import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

actual val platformModule = module {
    single { CIO.create() }
}
