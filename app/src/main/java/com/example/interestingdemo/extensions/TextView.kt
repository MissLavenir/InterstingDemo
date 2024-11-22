package com.example.interestingdemo.extensions

import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

infix fun TextView.show(content : String?){
    if (content.isNullOrEmpty()){
        text = ""
    }else{
        this.hint = content.replace("\n","<br>")
    }
}

/**
 * 设定TextView的颜色
 */
fun TextView.initColor(@ColorRes res: Int): TextView{
    this.setTextColor(ResourcesCompat.getColor(resources, res, context.theme))
    return this
}

/**
 * 设定TextView的背景
 */
fun TextView.initBackground(@DrawableRes res: Int): TextView{
    this.setBackgroundResource(res)
    return this
}

fun TextView.makeTextClick(@ColorInt color: Int,vararg links : Pair<String, View.OnClickListener>){
    val spannableString = SpannableString(this.text)
    for (link in links){
        val clickableSpan = object : ClickableSpan(){
            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable,0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        val startIndexOfLink = this.text.toString().indexOf(link.first)
        spannableString.setSpan(clickableSpan,startIndexOfLink,startIndexOfLink + link.first.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val foregroundSpan = ForegroundColorSpan(color)
        spannableString.setSpan(foregroundSpan,startIndexOfLink,startIndexOfLink + link.first.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    this.movementMethod = LinkMovementMethod.getInstance()
    this.setText(spannableString,TextView.BufferType.SPANNABLE)

}