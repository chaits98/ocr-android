package com.extempo.typescan.utilities


object ModuleLoader {
    private var done = false

    @Synchronized
    fun load() {
        if (done) {
            return
        }
        System.loadLibrary("opencv_java4")
        done = true
    }
}