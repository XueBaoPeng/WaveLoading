package com.example.liangmutian.bitmapwaveview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/7/4.
 */

public class CoustomView extends View {

    private int mWidth;
    private int mHeight;

    private Bitmap mBackgroundBitmap;//背景图片

    private Path mPath;//水波纹
    private Paint mPathPaint;

    private float mWaveHeight = 10f;//水波纹高度
    private float mWaveHalfWidth = 100f;//水波纹宽度
    private String mWaveColor = "#5be4ef";
    private int mWaveSpeed = 30;

    private Paint mTextPaint;
    private String mCurrentText = "";
    private String mTextColor = "#FFFFFF";
    private int mTextSize = 41;

    private int mMaxProgress = 100;
    private int mCurrentProgress = 0;
    private float mCurY;//上次波浪中线的y轴坐标

    private float mDistance = 0;
    private int mRefreshGap = 10;

    /**
     * Y方向上的每次增长值
     */
    private int increateHeight;

    private boolean mAllowProgressInBothDirections = false;

    //设置当前进度值和当前显示的文字
    public void setCurrent(int currentProgress, String currentText) {
        this.mCurrentProgress = currentProgress;
        this.mCurrentText = currentText;
        invalidate();
    }

    //设置当前进度值和当前显示的文字
    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    //设置显示文字的大小和颜色
    public void setText(String mTextColor, int mTextSize) {
        this.mTextColor = mTextColor;
        this.mTextSize = mTextSize;
    }

    //设置波浪的高度和波浪的宽度（均为一个波峰的大小）
    public void setWave(float mWaveHight, float mWaveWidth) {
        this.mWaveHeight = mWaveHight;
        this.mWaveHalfWidth = mWaveWidth / 2;
    }

    //设置水波的颜色
    public void setWaveColor(String mWaveColor) {
        this.mWaveColor = mWaveColor;
    }

    //设置波浪的上下震动的速度（这里注意值越大，震动的越小）
    public void setWaveSpeed(int mWaveSpeed) {
        this.mWaveSpeed = mWaveSpeed;
    }

    public void allowProgressInBothDirections(boolean allow) {
        this.mAllowProgressInBothDirections = allow;
    }

    public CoustomView(Context context) {
        this(context, null);
    }

    public CoustomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (null == getBackground()) {
            throw new IllegalArgumentException(String.format("background is null."));
        } else {
            mBackgroundBitmap = getBitmapFromDrawable(getBackground());
        }
        /**
         * 波浪画笔
         */
        mPath = new Path();
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);//抗锯齿
        mPathPaint.setStyle(Paint.Style.FILL);
        /**
         * 进度画笔
         */
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

//        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mMaxProgress);
//        valueAnimator.setDuration(4000);
//        valueAnimator.setInterpolator(new LinearInterpolator());
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                mCurrentProgress = (int) animation.getAnimatedValue();
//                invalidate();
//            }
//        });
//
//        valueAnimator.setCurrentPlayTime(2000);
//        valueAnimator.start();
        mBezierDiffX = INCREATE_WIDTH;
        mWaveLowestY = 1.2f * mHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mWaveY = mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mBackgroundBitmap != null) {
            canvas.drawBitmap(createBitmap(), 0, 0, null);
        }
    }

    /**
     * 水波纹的X左边是否在增长
     */
    private boolean mIsXDiffIncrease = true;
    /**
     * X方向上的每次增长值
     */
    private final int INCREATE_WIDTH = 0x00000005;
    /**
     * 贝塞尔曲线控制点距离原点x的增量
     */
    private float mBezierDiffX;

    private void checkIncrease(float mBezierDiffX) {
        if (mIsXDiffIncrease) {
            mIsXDiffIncrease = mBezierDiffX > 0.5 * mWidth ? !mIsXDiffIncrease : mIsXDiffIncrease;
        } else {
            mIsXDiffIncrease = mBezierDiffX < 10 ? !mIsXDiffIncrease : mIsXDiffIncrease;
        }
    }

    /**
     * 水波纹最低控制点y
     */
    private float mWaveLowestY;

    /**
     * 当前波纹的y值
     */
    private float mWaveY;
    private Bitmap createBitmap() {
        mPathPaint.setColor(Color.parseColor(mWaveColor));
        mTextPaint.setColor(Color.parseColor(mTextColor));
        mTextPaint.setTextSize(mTextSize);
        mPath.reset();

        if (mIsXDiffIncrease) {
            mBezierDiffX += INCREATE_WIDTH;
        } else {
            mBezierDiffX -= INCREATE_WIDTH;
        }
        checkIncrease(mBezierDiffX);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap finalBmp = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(finalBmp);

        float CurMidY = mHeight * (mMaxProgress - mCurrentProgress) / mMaxProgress;
        if (mAllowProgressInBothDirections || mWaveY > CurMidY) {
            mWaveY = mWaveY - (mWaveY - CurMidY) / 10;
            mWaveLowestY = mWaveLowestY - (mWaveLowestY - CurMidY) / 5;
        }

        mPath.moveTo(0-mBezierDiffX, mWaveY);

        mPath.cubicTo(mBezierDiffX, mWaveY - (mWaveLowestY - mWaveY),
                mBezierDiffX + mWidth / 2, mWaveLowestY, mWidth, mWaveY);
        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();
        canvas.drawPath(mPath, mPathPaint);

        int min = Math.min(mWidth, mHeight);
        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap, min, min, false);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        canvas.drawBitmap(mBackgroundBitmap, 0, 0, paint);

        canvas.drawText(mCurrentText, mWidth / 2, mHeight / 2, mTextPaint);
        return finalBmp;
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
}
