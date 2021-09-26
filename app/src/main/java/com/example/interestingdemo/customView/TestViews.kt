package com.example.interestingdemo.customView

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.example.interestingdemo.R
import kotlin.math.roundToInt

class TestViews @JvmOverloads constructor (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int? = -1) : View(context,attrs,defStyleAttr ?: -1) {
    private var mPaint = Paint()
    //路径
    private val mPath = Path()
    private val mPath1 = Path()
    private val mPath2 = Path()
    private val mTextPaint = TextPaint()

    private var progress = 0f
    //匀速
    private var linearTransX = 0f
    //先加速后减速,也是默认的
    private var acAndDeTransX = 0f
    //加速
    private var acTransX = 0f
    //减速
    private var deTransX = 0f
    //回拉
    private var anTransX = 0f
    //回弹
    private var overTransX = 0f
    //回拉加回弹
    private var anAndOverTranX = 0f


    //Bitmap填充
//    private val bitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_cat)
//    private val shader = BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
    //扫描渐变
    private val shader = SweepGradient(100f,100f,Color.RED,Color.BLUE)
    //LinearGradient 线性渐变
//    private val shader = LinearGradient(20f,20f,160f,160f,Color.RED,Color.BLUE,Shader.TileMode.MIRROR)
    //RadialGradient 辐射渐变
//    private val shader = RadialGradient(100f,100f,80f,Color.RED,Color.BLUE,Shader.TileMode.MIRROR)

    //虚线
    private val dashPathEffect = DashPathEffect(floatArrayOf(20f,20f),0f)
//    随机偏离,有点问题，即便关了硬件加速，deviation也是改变了线的宽度
    private val discreteEffect = DiscretePathEffect(20f,5f)
    //拐角变圆角
    private val cornerPathEffect = CornerPathEffect(20f)

    private val composeEffect = ComposePathEffect(dashPathEffect,discreteEffect)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.setLayerType(LAYER_TYPE_SOFTWARE,mPaint)

        //绘制背景蒙版
        canvas.drawColor(Color.parseColor("#f5f5f5"))

        mPaint.style = Paint.Style.FILL
        mPaint.shader = shader //当设置shader时，下面设置color是不会生效的，因此在设定color时需要将shader置为null
        mPaint.color = context.resources.getColor(R.color.blue_300,context.theme)
        canvas.drawCircle(100f,100f,80f, mPaint)
        mPaint.shader = null

        //同心圆
        mPaint.style = Paint.Style.FILL
        mPaint.color = context.resources.getColor(R.color.red_200,context.theme)
        canvas.drawCircle(300f,100f,40f, mPaint)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 40f
        mPaint.color = context.resources.getColor(R.color.red_400,context.theme)
        canvas.drawCircle(300f,100f,60f,mPaint)

        //层层矩形框
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 15f
        mPaint.color = context.resources.getColor(R.color.grey_200,context.theme)
        canvas.drawRect(20f,200f,420f,400f,mPaint)
        mPaint.strokeWidth = 10f
        mPaint.color = context.resources.getColor(R.color.grey_300,context.theme)
        canvas.drawRect(70f,225f,370f,375f,mPaint)
        mPaint.strokeWidth = 5f
        mPaint.color = context.resources.getColor(R.color.grey_400,context.theme)
        canvas.drawRect(120f,250f,320f,350f,mPaint)
        mPaint.strokeWidth = 1f
        mPaint.color = context.resources.getColor(R.color.grey_500,context.theme)
        canvas.drawRect(170f,275f,280f,325f,mPaint)
        mPaint.style = Paint.Style.FILL
        mPaint.color = context.resources.getColor(R.color.grey_600,context.theme)
        canvas.drawRect(195f,290f,245f,310f,mPaint)

        //圆头
        mPaint.strokeWidth = 8f
        mPaint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(20f,420f,20f,520f,mPaint)
        canvas.drawLine(30f,420f,130f,420f,mPaint)
        canvas.drawLine(130f,430f,130f,530f,mPaint)
        canvas.drawLine(20f,530f,120f,530f,mPaint)
        canvas.drawLine(30f,430f,120f,520f,mPaint)
        canvas.drawLine(30f,520f,120f,430f,mPaint)
        //方头
        mPaint.strokeCap = Paint.Cap.SQUARE
        canvas.drawLine(150f,420f,150f,520f,mPaint)
        canvas.drawLine(160f,420f,260f,420f,mPaint)
        canvas.drawLine(260f,430f,260f,530f,mPaint)
        canvas.drawLine(150f,530f,250f,530f,mPaint)
        canvas.drawLine(160f,430f,250f,520f,mPaint)
        canvas.drawLine(160f,520f,250f,430f,mPaint)
        //平头  与方头的区别在于：平头是线宽取半，方头线宽全取
        mPaint.strokeCap = Paint.Cap.BUTT
        canvas.drawLine(150f+130f,420f,150f+130f,520f,mPaint)
        canvas.drawLine(160f+130f,420f,260f+130f,420f,mPaint)
        canvas.drawLine(260f+130f,430f,260f+130f,530f,mPaint)
        canvas.drawLine(150f+130f,530f,250f+130f,530f,mPaint)
        canvas.drawLine(160f+130f,430f,250f+130f,520f,mPaint)
        canvas.drawLine(160f+130f,520f,250f+130f,430f,mPaint)

        //椭圆
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true//设置抗锯齿
        canvas.drawOval(20f,550f,400f,650f,mPaint)
        mPaint.isAntiAlias = false//不设置抗锯齿
        canvas.drawOval(50f,580f,370f,620f,mPaint)

        //相交圆环
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPath.fillType = Path.FillType.EVEN_ODD
        mPath.addCircle(500f,100f,70f,Path.Direction.CW)
        mPath.addCircle(600f,100f,70f,Path.Direction.CCW)

        //五角星
        mPath.moveTo(550f,250f)
        mPath.lineTo(500f,400f)
        mPath.lineTo(620f,300f)
        mPath.lineTo(480f,300f)
        mPath.lineTo(600f,400f)
        mPath.close()
        mPaint.strokeCap = Paint.Cap.SQUARE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.color = context.resources.getColor(R.color.blue_500,context.theme)
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.strokeWidth = 10f
        canvas.drawPath(mPath,mPaint)

        mPaint.style = Paint.Style.FILL
        mPaint.color = context.resources.getColor(R.color.blue_200,context.theme)
        canvas.drawArc(500f,500f,640f,640f,-90f,30f,true,mPaint)
        canvas.drawArc(500f,500f,640f,640f,-50f,90f,false,mPaint)

        //圆环，弧线
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 30f
        mPaint.color = context.resources.getColor(R.color.green_200,context.theme)
        canvas.drawArc(500f,500f,640f,640f,60f,60f,false,mPaint)
        mPaint.color = context.resources.getColor(R.color.green_400,context.theme)
        canvas.drawArc(500f,500f,640f,640f,120f,60f,false,mPaint)
        mPaint.color = context.resources.getColor(R.color.green_600,context.theme)
        canvas.drawArc(500f,500f,640f,640f,180f,60f,false,mPaint)


        //饼状图
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = 0f
        mPaint.strokeCap = Paint.Cap.BUTT
        mPaint.color = context.resources.getColor(R.color.red_400,context.theme)
        canvas.drawArc(700f,500f,840f,640f,-60f,60f,true,mPaint)
        mPaint.color = context.resources.getColor(R.color.red_400,context.theme)
        canvas.drawArc(700f,500f,840f,640f,60f,60f,true,mPaint)
        mPaint.color = context.resources.getColor(R.color.red_400,context.theme)
        canvas.drawArc(700f,500f,840f,640f,180f,60f,true,mPaint)

        //随机偏移+虚线
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 1f
        mPaint.pathEffect = composeEffect
        mPath1.moveTo(20f,700f)
        mPath1.lineTo(100f,800f)
        mPath1.lineTo(200f,720f)
        mPath1.lineTo(350f,850f)
        mPath1.lineTo(450f,780f)
        mPath1.lineTo(500f,700f)
        mPath1.lineTo(600f,720f)
        mPath1.lineTo(650f,850f)
        canvas.drawPath(mPath1,mPaint)
        //圆角
        mPaint.pathEffect = cornerPathEffect
        mPath2.moveTo(20f,800f)
        mPath2.lineTo(100f,900f)
        mPath2.lineTo(200f,820f)
        mPath2.lineTo(350f,950f)
        mPath2.lineTo(450f,880f)
        mPath2.lineTo(500f,800f)
        mPath2.lineTo(600f,820f)
        mPath2.lineTo(650f,950f)
        canvas.drawPath(mPath2,mPaint)
        mPaint.pathEffect = null

        mPaint.textSize = 32f
        mPaint.style = Paint.Style.FILL

        canvas.drawTextOnPath("一二三四五六七八九十",mPath2,5f,-10f,mPaint)

//        mPaint.strokeWidth = 2f
//        canvas.drawLine(230f,1000f,230f,1400f,mPaint)
//        canvas.drawLine(30f,1200f,430f,1200f,mPaint)

        /**
         * 圆弧进度条
         */
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 20f
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(700f,700f,1000f,1000f,90f, progress*3.6f,false,mPaint)

        val string = "${(progress*3.6f*100f/360f).roundToInt()}%"
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = 64f
        mTextPaint.color = context.resources.getColor(R.color.blue_500,context.theme)
        mTextPaint.strokeWidth = 20f
        mTextPaint.isAntiAlias = true
        mTextPaint.strokeCap = Paint.Cap.ROUND
        val mixHei = (mTextPaint.fontMetrics.bottom - mTextPaint.fontMetrics.ascent)/4f//计算baseline过于麻烦，直接除以4比较简单
        canvas.drawText(string, 850f, 850f + mixHei, mTextPaint)


        /**
         * 速度设置
         */
        mTextPaint.textSize = 30f
        mTextPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("匀速",30f,1080f,mTextPaint)
        canvas.drawLine(30f,1100f,linearTransX,1100f,mPaint)
        canvas.drawText("先加速后减速",30f,1180f,mTextPaint)
        canvas.drawLine(30f,1200f,acAndDeTransX,1200f,mPaint)
        canvas.drawText("加速",30f,1280f,mTextPaint)
        canvas.drawLine(30f,1300f,acTransX,1300f,mPaint)
        canvas.drawText("减速",30f,1380f,mTextPaint)
        canvas.drawLine(30f,1400f,deTransX,1400f,mPaint)
        canvas.drawText("回拉",30f,1480f,mTextPaint)
        canvas.drawLine(30f,1500f,anTransX,1500f,mPaint)
        canvas.drawText("回弹",30f,1580f,mTextPaint)
        canvas.drawLine(30f,1600f,overTransX,1600f,mPaint)
        canvas.drawText("回拉回弹",30f,1680f,mTextPaint)
        canvas.drawLine(30f,1700f,anAndOverTranX,1700f,mPaint)

    }

    fun getProgress(): Float{
        return progress
    }

    fun setProgress(progress: Float){
        this.progress = progress
        invalidate()
    }

    fun getLinearTransX(): Float{
        return linearTransX
    }

    fun setLinearTransX(linearTransX: Float){
        this.linearTransX = linearTransX
        invalidate()
    }

    fun getAcAndDeTransX(): Float{
        return acAndDeTransX
    }

    fun setAcAndDeTransX(acAndDeTransX: Float){
        this.acAndDeTransX = acAndDeTransX
        invalidate()
    }

    fun getAcTransX(): Float{
        return acTransX
    }

    fun setAcTransX(acTransX: Float){
        this.acTransX = acTransX
        invalidate()
    }

    fun getDeTransX(): Float{
        return deTransX
    }

    fun setDeTransX(deTransX: Float){
        this.deTransX = deTransX
        invalidate()
    }

    fun getAnTransX(): Float{
        return anTransX
    }

    fun setAnTransX(anTransX: Float){
        this.anTransX = anTransX
        invalidate()
    }

    fun getOverTransX(): Float{
        return linearTransX
    }

    fun setOverTransX(overTransX: Float){
        this.overTransX = overTransX
        invalidate()
    }

    fun getAnAndOverTranX(): Float{
        return linearTransX
    }

    fun setAnAndOverTranX(anAndOverTranX: Float){
        this.anAndOverTranX = anAndOverTranX
        invalidate()
    }

}