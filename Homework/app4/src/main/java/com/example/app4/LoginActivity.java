package com.example.app4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button btn_login;
    private Button btn_exit;
    private Button btn_signup;

    AppDatabase db;
    UserDao userDao;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_signup:
                    signup();
                    break;
                case R.id.btn_login:
                    login();
                    break;
                case R.id.btn_exit:
                    finish();
                    break;
            }
        }
    };

    public void signup() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    public void login() {
        String userName = username.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        if (userName.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能未空", Toast.LENGTH_SHORT).show();
        } else if (Util.containsSqlInjection(userName) || Util.containsSqlInjection(userPassword)) {
            Toast.makeText(LoginActivity.this, "输入敏感，重新输入！", Toast.LENGTH_SHORT).show();
        } else {
            User user = userDao.findUser(userName, userPassword);
            if (user == null) {
                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", userName);
                bundle.putString("password", userPassword);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        }
    }

    public void init() {
        username = (EditText) findViewById(R.id.et_username);       //用户名
        password = (EditText) findViewById(R.id.et_password);           //密码
        btn_login = (Button) findViewById(R.id.btn_login);      //选取联系人
        btn_exit = (Button) findViewById(R.id.btn_exit);   //发送按钮
        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_login.setOnClickListener(listener);
        btn_exit.setOnClickListener(listener);
        btn_signup.setOnClickListener(listener);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "mydb")
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.setTitle("登录页");

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