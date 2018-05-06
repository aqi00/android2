package com.example.mixture;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.epub.EpubReader;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.example.mixture.adapter.EpubPagerAdapter;
import com.example.mixture.util.FileUtil;
import com.example.mixture.util.MD5Util;

public class EpubActivity extends AppCompatActivity {
    private final static String TAG = "EpubActivity";
    private TextView tv_title;
    private ViewPager vp_content; // 声明一个翻页视图对象
    private Handler mHandler = new Handler(); // 声明一个处理器对象
    private String mTitle; // 书籍标题
    private String mDir, mPath; // 文件目录和文件路径
    private ProgressDialog mDialog; // 声明一个进度对话框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub);
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
        tv_title = findViewById(R.id.tv_title);
        // 从布局文件中获取名叫vp_content的翻页视图
        vp_content = findViewById(R.id.vp_content);
        // 从布局视图中获取名叫pts_tab的翻页标题栏
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        // 设置翻页标题栏的文本大小
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        // 从前一个页面传来的意图中获取名叫title的书籍标题
        mTitle = getIntent().getStringExtra("title");
        // 从前一个页面传来的意图中获取名叫path的文件路径
        mPath = getIntent().getStringExtra("path");
        // 生成电子书解析后的文件存放目录
        mDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() +
                "/epub/" + MD5Util.encrypt(mPath);
        // 弹出进度对话框
        mDialog = ProgressDialog.show(this, "请稍候", "正在努力加载");
        // 延迟100毫秒后启动书籍渲染任务
        mHandler.postDelayed(new BookRender(), 100);
    }

    // 定义一个书籍渲染任务
    private class BookRender implements Runnable {
        @Override
        public void run() {
            renderEPUB(); // 开始渲染EPUB文件
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss(); // 关闭进度对话框
            }
        }
    }

    // 开始渲染EPUB文件
    private void renderEPUB() {
        // 创建一个EPUB阅读器对象
        EpubReader epubReader = new EpubReader();
        Book book = null;
        try {
            // 从指定文件路径创建输入流对象
            InputStream inputStr = new FileInputStream(mPath);
            // 从输入流中读取书籍数据
            book = epubReader.readEpub(inputStr);
            // 设置书籍的概要描述
            setBookMeta(book);
            // 获取该书的所有资源，包括网页、图片等等
            Resources resources = book.getResources();
            // 获取所有的链接地址
            Collection<String> hrefArray = resources.getAllHrefs();
            for (String href : hrefArray) {
                // 获取该链接指向的资源
                Resource res = resources.getByHref(href);
                // 把资源的字节数组保存为文件
                FileUtil.writeFile(mDir + "/" + href, res.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> htmlArray = new ArrayList<String>();
        // 获取该书的所有内容页，也就是所有网页
        List<Resource> contents = book.getContents();
        for (int i = 0; i < contents.size(); i++) {
            // 获取该网页的链接地址，并添加到网页队列中
            String href = String.format("%s/%s", mDir, contents.get(i).getHref());
            htmlArray.add(href);
        }
        // 下面使用ViewPager展示每页的WebView内容
        EpubPagerAdapter adapter = new EpubPagerAdapter(getSupportFragmentManager(), htmlArray);
        vp_content.setAdapter(adapter);
        vp_content.setCurrentItem(0);
        vp_content.setVisibility(View.VISIBLE);
    }

    // 设置书籍的概要描述
    private void setBookMeta(Book book) {
        // 书籍的头部信息，可获取标题、语言、作者、封面等信息
        Metadata meta = book.getMetadata();
        // 获取该书的作者列表
        List<Author> authorArray = meta.getAuthors();
        String autors = "作者：";
        for (int i = 0; i < authorArray.size(); i++) {
            if (i == 0) {
                autors = String.format("%s%s", autors, authorArray.get(i).toString());
            } else {
                autors = String.format("%s, %s", autors, authorArray.get(i).toString());
            }
        }
        autors = autors.replace(",", "");
        // 获取该书的主标题
        String title = meta.getFirstTitle();
        if (TextUtils.isEmpty(title)) {
            if (!TextUtils.isEmpty(mTitle)) {
                title = mTitle;
            } else {
                title = FileUtil.getFileName(mPath);
            }
        }
        // 获取该书的页数，同时更新数据库中该书信息
        EbookReaderActivity.updatePageCount(mPath,
                book.getContents().size(), title, autors);
        String fullTitle = String.format("%s（%s）", title, autors);
        tv_title.setText(fullTitle);
    }

}
