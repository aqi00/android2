package com.example.mixture;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.mixture.adapter.PdfPageAdapter;
import com.example.mixture.util.AssetsUtil;
import com.example.mixture.util.FileUtil;
import com.example.mixture.util.MD5Util;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressLint("DefaultLocale")
public class PdfRenderActivity extends AppCompatActivity implements OnClickListener, FileSelectCallbacks {
    private final static String TAG = "PdfRenderActivity";
    private TextView tv_title;
    private ViewPager vp_content; // 声明一个翻页视图对象
    private String mTitle; // 书籍标题
    private String mDir, mPath, mOriginPath; // 文件目录、文件路径，以及原始路径
    private Handler mHandler = new Handler(); // 声明一个处理器对象
    private ProgressDialog mDialog; // 声明一个进度对话框对象
    private ArrayList<String> imgArray = new ArrayList<String>(); // 图片路径队列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_render);
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
        // 从布局文件中获取名叫vp_content的翻页视图
        vp_content = findViewById(R.id.vp_content);
        // 从布局视图中获取名叫pts_tab的翻页标题栏
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        // 设置翻页标题栏的文本大小
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        tv_title = findViewById(R.id.tv_title);
        findViewById(R.id.btn_open).setOnClickListener(this);
        // 从前一个页面传来的意图中获取快递包裹
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null && !bundle.isEmpty()) { // 包裹非空
            mTitle = bundle.getString("title");
            mOriginPath = bundle.getString("path");
            // 生成PDF文件的图片保存目录
            mDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() +
                    "/pdf/" + MD5Util.encrypt(mOriginPath);
            // 生成PDF文件的保存目录
            mPath = mDir + "/" + FileUtil.getFileName(mOriginPath);
            // 无法直接从asset目录读取PDF文件，只能先把PDF文件复制到SD卡，再从SD卡读取PDF
            AssetsUtil.Assets2Sd(this, FileUtil.getFileName(mPath), mPath);
            readPDF(); // 读取PDF文件
            findViewById(R.id.btn_open).setVisibility(View.GONE);
        } else { // 包裹是空的
            findViewById(R.id.btn_open).setVisibility(View.VISIBLE);
        }
    }

    // 读取PDF文件
    private void readPDF() {
        if (!TextUtils.isEmpty(mTitle)) {
            tv_title.setText(mTitle);
        } else {
            // 从文件路径中获取文件名称
            tv_title.setText(FileUtil.getFileName(mPath));
        }
        // 弹出进度对话框
        mDialog = ProgressDialog.show(this, "请稍候", "正在努力加载");
        // 延迟100毫秒后启动书籍渲染任务
        mHandler.postDelayed(new BookRender(), 100);
    }

    // 定义一个书籍渲染任务
    private class BookRender implements Runnable {
        @Override
        public void run() {
            imgArray.clear(); // 清空图片路径队列
            renderPDF(); // 开始渲染PDF文件
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss(); // 关闭进度对话框
            }
        }
    }

    // 开始渲染PDF文件
    private void renderPDF() {
        try {
            // 打开存储卡里指定路径的PDF文件
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(
                    new File(mPath), ParcelFileDescriptor.MODE_READ_ONLY);
            // 创建一个PDF渲染器
            PdfRenderer pdfRenderer = new PdfRenderer(pfd);
            Log.d(TAG, "page count=" + pdfRenderer.getPageCount());
            // 依次处理PDF文件的每个页面
            for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                // 生成该页图片的保存路径
                String imgPath = String.format("%s/%03d.jpg", mDir, i);
                imgArray.add(imgPath);
                // 打开序号为i的页面
                PdfRenderer.Page page = pdfRenderer.openPage(i);
                // 创建该页面的临时位图
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),
                        Bitmap.Config.ARGB_8888);
                // 渲染该PDF页面并写入到临时位图
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                // 把位图对象保存为图片文件
                FileUtil.saveBitmap(imgPath, bitmap);
                page.close(); // 关闭该PDF页面
            }
            // 更新数据库记录的该文件页数
            EbookReaderActivity.updatePageCount(mOriginPath, pdfRenderer.getPageCount(), null, null);
            pdfRenderer.close(); // 处理完毕，关闭PDF渲染器
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        // 下面将解析出来的PDF页面组图通过ViewPager显示出来
        PdfPageAdapter adapter = new PdfPageAdapter(getSupportFragmentManager(), imgArray);
        vp_content.setAdapter(adapter);
        vp_content.setCurrentItem(0);
        vp_content.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open) {
            // 打开文件选择对话框
            FileSelectFragment.show(this, new String[]{"pdf"}, null);
        }
    }

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        // 拼接文件的完整路径
        mPath = String.format("%s/%s", absolutePath, fileName);
        // 生成PDF文件的图片保存目录
        mDir = absolutePath + "/pdf/" + MD5Util.encrypt(mPath);
        Log.d(TAG, "path=" + mPath);
        readPDF(); // 读取PDF文件
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

    // 在创建选项菜单时调用
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        return true;
    }

    // 在选中菜单项时调用
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // 点击了工具栏左侧的返回图标
            finish();
        } else if (imgArray==null || imgArray.size()<=0) {
            Toast.makeText(this, "请先打开PDF文件", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.menu_slider) { // 点击了“滑动浏览”菜单项
            // 携带图片路径队列，跳转到滑动浏览页面
            Intent intent = new Intent(this, PdfSliderActivity.class);
            intent.putStringArrayListExtra("img_list", imgArray);
            intent.putExtra("path", mPath);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_turn) { // 点击了“卷动浏览”菜单项
            // 携带图片路径队列，跳转到卷动浏览页面
            Intent intent = new Intent(this, PdfTurnActivity.class);
            intent.putStringArrayListExtra("img_list", imgArray);
            intent.putExtra("path", mPath);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
