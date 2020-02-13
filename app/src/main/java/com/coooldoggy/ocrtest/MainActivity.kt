package com.coooldoggy.ocrtest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.loader.content.CursorLoader
import com.coooldoggy.ocrtest.api.ApiManager
import com.coooldoggy.ocrtest.utils.*
import com.coooldoggy.ocrtest.utils.RealPathUtil.getRealPathFromURIAPI19
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.googlecode.tesseract.android.TessBaseAPI
import java.net.URI


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private var mCameraPath: String? = null
    var copyFile: FileCopyUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setResources()
    }

    private fun setResources() {
        btnGoogle.setOnClickListener {
            if (!getCheckPermission(perms)){
                    requestPermissions(this,
                        perms,
                        PERM_REQUEST_CODE_GOOGLE
                    )
            } else {
                takePicture(CAMERA_REQUEST_CODE_GOOGLE)
            }
        }
        btnKakao.setOnClickListener {
            if (!getCheckPermission(perms)){
                requestPermissions(this,
                    perms,
                    PERM_REQUEST_CODE_KAKAO
                )
            } else {
                takePicture(CAMERA_REQUEST_CODE_KAKAO)
            }
        }
        btnGoogleCloud.setOnClickListener {
            if (!getCheckPermission(perms)){
                requestPermissions(this,
                    perms,
                    PERM_REQUEST_CODE_GCP
                )
            } else {
                takePicture(CAMERA_REQUEST_CODE_GCP)
            }
        }
        btnTesseract.setOnClickListener {
            if (!getCheckPermission(perms)){
                requestPermissions(this,
                    perms,
                    PERM_REQUEST_CODE_TESS
                )
            } else {
                takePicture(CAMERA_REQUEST_CODE_TESS)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERM_REQUEST_CODE_GOOGLE -> {
                if (grantResults.isNotEmpty()){
                    if (getCheckPermission(perms)){
                        takePicture(CAMERA_REQUEST_CODE_GOOGLE)
                    }
                }
            }
            PERM_REQUEST_CODE_KAKAO -> {
                if (grantResults.isNotEmpty()){
                    if (getCheckPermission(perms)){
                        takePicture(CAMERA_REQUEST_CODE_KAKAO)
                    }
                }
            }
            PERM_REQUEST_CODE_GCP -> {
                if (grantResults.isNotEmpty()){
                    if (getCheckPermission(perms)){
                        takePicture(CAMERA_REQUEST_CODE_GCP)
                    }
                }
            }
            PERM_REQUEST_CODE_TESS -> {
                if (grantResults.isNotEmpty()){
                    if (getCheckPermission(perms)){
                        takePicture(CAMERA_REQUEST_CODE_TESS)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CAMERA_REQUEST_CODE_GOOGLE ->{
                if (data?.data == null){
                    googleAnalyze(mCameraPath!!)
                    start()
                }else{
                    val dataString = data?.data!!
                    val filpath = getRealPathFromURIAPI19(this, dataString)
                    start()
                    googleAnalyze(filpath!!)
                }
            }
            CAMERA_REQUEST_CODE_KAKAO -> {
                if (data?.data == null){
                    kakaoAnalyzeBox(mCameraPath!!)
                    start()
                }else{
                    val dataString = data?.data!!
                    val filpath = getRealPathFromURIAPI19(this, dataString)
                    start()
                    kakaoAnalyzeBox(filpath!!)
                }
            }
            CAMERA_REQUEST_CODE_GCP -> {
                if (data?.data == null){
                    gcpAnalyze(mCameraPath!!)
                    start()
                }else{
                    val dataString = data?.data!!
                    val filpath = getRealPathFromURIAPI19(this, dataString)
                    start()
                    gcpAnalyze(filpath!!)
                }
            }
            CAMERA_REQUEST_CODE_TESS ->{
                if (data?.data == null){
                    tessAnalyze(mCameraPath!!)
                    start()
                }else{
                    val dataString = data?.data!!
                    val filpath = getRealPathFromURIAPI19(this, dataString)
                    start()
                    tessAnalyze(filpath!!)
                }
            }
        }
    }


    private fun tessAnalyze(path: String){
        var dataPath: String = filesDir.path +"/tesseract/"
        checkFile(File(dataPath+"tessdata/"), "kor")
        checkFile(File(dataPath+"tessdata/"), "eng")
        var lang = "kor"
        var tess = TessBaseAPI()
        tess.init(dataPath, lang)
        tess.setImage(File(path))
        tv_result.text = tess.utF8Text
    }

    private fun gcpAnalyze(path: String){
        val image: FirebaseVisionImage = FirebaseVisionImage.fromFilePath(this, Uri.fromFile(File(path)))
        val options = FirebaseVisionCloudTextRecognizerOptions.Builder().setLanguageHints(listOf("ko")).build()
        val dectector = FirebaseVision.getInstance().getCloudTextRecognizer(options)
        val result = dectector.processImage(image).addOnSuccessListener {
            stop()
            Log.d(TAG, it.text)
            tv_result.text = it.text
            tv_timer.text = getMilliSeconds().toString()
        }.addOnFailureListener { e->
            Log.d(TAG, e.message)
        }
    }

    private fun kakaoAnalyzeBox(path: String){
        ApiManager.getKakaoBoxes(path).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.d(TAG, "type= ${it.result.boxes}")
                kakaoAnalyzeOcr(path, "${it.result.boxes}")
            }
            .subscribe({
                Log.d(TAG, "subscribe" + it.result)
            }, {t: Throwable? ->
                Log.e(TAG, "kakaoAnalyzeBox"+t?.message.toString())
            })
    }

    private fun kakaoAnalyzeOcr(path: String, box: String){
        ApiManager.getKakaoOCR(path, box).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers. mainThread())
            .subscribe ({
                stop()
                tv_result.text = it.result.get("recognition_words").asString
                tv_timer.text = getMilliSeconds().toString()
            },{t: Throwable? ->
                Log.e(TAG, "kakaoAnalyzeOcr"+t?.message.toString())
                tv_result.text = "사진이 인식되지 않습니다. 다시 촬영해주세요."
            })
    }

    private fun googleAnalyze(path: String){
        val image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(this, Uri.fromFile(File(path)))
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
            val result = detector.processImage(image)
                .addOnSuccessListener {
                    stop()
                    Log.d(TAG, it.text)
                    tv_result.text = it.text
                    tv_timer.text = getMilliSeconds().toString()
                }.addOnFailureListener {
                    Log.d(TAG, it.message)
                }
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun takePicture(code: Int){
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT, null)
        galleryIntent.type = "image/*"
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val chooser = Intent(Intent.ACTION_CHOOSER)
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent)
        chooser.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
        var intentArray = arrayOf(cameraIntent)
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

        if (cameraIntent.resolveActivity(this.packageManager) != null){
            var photoFile: File? = null
            photoFile = createImageFile()
            cameraIntent.putExtra("PhotoPath", mCameraPath)
            if (photoFile != null) {
                mCameraPath = photoFile.absolutePath
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
        }
            when(code){
                CAMERA_REQUEST_CODE_GOOGLE ->{
                    this.startActivityForResult( chooser,CAMERA_REQUEST_CODE_GOOGLE)
                }
                CAMERA_REQUEST_CODE_KAKAO -> {
                    this.startActivityForResult(chooser, CAMERA_REQUEST_CODE_KAKAO)
                }
                CAMERA_REQUEST_CODE_GCP->{
                    this.startActivityForResult(chooser, CAMERA_REQUEST_CODE_GCP)
                }
                CAMERA_REQUEST_CODE_TESS->{
                    this.startActivityForResult(chooser, CAMERA_REQUEST_CODE_TESS)
                }
            }
        }
    }


    private fun checkFile(dir: File, lang: String){
        if (!dir.exists() && dir.mkdirs()){
            var fileUtil = FileCopyUtils()
            fileUtil.copyFiles(lang, this)
        }
        if (dir.exists()){
            var dataPath: String = filesDir.path +"/tesseract/"
            var datafilePath = "$dataPath/tessdata/$lang.traineddata"
            var datafile = File(datafilePath)
            if (!datafile.exists()){
                var fileUtil = FileCopyUtils()
                fileUtil.copyFiles(lang, this)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
}
