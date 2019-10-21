package com.extempo.typescan

import com.extempo.typescan.factory.DataFactory.Factory.randomUuid
import com.extempo.typescan.model.DocumentItem


class DocumentFactory {
    companion object {
        fun makeDocumentItem(): DocumentItem {
            return DocumentItem(randomUuid(), randomUuid())
        }

        fun makeDocumentList(count: Int): List<DocumentItem> {
            val documentItemList = mutableListOf<DocumentItem>()
            repeat(count) {
                documentItemList.add(makeDocumentItem())
            }
            return documentItemList
        }
    }
}