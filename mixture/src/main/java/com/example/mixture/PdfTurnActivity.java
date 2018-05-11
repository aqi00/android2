package com.example.mixture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.example.mixture.util.FileUtil;
import com.example.mixture.util.Utils;

import java.util.ArrayList;

import fi.harism.curl.CurlPage;
import fi.harism.curl.CurlView;

/**
 * Created by ouyangshen on 2018/2/11.
 */
public class PdfTurnActivity extends AppCompatActivity {
    private final static String TAG = "PdfTurnActivity";
    private CurlView cv_content; // 声明一个卷曲视图对象
    private ArrayList<String> imgArray = new ArrayList<String>(); // 图片路径队列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_turn);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);
        // 给tl_head设置导航图标的点击监听器
        // setNavigationOnClickListener必须放到setSupportActionBar之后，不然不起作用
        tl_head.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 关闭当前页面
            }
        });
        TextView tv_title = findViewById(R.id.tv_title);
        // 从布局文件中获取名叫cv_content的卷曲视图
        cv_content = findViewById(R.id.cv_content);
        // 从前一个页面传来的意图中获取名叫path的文件路径
        String path = getIntent().getStringExtra("path");
        // 从文件路径中获取文件名称
        tv_title.setText(FileUtil.getFileName(path));
        // 从前一个页面传来的意图中获取名叫img_list的图片路径队列
        imgArray = getIntent().getStringArrayListExtra("img_list");
        if (imgArray != null && imgArray.size() > 0) {
            // 从指定路径的图片文件中获取位图数据
            Bitmap bitmap = BitmapFactory.decodeFile(imgArray.get(0));
            int iv_height = bitmap.getHeight() < Utils.dip2px(this, 300)
                    ? Utils.dip2px(this, 300) : bitmap.getHeight();
            // 在卷曲视图上显示位图图像
            showImage(iv_height);
            bitmap.recycle(); // 回收位图对象
        }
    }

    // 在卷曲视图上显示位图图像
    private void showImage(int height) {
        LayoutParams params = cv_content.getLayoutParams();
        params.height = height;
        // 设置卷曲视图的布局参数
        cv_content.setLayoutParams(params);
        // 设置卷曲视图的书页提供器
        cv_content.setPageProvider(new PageProvider(imgArray));
        // 设置卷曲视图的尺寸变更观察器
        cv_content.setSizeChangedObserver(new SizeChangedObserver());
        // 设置卷曲视图默认显示第一页
        cv_content.setCurrentIndex(0);
        // 设置卷曲视图的背景颜色
        cv_content.setBackgroundColor(Color.LTGRAY);
    }

    // 定义一个加载图片页面的提供器
    private class PageProvider implements CurlView.PageProvider {
        private ArrayList<String> mPathArray = new ArrayList<String>();

        public PageProvider(ArrayList<String> pathArray) {
            mPathArray = pathArray;
        }

        @Override
        public int getPageCount() {
            return mPathArray.size();
        }

        // 加载指定页面的位图
        private Bitmap loadBitmap(int width, int height, int index) {
            // 创建该页面的临时位图
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 将临时位图洗白
            bitmap.eraseColor(Color.WHITE);
            // 在临时位图上创建画布
            Canvas canvas = new Canvas(bitmap);
            // 从指定路径的图片文件中获取位图数据
            Bitmap image = BitmapFactory.decodeFile(mPathArray.get(index));
            // 把位图对象转换为图形对象
            BitmapDrawable drawable = new BitmapDrawable(getResources(), image);
            // 下面计算画布有效区域的四周边界
            int margin = 0;
            int border = 1;
            Rect rect = new Rect(margin, margin, width - margin, height - margin);
            int imageWidth = rect.width() - (border * 2);
            int imageHeight = imageWidth * drawable.getIntrinsicHeight()
                    / drawable.getIntrinsicWidth();
            if (imageHeight > rect.height() - (border * 2)) {
                imageHeight = rect.height() - (border * 2);
                imageWidth = imageHeight * drawable.getIntrinsicWidth()
                        / drawable.getIntrinsicHeight();
            }
            rect.left += ((rect.width() - imageWidth) / 2) - border;
            rect.right = rect.left + imageWidth + border + border;
            rect.top += ((rect.height() - imageHeight) / 2) - border;
            rect.bottom = rect.top + imageHeight + border + border;
            // 创建一个画笔对象
            Paint paint = new Paint();
            paint.setColor(Color.LTGRAY);
            // 在画布上绘制浅灰背景
            canvas.drawRect(rect, paint);
            rect.left += border;
            rect.right -= border;
            rect.top += border;
            rect.bottom -= border;
            // 设置图像对象的四周边界
            drawable.setBounds(rect);
            // 把图形对象绘制到画布上
            drawable.draw(canvas);
            return bitmap;
        }

        // 在页面更新时触发
        public void updatePage(CurlPage page, int width, int height, int index) {
            // 加载指定页面的位图
            Bitmap front = loadBitmap(width, height, index);
            // 设置书页的纹理
            page.setTexture(front, CurlPage.SIDE_BOTH);
        }
    }

    // 定义一个监听卷曲视图发生尺寸变更的观察器
    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h) {
            // 设置卷曲视图的观看模式
            cv_content.setViewMode(CurlView.SHOW_ONE_PAGE);
            // 设置卷曲视图的四周边缘
            cv_content.setMargins(0f, 0f, 0f, 0f);
        }
    }

}
