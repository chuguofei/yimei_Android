package com.yimei.activity.ipqc;
import com.yimei.activity.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PinZhiGuanLi_Activity extends Activity {

	private Button pinzhi_shoujian,pinzhi_xunjian;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pinzhiguanli);
		
		pinzhi_shoujian = (Button) findViewById(R.id.pinzhi_shoujian);
		pinzhi_xunjian = (Button) findViewById(R.id.pinzhi_xunjian);
		pinzhi_shoujian.setOnClickListener(OnClick);
		pinzhi_xunjian.setOnClickListener(OnClick);
		
	}

	OnClickListener OnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.pinzhi_shoujian) {
				startActivity(new Intent(PinZhiGuanLi_Activity.this,IPQC_shoujian.class));
			}
			if (v.getId() == R.id.pinzhi_xunjian) {
				startActivity(new Intent(PinZhiGuanLi_Activity.this,IPQC_xunjian.class));
			}
		}
	};
}
