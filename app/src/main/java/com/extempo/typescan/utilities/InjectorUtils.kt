package com.extempo.typescan.utilities

import android.content.Context
import com.extempo.typescan.model.repository.DocumentRepository
import com.extempo.typescan.viewmodel.HomeActivityViewModelFactory
import com.extempo.typescan.viewmodel.TextEditorActivityViewModelFactory


object InjectorUtils {
    fun provideHomeActivityViewModelFactory(context: Context): HomeActivityViewModelFactory {
        return HomeActivityViewModelFactory(DocumentRepository(context))
    }
    fun provideTextEditorActivityViewModelFactory(context: Context): TextEditorActivityViewModelFactory {
        return TextEditorActivityViewModelFactory(DocumentRepository(context))
    }
}