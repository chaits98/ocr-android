package com.extempo.typescan.utilities

import android.content.Context
import com.extempo.typescan.model.repository.DocumentRepository
import com.extempo.typescan.viewmodel.HomeActivityViewModelFactory


object InjectorUtils {
    fun provideHomeActivityViewModelFactory(context: Context): HomeActivityViewModelFactory {
        return HomeActivityViewModelFactory(DocumentRepository(context))
    }
}