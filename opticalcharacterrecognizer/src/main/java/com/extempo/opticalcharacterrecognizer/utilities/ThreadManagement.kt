package com.extempo.opticalcharacterrecognizer.utilities

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ThreadManagement {
    val inferenceExecutor: ExecutorService = Executors.newSingleThreadExecutor()
}