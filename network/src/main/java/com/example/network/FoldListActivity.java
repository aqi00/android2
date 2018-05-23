package com.example.network;

import java.util.ArrayList;

import com.example.network.adapter.MailExpandAdapter;
import com.example.network.bean.MailBox;
import com.example.network.bean.MailItem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

/**
 * Created by ouyangshen on 2017/11/11.
 */
public class FoldListActivity extends AppCompatActivity {
    private final static String TAG = "FoldListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fold_list);
        initMailBox(); // 初始化整个邮箱
    }

    // 初始化整个邮箱
    private void initMailBox() {
        // 从布局文件中获取名叫elv_list的可折叠列表视图
        ExpandableListView elv_list = findViewById(R.id.elv_list);
        // 以下依次往邮箱队列中添加收件箱、发件箱、草稿箱、废件箱的列表信息
        ArrayList<MailBox> box_list = new ArrayList<MailBox>();
        box_list.add(new MailBox(R.drawable.mail_folder_inbox, "收件箱", getRecvMail()));
        box_list.add(new MailBox(R.drawable.mail_folder_outbox, "发件箱", getSentMail()));
        box_list.add(new MailBox(R.drawable.mail_folder_draft, "草稿箱", getDraftMail()));
        box_list.add(new MailBox(R.drawable.mail_folder_recycle, "废件箱", getRecycleMail()));
        // 构建一个邮箱队列的可折叠列表适配器
        MailExpandAdapter adapter = new MailExpandAdapter(this, box_list);
        // 给elv_list设置邮箱可折叠列表适配器
        elv_list.setAdapter(adapter);
        // 给elv_list设置孙子项的点击监听器
        elv_list.setOnChildClickListener(adapter);
        // 给elv_list设置分组的点击监听器
        elv_list.setOnGroupClickListener(adapter);
        // 默认展开第一个邮件夹，即收件箱
        elv_list.expandGroup(0);
    }

    // 获取收件箱的队列信息
    private ArrayList<MailItem> getRecvMail() {
        ArrayList<MailItem> mail_list = new ArrayList<MailItem>();
        mail_list.add(new MailItem("这里是收件箱呀1", "2018年5月15日"));
        mail_list.add(new MailItem("这里是收件箱呀2", "2018年5月10日"));
        mail_list.add(new MailItem("这里是收件箱呀3", "2018年5月14日"));
        mail_list.add(new MailItem("这里是收件箱呀4", "2018年5月11日"));
        mail_list.add(new MailItem("这里是收件箱呀5", "2018年5月13日"));
        return mail_list;
    }

    // 获取发件箱的队列信息
    private ArrayList<MailItem> getSentMail() {
        ArrayList<MailItem> mail_list = new ArrayList<MailItem>();
        mail_list.add(new MailItem("邮件发出去了吗1", "2018年5月15日"));
        mail_list.add(new MailItem("邮件发出去了吗2", "2018年5月14日"));
        mail_list.add(new MailItem("邮件发出去了吗3", "2018年5月11日"));
        mail_list.add(new MailItem("邮件发出去了吗4", "2018年5月13日"));
        mail_list.add(new MailItem("邮件发出去了吗5", "2018年5月10日"));
        return mail_list;
    }

    // 获取草稿箱的队列信息
    private ArrayList<MailItem> getDraftMail() {
        ArrayList<MailItem> mail_list = new ArrayList<MailItem>();
        mail_list.add(new MailItem("暂时放在草稿箱吧1", "2018年5月14日"));
        mail_list.add(new MailItem("暂时放在草稿箱吧2", "2018年5月11日"));
        mail_list.add(new MailItem("暂时放在草稿箱吧3", "2018年5月15日"));
        mail_list.add(new MailItem("暂时放在草稿箱吧4", "2018年5月10日"));
        mail_list.add(new MailItem("暂时放在草稿箱吧5", "2018年5月13日"));
        return mail_list;
    }

    // 获取废件箱的队列信息
    private ArrayList<MailItem> getRecycleMail() {
        ArrayList<MailItem> mail_list = new ArrayList<MailItem>();
        mail_list.add(new MailItem("怎么被删除了啊1", "2018年5月11日"));
        mail_list.add(new MailItem("怎么被删除了啊2", "2018年5月13日"));
        mail_list.add(new MailItem("怎么被删除了啊3", "2018年5月15日"));
        mail_list.add(new MailItem("怎么被删除了啊4", "2018年5月10日"));
        mail_list.add(new MailItem("怎么被删除了啊5", "2018年5月14日"));
        return mail_list;
    }

}
