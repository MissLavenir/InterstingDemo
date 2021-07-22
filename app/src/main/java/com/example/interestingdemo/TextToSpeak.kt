package com.example.interestingdemo

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.interestingdemo.extensions.coverToTTSFriendlyString
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.fragment_text_to_speak.*
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class TextToSpeak : Fragment() {
    //TTS语音播报队列
    private val speakQueue = arrayListOf<String>()
    //TTS引擎初始化锁
    private var textToSpeechInitLock = false
    //TTS完成状态
    private var textToSpeechDone = false
    //正在播报的TTS语音
    private var textToSpeech : TextToSpeech? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_to_speak, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textInputLayout.requestFocus()
        startSpeakBtn.setOnClickListener {
            val text = textInput.text.toString()
            if (text.isNotEmpty()){
                if (textToSpeechDone){
                    handleSpeakQueue(text)
                } else {
                    initSpeakText {
                        handleSpeakQueue(textInput.text.toString())
                    }
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
                    initSpeakText {
                        textToFile(text)
                    }
                }
            } else {
                toast("转换文字不能为空")
            }
        }
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

    private fun initSpeakText(block: () -> Unit){
        if (textToSpeechInitLock) return
        if (textToSpeech == null || textToSpeechDone){
            textToSpeechInitLock = true
            textToSpeech = TextToSpeech(context) {
                if (it == TextToSpeech.SUCCESS) {
                    textToSpeech?.let { tts ->
                        textToSpeechInitLock = false
                        textToSpeechDone = true
                        tts.setPitch(1.25f) //音调 数值越小越粗,1.0为默认音调，小米为小爱同学
                        tts.setSpeechRate(1.0f) //语速
                        block.invoke()
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

}