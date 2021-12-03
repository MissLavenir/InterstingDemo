package com.example.interestingdemo.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.interestingdemo.R
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation

object GlideUtil {
    //正常加载图片
    fun loadImage(context: Context, url: String, imageView: ImageView){
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(imageView)
    }

    //跳过缓存加载图片
    fun loadImageNoCache(context: Context, url: String, imageView: ImageView){
        Glide.with(context)
            .load(url)
            .skipMemoryCache(true)
            .into(imageView)
    }

    //使用占位图加载图片
    fun loadImageHasPlaceHolder(context: Context, url: String, imageView: ImageView){
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.ic_image_load_fail)
            .into(imageView)
    }

    //加载图片并圆角处理
    fun loadImageRadius(context: Context,url: String, imageView: ImageView, radius: Int){
        Glide.with(context)
            .load(url)
            .transform(RoundedCornersTransformation(radius,20, RoundedCornersTransformation.CornerType.ALL))
            .into(imageView)
    }

    //加载图片并圆形处理
    fun loadImageCircle(context: Context,url: String, imageView: ImageView){
        Glide.with(context)
            .load(url)
            .circleCrop()
            .into(imageView)
    }

    //模糊处理
    fun loadImageBlur(context: Context,url: String, imageView: ImageView, radius: Int, sampling : Int){
        Glide.with(context)
            .load(url)
            .transform(BlurTransformation(radius,sampling))
            .into(imageView)
    }

    //素描处理
    fun loadImageSketchFilter(context: Context,url: String, imageView: ImageView){
        Glide.with(context)
            .load(url)
            .transform(SketchFilterTransformation())
            .into(imageView)
    }

    //素描处理
    fun loadImageToonFilter(context: Context,url: String, imageView: ImageView){
        Glide.with(context)
            .load(url)
            .transform(ToonFilterTransformation())
            .into(imageView)
    }

    /**
     * Glide缓存机制
     * 活动缓存->内存缓存->磁盘缓存
     * 加载一张网络图片时先检查活动缓存，有则直接返回，没有则检查内存缓存。
     * 内存缓存有则返回，没有则创建网络请求，但并不会直接请求，而是检查磁盘缓存。
     * 磁盘缓存有则返回，没有则进行请求。
     * 请求回来的图片数据为流数据，先处理成Bitmap位图并进行磁盘缓存。
     * 将Bitmap放入内存缓存,然后转换成图片格式，再将内存缓存的图片。
     * 将内存缓存的图片移入活动缓存中，然后展示在UI上。
     * 当Activity或Fragment不在引用图片时，会将活动缓存的图片移入内存缓存中。
     * 内存缓存采用Lru算法清除资缓存的资源。
     *
     */
    //清理磁盘缓存，不能在主线程进行
    suspend fun clearDiskCache(context: Context){
        Glide.get(context).clearDiskCache()
    }

    //清理内存缓存，可以在主线程进行
    fun clearMemory(context: Context){
        Glide.get(context).clearMemory()
    }

}