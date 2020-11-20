package com.example.interestingdemo

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.interestingdemo.extensions.coverToTTSFriendlyString
import com.example.interestingdemo.extensions.toast
import kotlinx.android.synthetic.main.fragment_text_to_speak.*
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
            speakText(textInput.text.toString())
        }
    }

    private fun speakText(text : String){
        speakQueue.add(text.coverToTTSFriendlyString())
        if (textToSpeechInitLock) return
        if (textToSpeech == null || textToSpeechDone){
            textToSpeechInitLock = true
            textToSpeech = TextToSpeech(context,TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS){
                    textToSpeech?.let {
                        textToSpeechInitLock = false
                        textToSpeechDone = true
                        handleSpeakQueue()
                    }
                }
            })
        }else if (textToSpeechDone){
            handleSpeakQueue()
        }
    }
    private fun handleSpeakQueue(){
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