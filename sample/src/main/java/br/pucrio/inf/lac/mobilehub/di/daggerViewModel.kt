package br.pucrio.inf.lac.mobilehub.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Suppress("UNCHECKED_CAST")
@Composable
internal inline fun <reified T : ViewModel> daggerViewModel(
    crossinline viewModelInstanceCreator: () -> T,
    key: String? = null
): T = viewModel(
    modelClass = T::class.java,
    key = key,
    factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            viewModelInstanceCreator() as T
    }
)
