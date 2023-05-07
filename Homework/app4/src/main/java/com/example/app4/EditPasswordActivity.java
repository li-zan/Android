package com.example.app4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.CursorWindow;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app4.db.AppDatabase;
import com.example.app4.entity.User;

import java.lang.reflect.Field;

public class EditPasswordActivity extends AppCompatActivity {
    EditText old_password;
    EditText new_password;
    EditText confirm_password;
    Button button;
    String oldPassword;
    String newPassword;
    String confirmPassword;
    Bundle bundle;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            oldPassword = old_password.getText().toString().trim();
            newPassword = new_password.getText().toString().trim();
            confirmPassword = confirm_password.getText().toString().trim();
            if (!bundle.getString("password").equals(oldPassword)) {
                Toast.makeText(EditPasswordActivity.this, "旧密码不正确！", Toast.LENGTH_SHORT).show();
            } else if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(EditPasswordActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(EditPasswordActivity.this, "新密码与确认密码不一致！", Toast.LENGTH_SHORT).show();
            } else if (Util.containsSqlInjection(newPassword)) {
                Toast.makeText(EditPasswordActivity.this, "输入敏感，重新输入！", Toast.LENGTH_SHORT).show();
            } else {
                bundle.putString("new_password", newPassword);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    public void init() {
        old_password = (EditText) findViewById(R.id.old_password);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_password = (EditText) findViewById(R.id.confim_password);
        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(listener);
        bundle = getIntent().getExtras();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //数据库行长超过默认大小会抛异常
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }

        init();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}