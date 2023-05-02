package com.example.projecttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    FragmentAdapter fragmentAdapter;
    BroadcastReceiver br;

    boolean loginStauts = false;     //登录状态，先假设为true，这样可以看其他两个模块


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1. 设置为全屏模式（隐藏状态条）
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //2. ActionBar显示返回按钮
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //set the status bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.teal_700));

        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(fragmentAdapter);

        viewPager2.setUserInputEnabled(false); //禁止默认页面拖拽，只有成功登录才可拖拽

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                loginStauts = sp.getBoolean("loginStatus", false);
                if(loginStauts){
                    viewPager2.setCurrentItem(tab.getPosition());   //已登录情况
                }else{
                    if (tab.getPosition() == 0) {
                        //设置判断的目的是：消除因默认行为回调导致的提醒重复问题
                        //在未登录的情况下点击其他模块，会提醒两次请登录，因为第一次回调执行最后一行选择‘登录’标签会再次触发回调
                        Toast.makeText(MainActivity.this, "请登录", Toast.LENGTH_SHORT).show();
                    }
                    viewPager2.setCurrentItem(0);    //未登录时只显示第一个Fragement
                    tabLayout.selectTab(tabLayout.getTabAt(0));
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("flag", "pos=" + position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        //动态注册广播接收器
        br = new OffLineReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.offline");
        registerReceiver(br, intentFilter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("loginStatus", false);
        editor.apply();
    }
}