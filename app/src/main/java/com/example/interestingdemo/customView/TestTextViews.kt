package com.example.interestingdemo.customView

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.interestingdemo.R
import com.example.interestingdemo.extensions.sp2px


class TestTextViews @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int? = -1) : View(context, attrs, defStyleAttr
        ?: -1) {

    private val mPaint = Paint()
    private val textPaint = TextPaint()
    private val staticLayout1 = StaticLayout("Lorem Ipsum is simply dummy text of the printing and typesetting industry.Lorem Ipsum is simply dummy text of the printing and typesetting industry.", textPaint,
            600, Layout.Alignment.ALIGN_NORMAL, 4f, 0f, true)
    private val staticLayout2 = StaticLayout("1\n23456\n34567890-12345678901234567890", textPaint,
            600, Layout.Alignment.ALIGN_NORMAL, 4f, 0f, true)

    private val mBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_black_cat)

    private val mPath = Path()

    private val mMatrix = Matrix()
    private val pointsSrc = floatArrayOf(200f,100f,200f+mBitmap.width,100f,200f,100f+mBitmap.height,200f+mBitmap.width,100f+mBitmap.height)
    private val pointsDst = floatArrayOf(200f-10f,100f+50f,200f+mBitmap.width+120f,100f-90f,200f+20f,100f+mBitmap.height+30f,200f+mBitmap.width+20f,100f+mBitmap.height+60f)

    private val mCamera = Camera()
    private var rotateY = 0f
    private var textProgress = 0f
    private val textPaint1 = TextPaint()
    private val textPaint2 = TextPaint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

//        textPaint.textSize = 36f
//        canvas.save()
//        canvas.translate(50f, 100f)
//        staticLayout1.draw(canvas)
//        canvas.translate(0f, 200f)
//        staticLayout2.draw(canvas)
//        canvas.restore()
//        canvas.save()
//        mPaint.textSize = 42f
//        mPaint.isFakeBoldText = true //伪粗体
//        mPaint.isStrikeThruText = true //删除线
//        mPaint.isUnderlineText = true //下划线
//        mPaint.typeface = Typeface.MONOSPACE //字体
//        mPaint.textSkewX = -0.5f  //倾斜度
//        mPaint.textScaleX = 0.5f  //横向缩放，变胖变瘦
//        mPaint.letterSpacing = 1f //文字间距
//        canvas.drawText("Hello Custom View", 40f, 500f, mPaint)
//        canvas.drawText("Hello Custom View", 40f, 500f + mPaint.fontSpacing, mPaint)
//        canvas.restore()


//        //矩形裁剪
//        canvas.save()
//        canvas.clipRect(150f,600f,500f,1000f)
//        canvas.restore()
//
//        //路径裁剪
//        canvas.save()
//        mPath.moveTo(125f,650f)
//        mPath.lineTo(300f,750f)
//        mPath.lineTo(475f,650f)
//        mPath.lineTo(360f,1000f)
//        mPath.lineTo(240f,1000f)
//        mPath.close()
//        mPath.addCircle(300f,800f,160f,Path.Direction.CW)
//        canvas.clipPath(mPath)
//        canvas.restore()
//
//        canvas.translate(130f,80f)//平移
//        canvas.rotate(-30f,50f,600f)//旋转－30°
//        canvas.scale(0.8f,1.3f,50f,600f)//缩放，左上角是缩放轴心
//        canvas.skew(0f,0.5f)//错切
//        canvas.restore()
//
//        //Matrix自定义变换
//        canvas.save()
//        mMatrix.reset()
//        mMatrix.setPolyToPoly(pointsSrc,0,pointsDst,0,4)
//        canvas.concat(mMatrix)
//        canvas.drawBitmap(mBitmap, 200f, 100f, mPaint)
//        canvas.restore()

        //使用Camera来缩放，需要关闭硬件加速
        this.setLayerType(LAYER_TYPE_SOFTWARE,mPaint)

        canvas.save()
        mCamera.save()
        mCamera.rotateY(rotateY)
//        mCamera.rotateX(rotateY)
//        mCamera.rotateZ(rotateY)
        //移动Camera的坐标轴
        canvas.translate(100f+(mBitmap.width)/2,100f+(mBitmap.height)/2)
        mCamera.applyToCanvas(canvas)
        //移回Camera的坐标轴
        canvas.translate(-100f-(mBitmap.width)/2,-100f-(mBitmap.height)/2)
        mCamera.restore()

        canvas.clipRect(100f+mBitmap.width/2,0f,100f+mBitmap.width,100f+mBitmap.height*1.43f)//因为放大的缘故应该乘以2的平方根
        canvas.drawBitmap(mBitmap, 100f, 100f, mPaint)
        canvas.restore()

        canvas.save()
        canvas.clipRect(0f,0f,100f+mBitmap.width/2,100f+mBitmap.height)
        canvas.drawBitmap(mBitmap, 100f, 100f, mPaint)
        canvas.restore()

        canvas.save()
        val text = "偏偏是最焦躁的季节，可我却无心再继续"
        val width = textPaint2.measureText(text)
        textPaint1.isAntiAlias = true
        textPaint1.textSize = context.resources.sp2px(16f)
        textPaint1.color = context.resources.getColor(R.color.blue_500,context.theme)
        canvas.clipRect(50f,600f,50f+width*textProgress,720f)
        canvas.drawText(text,50f,700f,textPaint1)
        canvas.restore()
        canvas.save()
        textPaint2.isAntiAlias = true
        textPaint2.textSize = context.resources.sp2px(16f)
        textPaint2.color = context.resources.getColor(R.color.green_500,context.theme)
        canvas.clipRect(50f+width*textProgress,600f,50+width,720f)
        canvas.drawText(text,50f,700f,textPaint2)
        canvas.restore()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var measureWidth = 200 + mBitmap.width
        var measureHeight = 800

        measureWidth = resolveSize(measureWidth,widthMeasureSpec)
        measureHeight = resolveSize(measureHeight,heightMeasureSpec)

        setMeasuredDimension(measureWidth,measureHeight)

    }

    fun getRotateY() : Float{
        return rotateY
    }

    fun setRotateY(rotateY: Float){
        this.rotateY = rotateY
    }

    fun getTextProgress(): Float{
        return  textProgress
    }

    fun setTextProgress(textProgress: Float){
        this.textProgress = textProgress
    }



}