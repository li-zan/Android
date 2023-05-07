package com.example.app4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    ImageView img_detail_avatar;
    TextView txt_detail_description;


    public void init() {
        img_detail_avatar = (ImageView) findViewById(R.id.img_detail_avatar);
        txt_detail_description = (TextView) findViewById(R.id.txt_detail_description);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        BigBinder bigBinder = (BigBinder) bundle.getBinder("bigBinder");
        img_detail_avatar.setImageBitmap(Util.decode(bigBinder.avatar));
        String name = bigBinder.name;
        String description = name + " " + bigBinder.description;
        SpannableString spannableString = new SpannableString(description);
        spannableString.setSpan(new RelativeSizeSpan(1.8f), 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        txt_detail_description.setText(spannableString);
//        txt_detail_description.setText(description);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        init();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


}