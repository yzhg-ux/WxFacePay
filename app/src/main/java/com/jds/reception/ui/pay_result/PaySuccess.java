package com.jds.reception.ui.pay_result;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jds.reception.R;
import com.jds.reception.ui.main.MainActivity;

/**
 * 类 名: PaySuccess
 * 作 者: yzhg
 * 创 建: 2019/5/24 0024
 * 版 本: 2.0
 * 历 史: (版本) 作者 时间 注释
 * 描 述:
 */
public class PaySuccess extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pay_result);
        findViewById(R.id.tvReturnPay).setOnClickListener(v -> {
            Intent intent = new Intent(PaySuccess.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
