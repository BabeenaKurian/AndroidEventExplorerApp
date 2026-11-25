package com.babeena.myapplication.ui.details

import com.babeena.myapplication.domain.model.Event

data class DetailsUiState(
    val isLoading: Boolean = false, val event: Event? = null, val errorMessage: String? = null
)
