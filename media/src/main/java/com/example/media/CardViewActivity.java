package com.example.media;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by ouyangshen on 2017/12/4.
 */
public class CardViewActivity extends AppCompatActivity {
    private CardView cv_card; // 声明一个卡片视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        // 从布局文件中获取名叫cv_card的卡片视图
        cv_card = findViewById(R.id.cv_card);
        initCardSpinner(); // 初始化卡片类型下拉框
    }

    // 初始化卡片类型下拉框
    private void initCardSpinner() {
        ArrayAdapter<String> cardAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, cardArray);
        Spinner sp_card = findViewById(R.id.sp_card);
        sp_card.setPrompt("请选择卡片视图类型");
        sp_card.setAdapter(cardAdapter);
        sp_card.setOnItemSelectedListener(new CardSelectedListener());
        sp_card.setSelection(0);
    }

    private String[] cardArray = {"圆角与阴影均为3", "圆角与阴影均为6", "圆角与阴影均为10",
            "圆角与阴影均为15", "圆角与阴影均为20", "圆角与阴影均为30", "圆角与阴影均为50"};
    private int[] radiusArray = {3, 6, 10, 15, 20, 30, 50};
    class CardSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            int interval = radiusArray[arg2];
            // 设置卡片视图的圆角半径
            cv_card.setRadius(interval);
            // 设置卡片视图的阴影长度
            cv_card.setCardElevation(interval);
            MarginLayoutParams params = (MarginLayoutParams) cv_card.getLayoutParams();
            params.setMargins(interval, interval, interval, interval);
            // 设置卡片视图的布局参数
            cv_card.setLayoutParams(params);
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
