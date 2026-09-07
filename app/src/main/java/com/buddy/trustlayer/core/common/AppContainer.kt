package com.buddy.trustlayer.core.common

import com.buddy.trustlayer.data.remote.TrustEngineApi
import com.buddy.trustlayer.data.repository.MockTrustEngineRepository
import com.buddy.trustlayer.data.repository.RemoteTrustEngineRepository
import com.buddy.trustlayer.domain.repository.TrustEngineRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Dependency Injection Container.
 */
object AppContainer {

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(AppConfig.BACKEND_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val trustEngineApi: TrustEngineApi
        get() = retrofit.create(TrustEngineApi::class.java)
    
    private val mockRepository by lazy { MockTrustEngineRepository() }
    
    val trustEngineRepository: TrustEngineRepository
        get() = if (AppConfig.USE_MOCK_ENGINE) mockRepository else RemoteTrustEngineRepository(trustEngineApi)
}
