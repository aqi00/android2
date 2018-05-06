package com.example.mixture;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.mixture.adapter.BookListAdapter;
import com.example.mixture.bean.BookInfo;
import com.example.mixture.database.BookDBHelper;
import com.example.mixture.service.CopyFileService;
import com.example.mixture.util.FileUtil;
import com.example.mixture.widget.InputDialogFragment;
import com.example.mixture.widget.InputDialogFragment.InputCallbacks;

import java.util.ArrayList;
import java.util.Map;

public class EbookReaderActivity extends AppCompatActivity implements
        OnClickListener, OnItemClickListener, OnItemLongClickListener,
        FileSelectCallbacks, InputCallbacks {
    private static final String TAG = "EbookReaderActivity";
    private ListView lv_ebook; // 声明一个用于展示书籍列表的列表视图对象
    private ArrayList<String> pathArray = new ArrayList<String>(); // 电子书路径队列
    private ArrayList<BookInfo> bookList = new ArrayList<BookInfo>(); // 书籍信息队列
    public static BookDBHelper helper; // 声明一个书籍数据库的帮助器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook_reader);
        findViewById(R.id.btn_open).setOnClickListener(this);
        lv_ebook = findViewById(R.id.lv_ebook);
        // 获得书籍数据库的唯一实例
        helper = BookDBHelper.getInstance(this, 1);
        helper.openLink(); // 打开数据库连接
        String dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        // 下面是三个演示用的电子书样例，可在assets目录下找到
        pathArray.add(dir + "/pdf/tangshi.pdf");
        pathArray.add(dir + "/epub/lunyu.epub");
        pathArray.add(dir + "/djvu/zhugeliang.djvu");
        // 延迟100毫秒后启动电子书复制任务
        new Handler().postDelayed(mCopyService, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.closeLink(); // 关闭数据库连接
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBookList(); // 初始化书籍列表
    }

    // 定义一个电子书复制任务
    private Runnable mCopyService = new Runnable() {
        @Override
        public void run() {
            // 无法直接从asset目录读取pdf/epub/djvu文件，只能先把这些文件复制到SD卡，再从SD卡读取文件
            // 下面启动复制电子书的后台异步服务
            Intent intent = new Intent(EbookReaderActivity.this, CopyFileService.class);
            intent.putStringArrayListExtra("file_list", pathArray);
            startService(intent);
        }
    };

    // 初始化书籍列表
    private void initBookList() {
        // 查询数据库中所有书籍记录
        bookList = helper.query("1=1");
        if (bookList.size() <= 0) {
            for (String path : pathArray) {
                bookList.add(new BookInfo(path));
            }
        }
        // 下面把书籍记录通过ListView展现出来
        BookListAdapter adapter = new BookListAdapter(this, bookList);
        lv_ebook.setAdapter(adapter);
        lv_ebook.setOnItemClickListener(this);
        lv_ebook.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open) {
            String[] bookExs = new String[]{"pdf", "epub", "djvu"};
            // 打开文件选择对话框
            FileSelectFragment.show(this, bookExs, null);
        }
    }

    // 在点击书籍记录时触发
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 区分不同的阅读器，并跳到对应的阅览页面
        diffReader(bookList.get(position).path, bookList.get(position).title);
    }

    // 在长按书籍记录时触发
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // 弹出输入对话框，用于手动修改书籍标题
        InputDialogFragment dialog = InputDialogFragment.newInstance(
                bookList.get(position).path, position, "请输入书籍名称");
        String fragTag = getResources().getString(R.string.app_name);
        dialog.show(getFragmentManager(), fragTag);
        return true;
    }

    // SSID对应文件路径，password对应书籍名称，type对应位置序号
    // 在输入对话框上面点击确定按钮后触发
    public void onInput(String SSID, String password, int type) {
        BookInfo book = bookList.get(type);
        book.title = password;
        // 更新数据库中该书籍记录的标题
        updatePageCount(book.path, book.page_number, book.title, "");
    }

    // 点击文件选择对话框的确定按钮后触发
    public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
        // 拼接文件的完整路径
        String path = String.format("%s/%s", absolutePath, fileName);
        Log.d(TAG, "path=" + path);
        // 往数据库中添加一条书籍记录
        helper.insert(new BookInfo(path));
        // 区分不同的阅读器，并跳到对应的阅览页面
        diffReader(path, "");
    }

    // 检查文件是否合法时触发
    public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
        return true;
    }

    // 区分不同的阅读器，并跳到对应的阅览页面
    private void diffReader(String path, String title) {
        String extendName = FileUtil.getExtendName(path);
        if (extendName.equals("pdf")) { // PDF格式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 跳转到Android自带的PDF阅览页面
                startReader(path, title, PdfRenderActivity.class);
            } else {
                // 跳转到第三方Vudroid提供的阅览页面
                startReader(path, title, VudroidActivity.class);
            }
        } else if (extendName.equals("epub")) { // EPUB格式
            // 跳转到EPUB阅览页面
            startReader(path, title, EpubActivity.class);
        } else if (extendName.equals("djvu")) { // DJVU格式
            // 跳转到第三方Vudroid提供的阅览页面
            startReader(path, title, VudroidActivity.class);
        } else {
            Toast.makeText(this, "暂不支持该格式的电子书", Toast.LENGTH_SHORT).show();
        }
    }

    // 启动指定的电子书阅览页面
    private void startReader(String path, String title, Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("path", path);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    // 更新数据库中该书籍记录的总页数
    public static void updatePageCount(String path, int count, String title, String author) {
        // 根据文件路径查询书籍记录
        BookInfo info = helper.queryByPath(path);
        if (info != null) {
            info.page_number = count;
            if (!TextUtils.isEmpty(title)) {
                info.title = title;
            }
            if (!TextUtils.isEmpty(author)) {
                info.author = author;
            }
            // 更新书籍信息
            helper.update(info);
        }
    }
}
