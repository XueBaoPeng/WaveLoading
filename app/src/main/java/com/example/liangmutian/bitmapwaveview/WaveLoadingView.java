package com.example.liangmutian.bitmapwaveview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xbp on 2017/7/4.
 * 水波纹加载的进度条
 */

public class WaveLoadingView extends View {

    private int mBackgroundWidth;//背景图的宽
    private int mBackgroundHeight;//背景图的高

    private Bitmap mBackgroundBitmap;//背景图片（可以任意设置）
    private Paint mBackgroundPaint;//绘制背景画笔
    private Canvas mBackgroundCanvas;
    private Bitmap mfinalShowBitmap;//最终展示的背景

    private Path mWavePath;//水波纹连线
    private Paint mWavePaint;//水波纹画笔

    private Paint mPercentPaint;//百分比画笔

    private int mMaxProgress = 100;//最大的进度值默认100
    private int mCurrentProgress = 0;//当前进度值

    private float mWaveStartAmplitude = 0;//水波纹的起始振幅
    private float mWaveAmplitude;//水波纹的振幅
    private int mOffset;//水波纹X轴的偏移量形成水平波动效果

    private ValueAnimator animator;//水平循环的属性动画

    private OnLoadinFinishListener onLoadinFinishListener;

    public void setOnLoadinFinishListener(OnLoadinFinishListener onLoadinFinishListener) {
        this.onLoadinFinishListener = onLoadinFinishListener;
    }

    //渐变色开始
    private static final int DEFAULT_START_COLOR = Color.parseColor("#FF6600");
    //渐变色结束
    private static final int DEFAULT_END_COLOR = Color.parseColor("#3398FD");

    //设置当前进度值和当前显示的文字
    public void setCurrent(int currentProgress) {
        this.mCurrentProgress = currentProgress;
        postInvalidate();
    }


    public WaveLoadingView(Context context) {
        this(context, null);
    }

    public WaveLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveLoadingView);
        float textSize = typedArray.getDimension(R.styleable.WaveLoadingView_WaveTextSize, dip2px(context, 20));
        int textColor = typedArray.getColor(R.styleable.WaveLoadingView_WaveTextColor, Color.BLACK);
        mMaxProgress = typedArray.getInteger(R.styleable.WaveLoadingView_WaveMaxProgress, 100);
        mWaveAmplitude = typedArray.getDimension(R.styleable.WaveLoadingView_WaveAmplitude, dip2px(context, 10));
        if (null == getBackground()) {
            throw new IllegalArgumentException(String.format("background  not null"));
        } else {
            mBackgroundBitmap = getBitmapFromDrawable(getBackground());
            mBackgroundCanvas = new Canvas();
            /**
             * 波浪画笔
             */
            mWavePath = new Path();
            mWavePaint = new Paint();
            mWavePaint.setAntiAlias(true);//抗锯齿
            mWavePaint.setStyle(Paint.Style.FILL);
            /**
             * 进度画笔
             */
            mPercentPaint = new Paint();
            mPercentPaint.setAntiAlias(true);
            mPercentPaint.setColor(textColor);
            mPercentPaint.setTextSize(textSize);
            mPercentPaint.setTextAlign(Paint.Align.CENTER);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setAntiAlias(true);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBackgroundWidth = MeasureSpec.getSize(widthMeasureSpec);
        mBackgroundHeight = MeasureSpec.getSize(heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mBackgroundBitmap != null) {
            canvas.drawBitmap(createBitmap(), 0, 0, null);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) { //可以准确测量到view的宽高
        super.onWindowFocusChanged(hasWindowFocus);
        startAnimal();//启动动画
    }

    private void startAnimal() {
        animator = ValueAnimator.ofInt(0, mBackgroundWidth);//设置移动范围为一个屏幕宽度
        animator.setDuration(2000);//设置持续时间为1秒
        animator.setRepeatCount(ValueAnimator.INFINITE);//设置为无限循环
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();//修改偏移量
                postInvalidate();//刷新界面
            }
        });
        animator.start();
        LinearGradient lg = new LinearGradient(0, 0, 0, mBackgroundHeight, DEFAULT_START_COLOR, DEFAULT_END_COLOR, Shader.TileMode.REPEAT);
        mWavePaint.setShader(lg);

        mfinalShowBitmap = Bitmap.createBitmap(mBackgroundWidth, mBackgroundHeight, Bitmap.Config.ARGB_8888);
        mBackgroundCanvas.setBitmap(mfinalShowBitmap);

        int min = Math.min(mBackgroundWidth, mBackgroundHeight);
        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap, min, min, false);//调整背景图片
    }

    private Bitmap createBitmap() {

        mWavePath.reset();

        //绘制波浪曲线
        float mWaveHeight = mBackgroundHeight * mCurrentProgress / mMaxProgress;
        float mAmplitude;
        if (mCurrentProgress == 0) {
            mAmplitude = mWaveStartAmplitude;
        } else {
            mAmplitude = mWaveAmplitude;
        }

        mWavePath.moveTo(mBackgroundWidth + mOffset, mBackgroundHeight - mWaveHeight);//因为 y 轴正方向是向下的，所以减去水波纹的高度
        mWavePath.lineTo(mBackgroundWidth + mOffset, mBackgroundHeight);//绘制右边的线段
        mWavePath.lineTo(-mBackgroundWidth + mOffset, mBackgroundHeight);//绘制底部的线段
        mWavePath.lineTo(-mBackgroundWidth + mOffset, mBackgroundHeight - mWaveHeight);//绘制左边的线段

        mWavePath.quadTo((-mBackgroundWidth * 3 / 4) + mOffset, mBackgroundHeight - mWaveHeight + mAmplitude, (-mBackgroundWidth / 2) + mOffset, mBackgroundHeight - mWaveHeight); //画出第一段波纹的第一条曲线
        mWavePath.quadTo((-mBackgroundWidth / 4) + mOffset, mBackgroundHeight - mWaveHeight - mAmplitude, 0 + mOffset, mBackgroundHeight - mWaveHeight); //画出第一段波纹的第二条曲线
        mWavePath.quadTo((mBackgroundWidth / 4) + mOffset, mBackgroundHeight - mWaveHeight + mAmplitude, (mBackgroundWidth / 2) + mOffset, mBackgroundHeight - mWaveHeight); //画出第二段波纹的第一条曲线
        mWavePath.quadTo((mBackgroundWidth * 3 / 4) + mOffset, mBackgroundHeight - mWaveHeight - mAmplitude, mBackgroundWidth + mOffset, mBackgroundHeight - mWaveHeight);  //画出第二段波纹的第二条曲线
        mWavePath.close();//封闭曲线
        mBackgroundCanvas.drawPath(mWavePath, mWavePaint);//绘制曲线

        mBackgroundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        mBackgroundCanvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);//组合背景图片和波浪

        //绘制进度值
        mBackgroundCanvas.drawText(mCurrentProgress + "%", mBackgroundWidth / 2 - 20, mBackgroundHeight / 2 + 60, mPercentPaint);
        if (mCurrentProgress == mMaxProgress) {
            if (onLoadinFinishListener != null) {
                onLoadinFinishListener.LoadingComplete();
            }
            animator.removeAllUpdateListeners();
            animator.cancel();//防止内存泄漏
        }
        return mfinalShowBitmap;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {//获取背景图片
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * 加载完成回调
     */
    public interface OnLoadinFinishListener {
        void LoadingComplete();
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
