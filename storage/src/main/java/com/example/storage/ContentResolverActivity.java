package com.example.storage;

import com.example.storage.bean.Contact;
import com.example.storage.util.CommunicationUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ouyangshen on 2017/12/4.
 */
@SuppressLint("DefaultLocale")
public class ContentResolverActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "ContentResolverActivity";
    private EditText et_contact_name;
    private EditText et_contact_phone;
    private EditText et_contact_email;
    private TextView tv_read_contact;
    private String mContactCount = "";
    private String mContactResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_resolver);
        et_contact_name = findViewById(R.id.et_contact_name);
        et_contact_phone = findViewById(R.id.et_contact_phone);
        et_contact_email = findViewById(R.id.et_contact_email);
        tv_read_contact = findViewById(R.id.tv_read_contact);
        tv_read_contact.setOnClickListener(this);
        findViewById(R.id.btn_add_contact).setOnClickListener(this);
        showContactInfo();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_contact) {
            Contact contact = new Contact();
            contact.name = et_contact_name.getText().toString().trim();
            contact.phone = et_contact_phone.getText().toString().trim();
            contact.email = et_contact_email.getText().toString().trim();
            // 方式一，使用ContentResolver多次写入，每次一个字段
            CommunicationUtil.addContacts(getContentResolver(), contact);
            // 方式二，使用ContentProviderOperation一次写入，每次多个字段
            //CommunicationUtil.addFullContacts(getContentResolver(), contact);
            showContactInfo();
        } else if (v.getId() == R.id.tv_read_contact) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(mContactCount);
            builder.setMessage(mContactResult);
            builder.setPositiveButton("确定", null);
            builder.create().show();
        }
    }

    // 显示所有的联系人信息
    private void showContactInfo() {
        try {
            mContactResult = CommunicationUtil.readAllContacts(getContentResolver());
            String[] split = mContactResult.split("\n");
            int count = (!mContactResult.contains("\n")) ? 0 : split.length;
            mContactCount = String.format("当前共找到%d位联系人", count);
            tv_read_contact.setText(mContactCount);
        } catch (Exception e) {
            Toast.makeText(this, "请检查是否开启了通讯录权限", Toast.LENGTH_SHORT).show();
        }
    }

}
