package com.example.thirdsdk.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.alipay.sdk.app.PayTask;
import com.example.thirdsdk.TaxResultActivity;
import com.example.thirdsdk.bean.AlipayConstants;
import com.example.thirdsdk.bean.PayResult;
import com.example.thirdsdk.util.SignUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

public class AlipayTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "AlipayTask";
    private Context mContext;
    private ProgressDialog mDialog;
    private int mType;

    public AlipayTask(Context context, int type) {
        mContext = context;
        mType = type;
    }

    @Override
    protected void onPreExecute() {
        if (TextUtils.isEmpty(AlipayConstants.PARTNER)
                || TextUtils.isEmpty(AlipayConstants.RSA_PRIVATE)
                || TextUtils.isEmpty(AlipayConstants.SELLER)) {
            new AlertDialog.Builder(mContext).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    }).show();
            cancel(true);
        } else {
            mDialog = ProgressDialog.show(mContext, "提示", "正在启动支付宝...");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String orderInfo = getOrderInfo(params[0], params[1], params[2]);

        //特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
        String sign = sign(orderInfo);
        try {
            //仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
        // 构造PayTask 对象
        PayTask alipay = new PayTask((Activity) mContext);
        // 调用支付接口，获取支付结果
        return alipay.pay(payInfo, false);
    }

    @Override
    protected void onPostExecute(String result) {
        if (mDialog != null) {
            mDialog.dismiss();
        }

        PayResult payResult = new PayResult(result);
        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
        String resultInfo = payResult.getResult();
        Toast.makeText(mContext, "resultInfo=" + resultInfo, Toast.LENGTH_SHORT).show();

        String resultStatus = payResult.getResultStatus();
        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
        if (TextUtils.equals(resultStatus, "9000")) {
            Toast.makeText(mContext, "支付宝缴费成功", Toast.LENGTH_SHORT).show();
        } else {
            // 判断resultStatus 为非“9000”则代表可能支付失败
            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
            if (TextUtils.equals(resultStatus, "8000")) {
                Toast.makeText(mContext, "支付宝缴费结果确认中", Toast.LENGTH_SHORT).show();
            } else {
                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                Toast.makeText(mContext, "支付宝缴费失败" + payResult.getResult(), Toast.LENGTH_SHORT).show();
            }
        }
        if (mType == 1) {
            Intent intent = new Intent(mContext, TaxResultActivity.class);
            mContext.startActivity(intent);
        }
    }

    private String getOrderInfo(String subject, String body, String price) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + AlipayConstants.PARTNER + "\"";
        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + AlipayConstants.SELLER + "\"";
        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";
        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";
        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";
        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";
        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";
        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间，默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    private String sign(String content) {
        return SignUtils.sign(content, AlipayConstants.RSA_PRIVATE);
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
