package com.example.middle;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by ouyangshen on 2017/9/24.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_relative_xml).setOnClickListener(this);
        findViewById(R.id.btn_relative_code).setOnClickListener(this);
        findViewById(R.id.btn_frame).setOnClickListener(this);
        findViewById(R.id.btn_checkbox).setOnClickListener(this);
        findViewById(R.id.btn_switch_default).setOnClickListener(this);
        findViewById(R.id.btn_switch_ios).setOnClickListener(this);
        findViewById(R.id.btn_radio_horizontal).setOnClickListener(this);
        findViewById(R.id.btn_radio_vertical).setOnClickListener(this);
        findViewById(R.id.btn_spinner_dropdown).setOnClickListener(this);
        findViewById(R.id.btn_spinner_dialog).setOnClickListener(this);
        findViewById(R.id.btn_spinner_icon).setOnClickListener(this);
        findViewById(R.id.btn_edit_simple).setOnClickListener(this);
        findViewById(R.id.btn_edit_cursor).setOnClickListener(this);
        findViewById(R.id.btn_edit_border).setOnClickListener(this);
        findViewById(R.id.btn_edit_hide).setOnClickListener(this);
        findViewById(R.id.btn_edit_jump).setOnClickListener(this);
        findViewById(R.id.btn_edit_auto).setOnClickListener(this);
        findViewById(R.id.btn_act_jump).setOnClickListener(this);
        findViewById(R.id.btn_act_rotate).setOnClickListener(this);
        findViewById(R.id.btn_act_home).setOnClickListener(this);
        findViewById(R.id.btn_act_uri).setOnClickListener(this);
        findViewById(R.id.btn_act_request).setOnClickListener(this);
        findViewById(R.id.btn_text_check).setOnClickListener(this);
        findViewById(R.id.btn_mortgage).setOnClickListener(this);
        findViewById(R.id.btn_alert).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_relative_xml) {
            Intent intent = new Intent(this, RelativeXmlActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_relative_code) {
            Intent intent = new Intent(this, RelativeCodeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_frame) {
            Intent intent = new Intent(this, FrameActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_checkbox) {
            Intent intent = new Intent(this, CheckboxActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_switch_default) {
            Intent intent = new Intent(this, SwitchDefaultActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_switch_ios) {
            Intent intent = new Intent(this, SwitchIOSActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_radio_horizontal) {
            Intent intent = new Intent(this, RadioHorizontalActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_radio_vertical) {
            Intent intent = new Intent(this, RadioVerticalActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_spinner_dropdown) {
            Intent intent = new Intent(this, SpinnerDropdownActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_spinner_dialog) {
            Intent intent = new Intent(this, SpinnerDialogActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_spinner_icon) {
            Intent intent = new Intent(this, SpinnerIconActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_edit_simple) {
            Intent intent = new Intent(this, EditSimpleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_edit_cursor) {
            Intent intent = new Intent(this, EditCursorActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_edit_border) {
            Intent intent = new Intent(this, EditBorderActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_edit_hide) {
            Intent intent = new Intent(this, EditHideActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_edit_jump) {
            Intent intent = new Intent(this, EditJumpActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_edit_auto) {
            Intent intent = new Intent(this, EditAutoActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_act_jump) {
            Intent intent = new Intent(this, ActJumpActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_act_rotate) {
            Intent intent = new Intent(this, ActRotateActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_act_home) {
            Intent intent = new Intent(this, ActHomeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_act_uri) {
            Intent intent = new Intent(this, ActUriActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_act_request) {
            Intent intent = new Intent(this, ActRequestActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_text_check) {
            Intent intent = new Intent(this, TextCheckActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_mortgage) {
            Intent intent = new Intent(this, MortgageActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_alert) {
            Intent intent = new Intent(this, AlertActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_login) {
            Intent intent = new Intent(this, LoginMainActivity.class);
            startActivity(intent);
        }
    }

}
