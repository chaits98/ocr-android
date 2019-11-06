package com.extempo.typescan.utilities

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ThreadManagement {
    val databaseExecutor: ExecutorService = Executors.newSingleThreadExecutor()
}