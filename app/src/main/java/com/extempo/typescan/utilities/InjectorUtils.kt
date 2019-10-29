package com.extempo.typescan.utilities

import com.extempo.typescan.viewmodel.SelectionActivityViewModelFactory


object InjectorUtils {
    fun provideSelectionActivityViewModelFactory(): SelectionActivityViewModelFactory {
        return SelectionActivityViewModelFactory()
    }
}