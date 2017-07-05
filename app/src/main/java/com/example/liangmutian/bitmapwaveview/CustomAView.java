package com.example.liangmutian.bitmapwaveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * xbp
 */
public class CustomAView extends View {

    private int mXPoint = 200;//原点的X坐标
    private int mYPoint = 800;

    public int mXScale = 100;//
    public int mYScale = 100;

    private int XLength = 680;
    private int YLength = 640;

    public String[] XLabel = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};    //X的刻度
    public String[] YLabel = new String[]{"", "50", "100", "150", "200", "250", "300", "350"};    //Y的刻度
    public String[] mData = new String[]{"150", "230", "10", "136", "45", "40", "112", "313"};      //数据
    public String Title;    //显示的标题

    /**
     * @param context
     */
    public CustomAView(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public CustomAView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CustomAView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    Paint paint;
    Paint paint1;

    /**
     * 初始化变量
     */
    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);//去锯齿
        paint.setColor(Color.parseColor("#ff02f2"));//颜色
        paint1 = new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setAntiAlias(true);//去锯齿
        paint1.setColor(Color.DKGRAY);
        paint.setTextSize(12);  //设置轴文字大小
    }

    public void SetInfo(String[] XLabel, String[] YLable, String[] mData) {
        this.XLabel = XLabel;
        this.YLabel = YLable;
        this.mData = mData;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        //画Y轴
        canvas.drawLine(mXPoint, mYPoint, mXPoint, mYPoint - YLength, paint);

        canvas.drawLine(mXPoint, mYPoint - YLength, mXPoint - 4, mYPoint - YLength + 6, paint);
        canvas.drawLine(mXPoint, mYPoint - YLength, mXPoint + 4, mYPoint - YLength + 6, paint);

        for (int i = 0; i * mYScale < YLength; i++) {
            canvas.drawLine(mXPoint, mYPoint - i * mYScale, mXPoint + 5, mYPoint - i * mYScale, paint);
            try {
                canvas.drawText(YLabel[i], mXPoint - 22, mYPoint - i * mYScale + 5, paint);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        canvas.drawLine(mXPoint, mYPoint, mXPoint + XLength, mYPoint, paint);

        for (int i = 0; i * mXScale < XLength; i++) {
            canvas.drawLine(mXPoint + mXScale * i, mYPoint, mXPoint + i * mXScale, mYPoint - 5, paint);
            try {
                canvas.drawText(XLabel[i], mXPoint + mXScale * i + 20, mYPoint + 20, paint);
                //数据值
                if (i > 0 && getYPoint(mData[i - 1]) != -999 && getYPoint(mData[i]) != -999)  //保证有效数据
                    canvas.drawLine(mXPoint + (i - 1) * mXScale, getYPoint(mData[i - 1]), mXPoint + i * mXScale, getYPoint(mData[i]), paint);
                canvas.drawCircle(mXPoint + i * mXScale, getYPoint(mData[i]), 2, paint);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        canvas.drawLine(mXPoint + XLength, mYPoint, mXPoint + XLength - 6, mYPoint + 3, paint);
        canvas.drawLine(mXPoint + XLength, mYPoint, mXPoint + XLength - 6, mYPoint - 3, paint);
    }

    private int getYPoint(String y0) {

        int y;
        try {
            y = Integer.parseInt(y0);
        } catch (Exception e) {
            return -999;    //出错则返回-999
        }
        try {
            return mYPoint - y * mYScale / Integer.parseInt(YLabel[1]);
        } catch (Exception e) {
        }
        return y;
    }

}
