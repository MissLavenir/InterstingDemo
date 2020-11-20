package com.example.interestingdemo.extensions

fun String.coverToTTSFriendlyString(): String{
    var result = this
    val noRegex = Regex("【[0-9]+】")
    result = noRegex.replace(result){
        it.value.replaceArabicNumber2ChineseNumberForTTS()
    }
    result = result.replace("￥","元")
    return result
}

fun String.replaceArabicNumber2ChineseNumberForTTS():String{
    var resultString = this
    resultString = resultString.replace("0","零，")
    resultString = resultString.replace("1","幺，")
    resultString = resultString.replace("2","二，")
    resultString = resultString.replace("3","三，")
    resultString = resultString.replace("4","四，")
    resultString = resultString.replace("5","五，")
    resultString = resultString.replace("6","六，")
    resultString = resultString.replace("7","七，")
    resultString = resultString.replace("8","八，")
    resultString = resultString.replace("9","九，")
    return resultString
}