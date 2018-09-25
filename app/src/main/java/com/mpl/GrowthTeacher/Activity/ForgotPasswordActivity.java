package com.mpl.GrowthTeacher.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mpl.GrowthTeacher.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView tv_phone;
    private LinearLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        tv_phone = findViewById(R.id.tv_phone);
        String s = "请拨打电话<font color='#2E97F0'>028-99201918</font> 联系客服重置密码";
        tv_phone.setText(Html.fromHtml(s));
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
