package com.example.junior;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junior.util.Arith;

/**
 * Created by ouyangshen on 2017/9/15.
 */
public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "CalculatorActivity";
    private TextView tv_result; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        // 从布局文件中获取名叫tv_result的文本视图
        tv_result = findViewById(R.id.tv_result);
        // 设置tv_result内部文本的移动方式为滚动形式
        tv_result.setMovementMethod(new ScrollingMovementMethod());
        // 下面给每个按钮控件都注册了点击监听器
        findViewById(R.id.btn_cancel).setOnClickListener(this); // “取消”按钮
        findViewById(R.id.btn_divide).setOnClickListener(this); // “除法”按钮
        findViewById(R.id.btn_multiply).setOnClickListener(this); // “乘法”按钮
        findViewById(R.id.btn_clear).setOnClickListener(this); // “清除”按钮
        findViewById(R.id.btn_seven).setOnClickListener(this); // 数字7
        findViewById(R.id.btn_eight).setOnClickListener(this); // 数字8
        findViewById(R.id.btn_nine).setOnClickListener(this); // 数字9
        findViewById(R.id.btn_plus).setOnClickListener(this); // “加法”按钮
        findViewById(R.id.btn_four).setOnClickListener(this); // 数字4
        findViewById(R.id.btn_five).setOnClickListener(this); // 数字5
        findViewById(R.id.btn_six).setOnClickListener(this); // 数字6
        findViewById(R.id.btn_minus).setOnClickListener(this); // “减法”按钮
        findViewById(R.id.btn_one).setOnClickListener(this); // 数字1
        findViewById(R.id.btn_two).setOnClickListener(this); // 数字2
        findViewById(R.id.btn_three).setOnClickListener(this); // 数字3
        findViewById(R.id.btn_zero).setOnClickListener(this); // 数字0
        findViewById(R.id.btn_dot).setOnClickListener(this); // “小数点”按钮
        findViewById(R.id.btn_equal).setOnClickListener(this); // “等号”按钮
        findViewById(R.id.ib_sqrt).setOnClickListener(this); // “开平方”按钮
    }

    @Override
    public void onClick(View v) {
        int resid = v.getId(); // 获得当前按钮的编号
        String inputText;
        if (resid == R.id.ib_sqrt) { // 如果是开根号按钮
            inputText = "√";
        } else { // 除了开根号按钮之外的其它按钮
            inputText = ((TextView) v).getText().toString();
        }
        Log.d(TAG, "resid=" + resid + ",inputText=" + inputText);
        if (resid == R.id.btn_clear) { // 点击了清除按钮
            clear("");
        } else if (resid == R.id.btn_cancel) { // 点击了取消按钮
            if (operator.equals("")) { // 无操作符，则表示逐位取消前一个操作数
                if (firstNum.length() == 1) {
                    firstNum = "0";
                } else if (firstNum.length() > 0) {
                    firstNum = firstNum.substring(0, firstNum.length() - 1);
                } else {
                    Toast.makeText(this, "没有可取消的数字了", Toast.LENGTH_SHORT).show();
                    return;
                }
                showText = firstNum;
                tv_result.setText(showText);
            } else { // 有操作符，则表示逐位取消后一个操作数
                if (nextNum.length() == 1) {
                    nextNum = "";
                } else if (nextNum.length() > 0) {
                    nextNum = nextNum.substring(0, nextNum.length() - 1);
                } else {
                    Toast.makeText(this, "没有可取消的数字了", Toast.LENGTH_SHORT).show();
                    return;
                }
                showText = showText.substring(0, showText.length() - 1);
                tv_result.setText(showText);
            }
        } else if (resid == R.id.btn_equal) { // 点击了等号按钮
            if (operator.length() == 0 || operator.equals("＝")) {
                Toast.makeText(this, "请输入运算符", Toast.LENGTH_SHORT).show();
                return;
            } else if (nextNum.length() <= 0) {
                Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
                return;
            }
            if (caculate()) { // 计算成功，则显示计算结果
                operator = inputText;
                showText = showText + "=" + result;
                tv_result.setText(showText);
            } else { // 计算失败，则直接返回
                return;
            }
        } else if (resid == R.id.btn_plus || resid == R.id.btn_minus // 点击了加、减、乘、除按钮
                || resid == R.id.btn_multiply || resid == R.id.btn_divide) {
            if (firstNum.length() <= 0) {
                Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
                return;
            }
            if (operator.length() == 0 || operator.equals("＝") || operator.equals("√")) {
                operator = inputText; // 操作符
                showText = showText + operator;
                tv_result.setText(showText);
            } else {
                Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (resid == R.id.ib_sqrt) { // 点击了开根号按钮
            if (firstNum.length() <= 0) {
                Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Double.parseDouble(firstNum) < 0) {
                Toast.makeText(this, "开根号的数值不能小于0", Toast.LENGTH_SHORT).show();
                return;
            }
            // 进行开根号运算
            result = String.valueOf(Math.sqrt(Double.parseDouble(firstNum)));
            firstNum = result;
            nextNum = "";
            operator = inputText;
            showText = showText + "√=" + result;
            tv_result.setText(showText);
            Log.d(TAG, "result=" + result + ",firstNum=" + firstNum + ",operator=" + operator);
        } else { // 点击了其它按钮，包括数字和小数点
            if (operator.equals("＝")) { // 上一次点击了等号按钮，则清空操作符
                operator = "";
                firstNum = "";
                showText = "";
            }
            if (resid == R.id.btn_dot) { // 点击了小数点
                inputText = ".";
            }
            if (operator.equals("")) { // 无操作符，则继续拼接前一个操作数
                firstNum = firstNum + inputText;
            } else { // 有操作符，则继续拼接后一个操作数
                nextNum = nextNum + inputText;
            }
            showText = showText + inputText;
            tv_result.setText(showText);
        }
        return;
    }

    private String operator = ""; // 操作符
    private String firstNum = ""; // 前一个操作数
    private String nextNum = ""; // 后一个操作数
    private String result = ""; // 当前的计算结果
    private String showText = ""; // 显示的文本内容

    // 开始加减乘除四则运算，计算成功则返回true，计算失败则返回false
    private boolean caculate() {
        if (operator.equals("＋")) { // 当前是相加运算
            result = String.valueOf(Arith.add(firstNum, nextNum));
        } else if (operator.equals("－")) { // 当前是相减运算
            result = String.valueOf(Arith.sub(firstNum, nextNum));
        } else if (operator.equals("×")) { // 当前是相乘运算
            result = String.valueOf(Arith.mul(firstNum, nextNum));
        } else if (operator.equals("÷")) { // 当前是相除运算
            if ("0".equals(nextNum)) { // 发现被除数是0
                // 被除数为0，要弹窗提示用户
                Toast.makeText(this, "被除数不能为零", Toast.LENGTH_SHORT).show();
                // 返回false表示运算失败
                return false;
            } else { // 被除数非0，则进行正常的除法运算
                result = String.valueOf(Arith.div(firstNum, nextNum));
            }
        }
        // 把运算结果打印到日志中
        Log.d(TAG, "result=" + result);
        firstNum = result;
        nextNum = "";
        // 返回true表示运算成功
        return true;
    }

    // 清空并初始化
    private void clear(String text) {
        showText = text;
        tv_result.setText(showText);
        operator = "";
        firstNum = "";
        nextNum = "";
        result = "";
    }

}
