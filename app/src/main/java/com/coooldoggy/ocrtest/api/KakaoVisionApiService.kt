package com.coooldoggy.ocrtest.api

import android.util.Log
import com.coooldoggy.ocrtest.*
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface KakaoVisionApiService {

    @POST(KAKAO_OCR_SUB_URL)
    @Multipart
    fun getKakaoOCRresult(@Header("Authorization") key: String,@Part("file\"; filename=\"photo.jpg\"") file: RequestBody, @Query("boxes")box: String) : Observable<DataModel.ocrResult>

    @POST(KAKAO_BOX_SUB_URL)
    @Multipart
    fun getKakaoBoxresult(@Header("Authorization") key: String, @Part("file\"; filename=\"photo.jpg\"") file: RequestBody) : Observable<DataModel.boxResult>

    companion object{

        fun createKakaoOCR(): KakaoVisionApiService{
            return getKakaoOCRApiService().create(KakaoVisionApiService::class.java)
        }

        private fun getKakaoOCRApiService() : Retrofit {
            return Retrofit.Builder()
                .client(provideOkHttpClient(provideLoggingInterceptor()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(KAKAO_BASE_URL)
                .build()
        }


        private fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
            val b = OkHttpClient.Builder()
            b.addInterceptor(interceptor)
            return b.build()
        }


        private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
            val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("DEBUG-API-LOG", it)
            })
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            return interceptor
        }

    }
}