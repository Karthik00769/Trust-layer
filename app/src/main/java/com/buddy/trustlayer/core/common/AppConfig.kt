package com.buddy.trustlayer.core.common

object AppConfig {
    /**
     * Set to true to use the local mock engine.
     * Set to false to use the remote Python backend.
     */
    var USE_MOCK_ENGINE = false
    
    /**
     * The base URL of the Python backend.
     * For emulator to local host, use 10.0.2.2.
     */
    var BACKEND_BASE_URL = "http://10.0.2.2:8000/"
}
