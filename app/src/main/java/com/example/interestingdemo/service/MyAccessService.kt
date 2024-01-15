package com.example.interestingdemo.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi

class MyAccessService: AccessibilityService() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.eventType?.let {
            when(it){
                //获取到事件后想做的事情
                AccessibilityEvent.TYPE_ANNOUNCEMENT-> Log.e("debugService", "应用程序发布公告的事件")
                AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> Log.e("debugService", "View的焦点")
                AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED -> Log.e("debugService", "View的焦点清除")
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> Log.e("debugService", "通知栏状态更新")
                AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> Log.e("debugService", "View的鼠标悬停选中")
                AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> Log.e("debugService", "View的鼠标悬停离开")
                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> Log.e("debugService", "开始触摸探索手势的事件")
                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> Log.e("debugService", "结束触摸探索手势的事件")
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> Log.e("debugService", "滚动类View")
                AccessibilityEvent.TYPE_VIEW_SELECTED -> Log.e("debugService", "表示通常在android.widget.AdapterView的上下文中选择项的事件")
                AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> Log.e("debugService", "EditText视图选中内容改变")
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> Log.e("debugService", "EditText视图内容改变")
                AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY -> Log.e("debugService", "表示以给定的移动粒度遍历视图文本的事件")
                AccessibilityEvent.TYPE_VIEW_CLICKED -> Log.e("debugService", "点击事件")
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> Log.e("debugService", "长按点击事件")
                AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED -> Log.e("debugService", "表示在android.view.View上的上下文单击事件")
                AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> Log.e("debugService", "开始手势检测")
                AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> Log.e("debugService", "结束手势检测")
                AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> Log.e("debugService", "表示用户开始触摸屏幕的事件")
                AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> Log.e("debugService", "表示用户结束触摸屏幕的事件")
                AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT -> Log.e("debugService", "表示助手当前正在读取用户屏幕上下文的事件")
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> { Log.e("debugService", "UI内容更新")
                    rootInActiveWindow?.findAccessibilityNodeInfosByText("跳过")?.forEach { info ->

                        Log.e("debugService", "child跳过：id=${info.windowId};info=${info}")
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK)

//                        for (i in 0 until info.parent.childCount){
//                            val child = info.parent.getChild(i)
//                            if (child.hintText == "请输入"){
//                                val arg = Bundle().apply { putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,"输入内容") }
//                                Log.e("debugService", "id=${child.windowId};info=${child}")
//                                child.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arg)
//                                continue
//                            }
//                            if (child.text == "确认"){
//                                Log.e("debugService", "child确认：id=${info.windowId};info=${info}")
//                                child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
//                            }
//                        }
                    }
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> Log.e("debugService", "布局窗口更改（如弹框出现，弹框消失，Activity布局创建，Activity布局消失）")
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> Log.e("debugService", "焦点更改")
                else -> Log.e("debugService", "其他事件$it")
            }
        }

    }

    override fun onInterrupt() {
        Log.e("debugService", "service onInterrupt")

    }

    override fun onServiceConnected() {
//        val info = AccessibilityServiceInfo().apply {
//            packageNames = arrayOf("com.ruigu.saler")
//            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
//            feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
//            notificationTimeout = 100
////            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
//        }
//        serviceInfo = info
        Log.e("debugService", "service onServiceConnected")
        super.onServiceConnected()
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("debugService", "service onCreate")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e("debugService", "service onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.e("debugService", "service onDestroy")
        super.onDestroy()
    }
}