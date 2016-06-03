package com.github.quoctrung66.osmnavigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.quoctrung66.osmnavigation.Handler.HandleView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Kiểm tra kết nối đến GPS và Network
        new HandleView(MainActivity.this).inicheck();
    }
}
