package com.example.mixture;

import java.io.File;
import java.util.ArrayList;

import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.core.DocumentView;
import org.vudroid.djvudroid.codec.DjvuContext;
import org.vudroid.pdfdroid.codec.PdfContext;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.mixture.adapter.PdfPageAdapter;
import com.example.mixture.util.FileUtil;
import com.example.mixture.util.MD5Util;

@SuppressLint("DefaultLocale")
public class VudroidActivity extends AppCompatActivity {
    private final static String TAG = "VudroidActivity";
    private ViewPager vp_content; // 声明一个翻页视图对象
    private FrameLayout fr_content; // 声明一个框架布局对象
    public static DecodeService decodeService; // 声明一个解码服务对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象
    private String mDir, mPath; // 文件目录和文件路径
    private ProgressDialog mDialog; // 声明一个进度对话框对象
    private ArrayList<String> imgArray = new ArrayList<String>(); // 图片路径队列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vudroid);
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
        // 从布局文件中获取名叫fr_content的框架布局
        fr_content = findViewById(R.id.fr_content);
        // 从布局文件中获取名叫vp_content的翻页视图
        vp_content = findViewById(R.id.vp_content);
        // 从布局视图中获取名叫pts_tab的翻页标题栏
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        // 设置翻页标题栏的文本大小
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        // 从前一个页面传来的意图中获取名叫title的书籍标题
        String title = getIntent().getStringExtra("title");
        // 从前一个页面传来的意图中获取名叫path的文件路径
        mPath = getIntent().getStringExtra("path");
        TextView tv_title = findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            // 从文件路径中获取文件名称
            tv_title.setText(FileUtil.getFileName(mPath));
        }
        readBook(); // 开始读取书籍内容
    }

    @Override
    protected void onDestroy() {
        decodeService.recycle(); // 回收解码服务
        decodeService = null;
        super.onDestroy();
    }

    // 开始读取书籍内容
    private void readBook() {
        // 弹出进度对话框
        mDialog = ProgressDialog.show(this, "请稍候", "正在努力加载");
        if (FileUtil.getExtendName(mPath).equals("pdf")) { // PDF文件
            decodeService = new DecodeServiceBase(new PdfContext());
            // 生成PDF文件的图片保存目录
            mDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() +
                    "/pdf/" + MD5Util.encrypt(mPath);
        } else { // DJVU文件
            decodeService = new DecodeServiceBase(new DjvuContext());
            // 生成DJVU文件的图片保存目录
            mDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() +
                    "/djvu/" + MD5Util.encrypt(mPath);
        }
        // 创建一个文档视图
        DocumentView documentView = new DocumentView(this);
        // 设置文档视图的布局参数
        documentView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // 设置解码服务的内容解析器
        decodeService.setContentResolver(getContentResolver());
        // 设置解码服务的内容视图
        decodeService.setContainerView(documentView);
        // 设置文档视图的解码服务
        documentView.setDecodeService(decodeService);
        // 把文档视图添加到框架布局上
        fr_content.addView(documentView);
        // 命令解码服务打开指定路径的电子书
        decodeService.open(Uri.fromFile(new File(mPath)));
        Log.d(TAG, "getPageCount="+decodeService.getPageCount());
        // 根据电子书的总页数生成图片路径队列
        for (int i=0; i<decodeService.getPageCount(); i++) {
            String imgPath = String.format("%s/%03d.jpg", mDir, i);
            imgArray.add(imgPath);
        }
        // 更新数据库中该书籍记录的总页数
        EbookReaderActivity.updatePageCount(mPath, decodeService.getPageCount(), null, null);
        // 延迟100毫秒后启动书籍渲染任务
        mHandler.postDelayed(mBookRender, 100);
    }

    private int mIndex = 0; // 当前书页的序号
    // 定义一个书籍渲染任务
    private Runnable mBookRender = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "getBitmap mIndex="+mIndex);
            // 生成该页图片的保存路径
            final String imgPath = String.format("%s/%03d.jpg", mDir, mIndex);
            if (!(new File(imgPath)).exists()) { // 不存在该页的图片
                // 对该页内容进行解码处理
                decodeService.decodePage(mDir, mIndex, new DecodeService.DecodeCallback() {
                    // 在解码完成时触发
                    public void decodeComplete(final Bitmap bitmap) {
                        // 把位图数据保存为图片文件
                        FileUtil.saveBitmap(imgPath, bitmap);
                        Log.d(TAG, "getBitmap index="+mIndex+",imgPath="+imgPath+",bitmap.getByteCount="+bitmap.getByteCount());
                        doNext(); // 进行下一步处理
                    }
                }, 1, new RectF(0, 0, 1, 1));
            } else { // 存在该页的图片
                doNext(); // 进行下一步处理
            }
        }
    };

    // 进行下一步处理
    private void doNext() {
        mIndex++;
        // 先加载前两个图片，因为ViewPager+Fragment组合初始就是加载前两页。全部加载要花很多时间
        if (mIndex < 2 && mIndex < decodeService.getPageCount()) { // 是前两页
            // 立即启动书籍渲染任务
            mHandler.post(mBookRender);
        } else { // 不是前两页
            // 回到UI主线程操作界面
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showContent(); // 显示书页内容
                }
            });
        }
    }

    // 显示书页内容
    private void showContent() {
        // 下面使用ViewPager展示每页的图片数据
        PdfPageAdapter adapter = new PdfPageAdapter(getSupportFragmentManager(), imgArray);
        vp_content.setAdapter(adapter);
        vp_content.setCurrentItem(0);
        vp_content.setVisibility(View.VISIBLE);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss(); // 关闭进度对话框
        }
    }

}
