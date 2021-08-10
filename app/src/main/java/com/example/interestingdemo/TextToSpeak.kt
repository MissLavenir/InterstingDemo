package com.example.interestingdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.RecognitionListener
import android.speech.RecognitionService
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.example.interestingdemo.Util.DialogUtil
import com.example.interestingdemo.extensions.coverToTTSFriendlyString
import com.example.interestingdemo.extensions.dLog
import com.example.interestingdemo.extensions.eLog
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.fragment_text_to_speak.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class TextToSpeak : Fragment(),EasyPermissions.PermissionCallbacks {
    //TTS语音播报队列
    private val speakQueue = arrayListOf<String>()
    //TTS引擎初始化锁
    private var textToSpeechInitLock = false
    //TTS完成状态
    private var textToSpeechDone = false
    //正在播报的TTS语音
    private var textToSpeech : TextToSpeech? = null
    //机器人动画
    private lateinit var rotate: Animation
    //语音识别
    private var speechRecognizer: SpeechRecognizer? = null
    //是否正在识别
    private var isSpeech = false

    companion object{
        const val REQUEST_AUDIO = 1010
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_to_speak, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //初始化动画
        rotate = AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_robot).apply {
            repeatMode = Animation.REVERSE
            repeatCount = -1
            duration = 1000
            interpolator = LinearInterpolator()
        }
        initFun()

        textInputLayout.requestFocus()
        startSpeakBtn.setOnClickListener {
            val text = textInput.text.toString()
            if (text.isNotEmpty()){
                if (textToSpeechDone){
                    handleSpeakQueue(text)
                } else {
                    toast("语音初始化失败,正在重新初始化")
                    initFun()
                }
            } else {
                toast("转换文字不能为空")
            }
        }

        getFileBtn.setOnClickListener {
            val text = textInput.text.toString()
            if (text.isNotEmpty()){
                if (textToSpeechDone){
                    textToFile(text)
                } else {
                    toast("语音初始化失败，正在重新初始化")
                    initFun()
                }
            } else {
                toast("转换文字不能为空")
            }
        }
    }

    private fun initFun(){
        initSpeakText()
        initSpeech()
    }

    private fun textToFile(text: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var filePath = ""
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()){
                val cacheFile = ContextCompat.getExternalCacheDirs(requireContext())
                if (cacheFile.isNotEmpty() && cacheFile[0] != null){
                    filePath += cacheFile[0].absolutePath + File.separator + "tts_sound"
                }
            } else {
                filePath += context?.cacheDir?.absolutePath + File.separator + "tts_sound"
            }
            val file = File(filePath)
            if (!file.exists()){
                file.mkdirs()
            }
            textToSpeech?.synthesizeToFile(text, null, File("${filePath}/sound${System.currentTimeMillis()}.mp3"), "record")?.let {
                toast("已成功保存在${filePath}文件夹下")
            }

        } else {
            toast("手机不支持此功能")
        }
    }

    private fun initSpeakText(){
        if (textToSpeechInitLock) return
        if (textToSpeech == null || textToSpeechDone){
            textToSpeechInitLock = true
            textToSpeech = TextToSpeech(context) {
                when (it) {
                    TextToSpeech.SUCCESS -> {
                        textToSpeech?.let { tts ->
                            textToSpeechInitLock = false
                            textToSpeechDone = true
                            tts.setPitch(1.25f) //音调 数值越小越粗,1.0为默认音调，小米为小爱同学
                            tts.setSpeechRate(0.8f) //语速
                            tts.setOnUtteranceProgressListener(object: UtteranceProgressListener(){
                                override fun onStart(p0: String?) {
                                    setAIAction(true)
                                }

                                override fun onDone(p0: String?) {
                                    setAIAction(false)
                                }

                                override fun onError(p0: String?) {
                                    toast("语音出现错误了")
                                }

                            })
                            toast("语音初始化完成")
                        }
                    }
                    else -> {
                        toast("语音初始化出现问题")
                    }
                }
            }
        }
    }
    private fun handleSpeakQueue(text: String){
        speakQueue.add(text.coverToTTSFriendlyString())
        speakQueue.forEach {
            callTextToSpeechToTalk(it)
        }
        speakQueue.clear()
    }

    private fun callTextToSpeechToTalk(text : String){
        textToSpeech?.let { speech ->
            if (speech.isLanguageAvailable(Locale.SIMPLIFIED_CHINESE) >= TextToSpeech.LANG_AVAILABLE){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    val params = Bundle()
                    params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM,AudioManager.STREAM_NOTIFICATION)
                    params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME,1f)
                    speech.speak(text,TextToSpeech.QUEUE_ADD,params,text)
                }else{
                    val params = HashMap<String,String>()
                    params[TextToSpeech.Engine.KEY_PARAM_STREAM] = AudioManager.STREAM_NOTIFICATION.toString()
                    params[TextToSpeech.Engine.KEY_PARAM_VOLUME] = "1.0"
                    @Suppress("DEPRECATION") // 针对旧版本
                    speech.speak(text,TextToSpeech.QUEUE_ADD,params)
                }
            }else{
                toast("此设备不支持语音播报")
            }
        }

    }

    @SuppressLint( "QueryPermissionsNeeded")
    private fun initSpeech(){
        //初始化语音识别
        if (SpeechRecognizer.isRecognitionAvailable(requireContext())){
            speechRecognizer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val list = context?.packageManager?.queryIntentServices(Intent(RecognitionService.SERVICE_INTERFACE),PackageManager.MATCH_ALL)
                if (!list.isNullOrEmpty()){
                    val cmp = ComponentName(list[0].serviceInfo.packageName, list[0].serviceInfo.name)
                    SpeechRecognizer.createSpeechRecognizer(requireContext(), cmp)
                } else {
                    SpeechRecognizer.createSpeechRecognizer(requireContext())
                }
            } else {
                SpeechRecognizer.createSpeechRecognizer(requireContext())

            }
//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
            speechRecognizer?.let { sr ->
                dLog("debug_speechRecognizer", "语音识别已初始化")
                sr.setRecognitionListener(object : RecognitionListener {

                    var audioPartText = ""
                    override fun onReadyForSpeech(p0: Bundle?) {
                        dLog("speechRecognizer","语音识别准备就绪")
                    }

                    override fun onBeginningOfSpeech() {
                        dLog("speechRecognizer","语音识别开始")
                        setAIAction(true)
                        getSpeechText.hint = "正在识别..."
                        isSpeech = true
                    }

                    override fun onRmsChanged(p0: Float) {
                        dLog("speechRecognizer","分贝值大小为:$p0")
                    }

                    override fun onBufferReceived(p0: ByteArray?) {
                    }

                    override fun onEndOfSpeech() {
                        dLog("speechRecognizer","语音识别结束")
                        isSpeech = false
                        setAIAction(false)
                        getSpeechText.hint = "点击机器人开始识别语音"
                    }

                    override fun onError(p0: Int) {
                        setAIAction(false)
                        getSpeechText.hint = "点击机器人开始识别语音"
                        eLog("error", "语音识别出现错误了，错误码:$p0")
                        when(p0){
                            SpeechRecognizer.ERROR_NO_MATCH -> {
                                toast("未监听到声音")
                            }
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                                toast("没有语音识别权限，错误码$p0")
                            }
                            SpeechRecognizer.ERROR_NETWORK,SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                                toast("网络错误，错误码$p0")
                            }
                            SpeechRecognizer.ERROR_CLIENT -> {
                                dLog("error","客户端错误，错误码$p0")
                            }
                            SpeechRecognizer.ERROR_SERVER -> {
                                toast("服务错误，错误码$p0")
                            }
                            SpeechRecognizer.ERROR_AUDIO -> {
                                toast("语音识别错误，错误码$p0")
                            }
                        }
                    }

                    override fun onResults(p0: Bundle?) {
                        dLog("speechRecognizer","语音识别结果：${p0.toString()}")
                        val results = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: ArrayList()
                        if (results.size > 0){
                            getSpeechText.text = results[0]
                            dLog("result",results[0])
                        } else {
                            getSpeechText.text = audioPartText
                        }

                    }

                    override fun onPartialResults(p0: Bundle?) {
                        dLog("speechRecognizer","语音识别部分结果：${p0.toString()}")
                        val results = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: ArrayList()
                        if (results.size > 0){
                            audioPartText = results[0]
                            dLog("partial_result",results[0])
                        }
                    }

                    override fun onEvent(p0: Int, p1: Bundle?) {
                        dLog("speechRecognizer","语音识别事件：${p0}__${p1.toString()}")
                    }
                })
            } ?: kotlin.run {
                toast("无法使用系统语音识别功能")
            }

        } else {
            toast( "没有语音识别可用")
        }

        ttsAI.setOnClickListener {
            if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.RECORD_AUDIO) && !isSpeech){
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)//识别模式->自由识别
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true)//返回部分结果
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,10)//最大返回结果数量
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.SIMPLIFIED_CHINESE)
                }
                speechRecognizer?.startListening(intent)
            } else {
                dLog("debug_speechRecognizer","没有语音权限")
                EasyPermissions.requestPermissions(this,"需要语音权限才可以进行语音输入", REQUEST_AUDIO,Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun setAIAction(boolean: Boolean){
        if (boolean){
            ttsAI.startAnimation(rotate)
        } else {
            ttsAI.animation = null
        }
    }

    override fun onPause() {
        super.onPause()
        speechRecognizer?.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,this)

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        DialogUtil.showCommonDialog(requireContext(),"提示","未拥有语音助手权限")
    }

}