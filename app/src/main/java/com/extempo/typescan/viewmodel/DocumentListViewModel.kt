package com.extempo.typescan.viewmodel

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.extempo.typescan.R
import com.extempo.typescan.model.DocumentItem
import com.extempo.typescan.model.repository.DocumentRepository
import kotlinx.android.synthetic.main.activity_home.*

class DocumentListViewModel(private val documentRepository: DocumentRepository): ViewModel() {

    fun getAlldocumentItems(): LiveData<PagedList<DocumentItem>>? = documentRepository.getAllDocumentItems()

    fun addFragment(fragmentManager: FragmentManager, fragment: Fragment, fragmentTag: String) {
        fragmentManager
            .beginTransaction()
            .add(R.id.home_frame_layout, fragment, fragmentTag)
            .commit()
    }

    fun replaceFragment(fragmentManager: FragmentManager, fragment: Fragment, fragmentTag: String) {
        fragmentManager
            .beginTransaction()
            .replace(R.id.home_frame_layout, fragment, fragmentTag)
            .commit()
    }

    fun removeFragmentWithID(fragmentManager: FragmentManager, fragmentID: String) {
        val removeFragment = fragmentManager.findFragmentByTag(fragmentID)
        if (removeFragment != null){
            fragmentManager.beginTransaction().remove(removeFragment).commitAllowingStateLoss()
        } else {
            Log.d("log_tag", "not found")
        }
    }
}