package com.example.interestingdemo.Util

import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * 提供精确算法的工具
 */
class BigDecimalUtil {

    companion object{
        /**
         * 提供精确的加法运算。
         *
         * @param v1 被加数
         * @param v2 加数
         * @return 两个参数的和
         */
        fun add(v1: Double, v2: Double): Double{
            val d1 = BigDecimal(v1)
            val d2 = BigDecimal(v2)
            return d1.add(d2).toDouble()
        }

        /**
         * 提供精确的减法运算。
         *
         * @param v1 被减数
         * @param v2 减数
         * @return 两个参数的差
         */
        fun sub(v1: Double, v2: Double): Double{
            val d1 = BigDecimal(v1)
            val d2 = BigDecimal(v2)
            return d1.subtract(d2).toDouble()
        }

        /**
         * 提供精确的乘法运算。
         *
         * @param v1 被乘数
         * @param v2 乘数
         * @return 两个参数的积
         */
        fun mul(v1: Double, v2: Double): Double{
            val d1 = BigDecimal(v1)
            val d2 = BigDecimal(v2)
            return d1.multiply(d2).toDouble()
        }

        /**
         * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
         * 定精度，以后的数字四舍五入。
         *
         * @param v1    被除数
         * @param v2    除数
         * @param scale 表示表示需要精确到小数点以后几位。
         * @return 两个参数的商
         */
        fun div(v1: Double, v2: Double, scale: Int): Double{
            val d1 = BigDecimal(v1)
            val d2 = BigDecimal(v2)
            return d1.divide(d2, scale, BigDecimal.ROUND_HALF_UP).toDouble()
        }

        /**
         * 提供精确的小数位四舍五入处理。但是会过滤掉小数点后的0
         *
         * @param v     需要四舍五入的数字
         * @param scale 小数点后保留几位
         * @return 四舍五入后的结果
         */
        fun round(v: Double, scale: Int): Double{
            if (scale < 0){
                throw IllegalArgumentException("The scale must be a positive integer or zero")
            }
            val d1 = BigDecimal(v)
            return d1.setScale(scale, BigDecimal.ROUND_HALF_UP).toDouble()
        }

        /**
         * double ==> string 取整
         */
        fun roundToString(v1: Double): String {
            val d1 = DecimalFormat("#")
            return d1.format(v1)
        }


        /**
         * string ==> string 保留小数点位数
         *
         * @throws NumberFormatException 报错返回原值
         */
        fun roundToString(string: String, scale: Int): String{
            return try {
                val d1 = BigDecimal(string)
                d1.setScale(scale, BigDecimal.ROUND_HALF_UP).toString()
            } catch (e: NumberFormatException){
                e.printStackTrace()
                string
            }
        }

    }

}