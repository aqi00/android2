package com.example.thirdsdk;

import com.example.thirdsdk.task.AlipayTask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * Created by ouyangshen on 2017/12/18.
 */
public class AlipayActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "AlipayActivity";
    private EditText et_goods_title;
    private EditText et_goods_desc;
    private EditText et_goods_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay);
        et_goods_title = findViewById(R.id.et_goods_title);
        et_goods_desc = findViewById(R.id.et_goods_desc);
        et_goods_price = findViewById(R.id.et_goods_price);
        findViewById(R.id.btn_alipay).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_alipay) {
            String title = et_goods_title.getText().toString();
            String desc = et_goods_desc.getText().toString();
            String price = et_goods_price.getText().toString();
            new AlipayTask(this, 0).execute(title, desc, price);
        }
    }

}
