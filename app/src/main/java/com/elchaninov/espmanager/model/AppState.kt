package com.elchaninov.espmanager.model

import com.elchaninov.espmanager.model.ms.MsModel

sealed class AppState{
    data class Success(val msModel: MsModel) : AppState()
    data class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}
