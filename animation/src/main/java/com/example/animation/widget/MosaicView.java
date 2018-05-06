package com.example.animation.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

@SuppressLint("DrawAllocation")
public class MosaicView extends View {
    private final static String TAG = "MosaicView";
    private Paint mPaint; // 声明一个画笔对象
    private int mOriention = LinearLayout.HORIZONTAL; // 马赛克的方向
    private int mGridCount = 20; // 格子的数量
    private PorterDuff.Mode mMode = PorterDuff.Mode.DST_IN; // 绘图模式为只展示交集
    private Bitmap mBitmap; // 声明一个位图对象
    private int mRatio = 0; // 绘制的比率
    private int mOffset = 5; // 偏差的比例
    private float FENMU = 100; // 计算比例的分母，其实分母的英语叫做denominator

    public MosaicView(Context context) {
        this(context, null);
    }

    public MosaicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(); // 创建一个新的画笔
    }

    // 设置马赛克的方向
    public void setOriention(int oriention) {
        mOriention = oriention;
    }

    // 设置马赛克的格子数量
    public void setGridCount(int grid_count) {
        mGridCount = grid_count;
    }

    // 设置偏差比例
    public void setOffset(int offset) {
        mOffset = offset;
    }

    // 设置绘图模式
    public void setMode(PorterDuff.Mode mode) {
        mMode = mode;
    }

    // 设置位图对象
    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    // 设置绘图比率
    public void setRatio(int ratio) {
        mRatio = ratio;
        invalidate(); // 立即刷新视图
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) {
            return;
        }
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 清空画布
        canvas.drawColor(Color.TRANSPARENT);
        // 创建一个遮罩位图
        Bitmap mask = Bitmap.createBitmap(width, height, mBitmap.getConfig());
        // 创建一个遮罩画布
        Canvas canvasMask = new Canvas(mask);
        if (mOriention == LinearLayout.HORIZONTAL) { // 水平方向
            float grid_width = height / mGridCount;
            int column_count = (int) Math.ceil(width / grid_width);
            int total_count = mGridCount * column_count;
            int draw_count = 0;
            for (int i = 0; i < column_count; i++) {
                for (int j = 0; j < mGridCount; j++) {
                    int now_ratio = (int) ((mGridCount * i + j) * FENMU / total_count);
                    if (now_ratio < mRatio - mOffset
                            || (now_ratio >= mRatio - mOffset && now_ratio < mRatio &&
                            ((j % 2 == 0 && i % 2 == 0) || (j % 2 == 1 && i % 2 == 1)))
                            || (now_ratio >= mRatio && now_ratio < mRatio + mOffset &&
                            ((j % 2 == 0 && i % 2 == 1) || (j % 2 == 1 && i % 2 == 0)))) {
                        int left = (int) (grid_width * i);
                        int top = (int) (grid_width * j);
                        // 在遮罩画布上绘制各方形格子
                        canvasMask.drawRect(left, top, left + grid_width, top + grid_width, mPaint);
                        if (i < column_count && j < mGridCount) {
                            draw_count++;
                        }
                        if (draw_count * FENMU / total_count > mRatio) {
                            break;
                        }
                    }
                }
                if (draw_count * FENMU / total_count > mRatio) {
                    break;
                }
            }
        } else { // 垂直方向
            float grid_width = width / mGridCount;
            int row_count = (int) Math.ceil(height / grid_width);
            int total_count = mGridCount * row_count;
            int draw_count = 0;
            for (int i = 0; i < row_count; i++) {
                for (int j = 0; j < mGridCount; j++) {
                    int now_ratio = (int) ((mGridCount * i + j) * FENMU / total_count);
                    if (now_ratio < mRatio - mOffset
                            || (now_ratio >= mRatio - mOffset && now_ratio < mRatio &&
                            ((j % 2 == 0 && i % 2 == 0) || (j % 2 == 1 && i % 2 == 1)))
                            || (now_ratio >= mRatio && now_ratio < mRatio + mOffset &&
                            ((j % 2 == 0 && i % 2 == 1) || (j % 2 == 1 && i % 2 == 0)))) {
                        int left = (int) (grid_width * j);
                        int top = (int) (grid_width * i);
                        // 在遮罩画布上绘制各方形格子
                        canvasMask.drawRect(left, top, left + grid_width, top + grid_width, mPaint);
                        if (i < row_count && j < mGridCount) {
                            draw_count++;
                        }
                        if (draw_count * FENMU / total_count > mRatio) {
                            break;
                        }
                    }
                }
                if (draw_count * FENMU / total_count > mRatio) {
                    break;
                }
            }
        }
        // 设置离屏缓存
        int saveLayer = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        Rect src = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        Rect dst = new Rect(0, 0, width, width * mBitmap.getHeight() / mBitmap.getWidth());
        // 绘制目标图像
        canvas.drawBitmap(mBitmap, src, dst, mPaint);
        // 设置混合模式（只在源图像和目标图像相交的地方绘制目标图像）
        mPaint.setXfermode(new PorterDuffXfermode(mMode));
        // 再绘制源图像的遮罩
        canvas.drawBitmap(mask, 0, 0, mPaint);
        // 还原混合模式
        mPaint.setXfermode(null);
        // 还原画布
        canvas.restoreToCount(saveLayer);
    }

}
