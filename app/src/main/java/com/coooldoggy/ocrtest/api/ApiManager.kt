package com.coooldoggy.ocrtest.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.coooldoggy.ocrtest.KAKAO_KEY
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


object ApiManager {
    private val TAG = ApiManager::class.java.simpleName

    fun getKakaoBoxes(fileUri: String): Observable<DataModel.boxResult> {
        val kakaoBoxService = KakaoVisionApiService.createKakaoOCR()
        scaleDown(fileUri)
        val file = File(fileUri)
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)

        return kakaoBoxService.getKakaoBoxresult(KAKAO_KEY, requestBody)
    }

    fun getKakaoOCR(fileUri: String, box: String): Observable<DataModel.ocrResult> {
        val kakaoOcrService = KakaoVisionApiService.createKakaoOCR()
        val file = File(fileUri)
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        return kakaoOcrService.getKakaoOCRresult(KAKAO_KEY, requestBody, box)
    }

    fun scaleDown(path: String) {
        var photo = BitmapFactory.decodeFile(path)
        photo = Bitmap.createScaledBitmap(photo, 1000, 1000, false)
        var bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 80, bytes)

        var file = File(path)
        file.createNewFile()
        var output = FileOutputStream(file)
        output.write(bytes.toByteArray())
        output.close()

    }
}