package net.sourceforge.simcpux.wxapi;

import net.sourceforge.simcpux.R;

import com.example.weixin.bean.WechatConstants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelbase.BaseResp.ErrCode;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
	private static final String TAG = "WXPayEntryActivity";
	private IWXAPI api;
	private TextView tv_result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wxpay_result);
		tv_result = (TextView) findViewById(R.id.tv_result);
		api = WXAPIFactory.createWXAPI(this, WechatConstants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onResp, errCode = " + resp.errCode);
		String result = "";
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			switch (resp.errCode) {
			case ErrCode.ERR_OK:
				result = "微信支付成功";
				break;
			case ErrCode.ERR_COMM:
				result = "微信支付失败：" + resp.errCode + "，" + resp.errStr;
				break;
			case ErrCode.ERR_USER_CANCEL:
				result = "微信支付取消：" + resp.errCode + "，" + resp.errStr;
				break;
			default:
				result = "微信支付未知异常：" + resp.errCode + "，" + resp.errStr;
				break;
			}
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		tv_result.setText(result);
	}

}
