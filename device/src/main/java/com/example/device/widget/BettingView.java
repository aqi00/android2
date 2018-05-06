package com.example.device.widget;

import java.util.ArrayList;

import com.example.device.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BettingView extends View {
    private final static String TAG = "BettingView";
    private int mWidth; // 区域的宽度
    private int mHeight; // 区域的高度
    private Bitmap mBowlBg; // 摇骰子的碗背景位图
    private Rect mRectSrc; // 位图的原始边界
    private Rect mRectDest; // 位图的目标边界
    private ArrayList<Integer> mDiceList = new ArrayList<Integer>(); // 骰子队列
    private Bitmap mDiceOne, mDiceTwo, mDiceThree, mDiceFour, mDiceFive, mDiceSix; // 六个骰子的位图
    private Bitmap mShake01, mShake02, mShake03, mShake04, mShake05; // 摇晃时候的位图

    public BettingView(Context context) {
        this(context, null);
    }

    public BettingView(Context context, AttributeSet attr) {
        super(context, attr);
        // 从资源图片中获取碗背景的位图
        mBowlBg = BitmapFactory.decodeResource(getResources(), R.drawable.bobing_bg);
        // 根据位图的宽高创建位图的原始边界
        mRectSrc = new Rect(0, 0, mBowlBg.getWidth(), mBowlBg.getHeight());
        // 以下分别从资源图片中获取六个骰子的位图
        mDiceOne = BitmapFactory.decodeResource(getResources(), R.drawable.dice01);
        mDiceTwo = BitmapFactory.decodeResource(getResources(), R.drawable.dice02);
        mDiceThree = BitmapFactory.decodeResource(getResources(), R.drawable.dice03);
        mDiceFour = BitmapFactory.decodeResource(getResources(), R.drawable.dice04);
        mDiceFive = BitmapFactory.decodeResource(getResources(), R.drawable.dice05);
        mDiceSix = BitmapFactory.decodeResource(getResources(), R.drawable.dice06);
        // 以下分别从资源图片中获取摇晃时候的位图
        mShake01 = BitmapFactory.decodeResource(getResources(), R.drawable.shake01);
        mShake02 = BitmapFactory.decodeResource(getResources(), R.drawable.shake02);
        mShake03 = BitmapFactory.decodeResource(getResources(), R.drawable.shake03);
        mShake04 = BitmapFactory.decodeResource(getResources(), R.drawable.shake04);
        mShake05 = BitmapFactory.decodeResource(getResources(), R.drawable.shake05);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = mWidth * mBowlBg.getHeight() / mBowlBg.getWidth();
        if (width < height) { // 宽度小于高度
            // 缩小高度到宽度一样尺寸
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        } else { // 宽度不小于高度
            // 缩小宽度到高度一样尺寸
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }
        // 根据视图的宽高创建位图的目标边界
        mRectDest = new Rect(0, 0, mWidth, mHeight);
        Log.d(TAG, "mWidth=" + mWidth);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int item_width = mWidth / 5;
        int item_height = mHeight / 5;
        // 在画布上绘制摇骰子的碗背景
        canvas.drawBitmap(mBowlBg, mRectSrc, mRectDest, new Paint());
        // 摇晃结束，逐个绘制六个骰子的位图
        for (int i = 0; i < mDiceList.size(); i++) {
            Bitmap bitmap = mDiceSix; // 默认点数6
            if (mDiceList.get(i) == 0) { // 点数1
                bitmap = mDiceOne;
            } else if (mDiceList.get(i) == 1) { // 点数2
                bitmap = mDiceTwo;
            } else if (mDiceList.get(i) == 2) { // 点数3
                bitmap = mDiceThree;
            } else if (mDiceList.get(i) == 3) { // 点数4
                bitmap = mDiceFour;
            } else if (mDiceList.get(i) == 4) { // 点数5
                bitmap = mDiceFive;
            }
            // 计算该骰子的左侧坐标
            int left = item_width + item_width * (i % 3);
            // 计算该骰子的上方坐标
            int top = item_height * 2 + item_height * (i / 3);
            if (item_width > bitmap.getWidth() * 2.5) { // 碗够大，则放大骰子
                Log.d(TAG, "left=" + left + ", top=" + top + ", right=" + (left + bitmap.getWidth()) + ", bottom=" + (top + bitmap.getHeight()));
                Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                // 骰子图案放大至原来的1.5倍
                Rect dst = new Rect(left, top,
                        (int) (left + bitmap.getWidth() * 1.5), (int) (top + bitmap.getHeight() * 1.5));
                // 在画布上绘制放大后的骰子
                canvas.drawBitmap(bitmap, src, dst, new Paint());
            } else { // 碗不够大，则不放大骰子
                // 在画布上绘制保持原状的骰子
                canvas.drawBitmap(bitmap, left, top, new Paint());
            }
        }
        // 还在摇晃，于是绘制随机位置的几个骰子
        if (mDiceList == null || mDiceList.size() <= 0) {
            for (int j = 0; j < 6; j++) {
                int seq = (int) (Math.random() * 100 % 5);
                Bitmap bitmap = mShake05;
                if (seq == 0) {
                    bitmap = mShake01;
                } else if (seq == 1) {
                    bitmap = mShake02;
                } else if (seq == 2) {
                    bitmap = mShake03;
                } else if (seq == 3) {
                    bitmap = mShake04;
                }
                int left = item_width + (int) (item_width * (Math.random() * 10 % 2.0));
                int top = item_height + (int) (item_height * (Math.random() * 10 % 2.0));
                if (item_width > bitmap.getWidth() * 2) {
                    Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    Rect dst = new Rect(left, top,
                            (int) (left + bitmap.getWidth() * 1.5), (int) (top + bitmap.getHeight() * 1.5));
                    canvas.drawBitmap(bitmap, src, dst, new Paint());
                } else {
                    canvas.drawBitmap(bitmap, left, top, new Paint());
                }
            }
        }
    }

    // 结束摇骰子，展示点数确定的骰子队列
    public void setDiceList(ArrayList<Integer> diceList) {
        mDiceList = diceList;
        // 立即刷新视图，也就是调用视图的onDraw和dispatchDraw方法
        invalidate();
    }

    // 正在摇骰子，随机展示摇晃着的骰子
    public void setRandom() {
        mDiceList = new ArrayList<Integer>();
        // 立即刷新视图，也就是调用视图的onDraw和dispatchDraw方法
        invalidate();
    }

}
