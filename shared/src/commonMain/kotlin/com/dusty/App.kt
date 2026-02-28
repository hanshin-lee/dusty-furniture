package com.dusty

import androidx.compose.runtime.Composable
import com.dusty.di.appModule
import com.dusty.di.platformModule
import com.dusty.presentation.navigation.DustyNavGraph
import com.dusty.presentation.theme.DustyTheme
import org.koin.compose.KoinApplication

@Composable
fun DustyApp() {
    KoinApplication(application = {
        modules(appModule, platformModule)
    }) {
        DustyTheme {
            DustyNavGraph()
        }
    }
}
