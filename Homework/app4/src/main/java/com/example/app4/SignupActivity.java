package com.example.app4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.database.CursorWindow;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app4.dao.UserDao;
import com.example.app4.db.AppDatabase;
import com.example.app4.entity.User;

import java.lang.reflect.Field;

public class SignupActivity extends AppCompatActivity {
    EditText signup_name;
    EditText signup_password;
    Button button;
    AppDatabase db;
    UserDao userDao;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = signup_name.getText().toString().trim();
            String password = signup_password.getText().toString().trim();
            if (Util.containsSqlInjection(name) || Util.containsSqlInjection(password)) {
                Toast.makeText(SignupActivity.this, "输入敏感，重新输入！", Toast.LENGTH_SHORT).show();
            } else {
                User user = userDao.findUserByName(name);
                if (user != null) {
                    Toast.makeText(SignupActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                    signup_name.setText("");
                } else {
                    User user1 = new User();
                    user1.name = name;
                    user1.password = password;
                    userDao.insert(user1);
                    Toast.makeText(SignupActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    public void init() {
        signup_name = (EditText) findViewById(R.id.signup_name);
        signup_password = (EditText) findViewById(R.id.signup_password);
        button = (Button) findViewById(R.id.button3);
        button.setOnClickListener(listener);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "mydb")
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}