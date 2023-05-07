package com.example.app4;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.app4.entity.Teacher;

public class EditActivity extends AppCompatActivity {
    ImageView imageView;
    EditText edt_name;
    EditText edt_description;
    Button button;
    String name;
    String description;
    String avatar;
    Teacher teacher;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            name = edt_name.getText().toString().trim();
            description = edt_description.getText().toString().trim();
            if (avatar == null || name.isEmpty() || description.isEmpty() || avatar.isEmpty()) {
                Toast.makeText(EditActivity.this, "请完整信息！", Toast.LENGTH_SHORT).show();
            } else {
                teacher.name = name;
                teacher.avatar = avatar;
                teacher.description = description;
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                BigBinder binder = new BigBinder(teacher);
                bundle.putBinder("bigBinder", binder);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    View.OnClickListener imgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        }
    };

    ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        launcher.launch("image/*");
                    } else {
                        Toast.makeText(EditActivity.this, "未授予图片读取相关权限！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result == null) return;
                    imageView.setImageURI(result);
                    avatar = Util.encode(getContentResolver(), result);
                }
            }
    );

    public void init() {
        imageView = (ImageView) findViewById(R.id.imageView);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_description = (EditText) findViewById(R.id.edt_description);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(listener);
        imageView.setOnClickListener(imgListener);
        //接收传递而来的信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        BigBinder bigBinder =(BigBinder) bundle.getBinder("bigBinder");
        avatar = bigBinder.avatar;
        name = bigBinder.name;
        description = bigBinder.description;
        teacher = new Teacher();
        teacher.setUid(bigBinder.uid);

        imageView.setImageBitmap(Util.decode(avatar));
        edt_name.setText(name);
        edt_description.setText(description);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("编辑页");

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