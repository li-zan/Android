package com.example.app4;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorWindow;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.app4.dao.TeacherDao;
import com.example.app4.dao.UserDao;
import com.example.app4.db.AppDatabase;
import com.example.app4.entity.Teacher;
import com.example.app4.entity.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    FloatingActionButton floatingActionButton;
    List<Teacher> teachers;
    MyAdapter adapter;
    AppDatabase db;
    TeacherDao teacherDao;
    UserDao userDao;

    Teacher tmpTeacher;
    int longPosition; //longClick
    int clickPosition; //click


    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            launcher.launch(intent);
        }
    };
    /**
     * addTeacher回传结果处理
     */
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Bundle bundle = data.getExtras();
                        BigBinder bigBinder = (BigBinder) bundle.getBinder("bigBinder");
                        Teacher teacher = new Teacher();
                        teacher.avatar = bigBinder.avatar;
                        teacher.name = bigBinder.name;
                        teacher.description = bigBinder.description;
                        addTeacher(teacher);
                    }
                }
            }
    );

    public void addTeacher(Teacher teacher) {
        teacherDao.insert(teacher);
        adapter.add(teacher);
        adapter.notifyDataSetChanged();
        teachers.clear();
        teachers.addAll(teacherDao.getAll());
    }

    public void deleteTeacher(int position) {
        Teacher teacher = teachers.get(position);
        teachers.remove(position);
        adapter.notifyDataSetChanged();
        teacherDao.delete(teacher);
    }

    public void editTeacher(int position) {
        Log.d("myflag", "" + position);
        tmpTeacher = teachers.get(position);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBinder("bigBinder", new BigBinder(tmpTeacher));
        intent.putExtras(bundle);
        editLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();
                        BigBinder bigBinder =(BigBinder) bundle.getBinder("bigBinder");
                        tmpTeacher.avatar = bigBinder.avatar;
                        tmpTeacher.name = bigBinder.name;
                        tmpTeacher.description = bigBinder.description;
                        teacherDao.update(tmpTeacher);
                        teachers.clear();
                        teachers.addAll(teacherDao.getAll());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
    );

    AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            setLongPosition(position);
            return false;
        }
    };

    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            Bundle bundle = new Bundle();
            BigBinder bigBinder = new BigBinder(teachers.get(position));
            bundle.putBinder("bigBinder", bigBinder);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    public void setLongPosition(int longPosition) {
        this.longPosition = longPosition;
    }

    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
    }

    public void init() {
        listView = (ListView) findViewById(R.id.listView);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.addButton);
        teachers = new ArrayList<>();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "mydb")
                .allowMainThreadQueries()
                .build();
        teacherDao = db.teacherDao();
        userDao = db.userDao();
        teachers = teacherDao.getAll();
        adapter = new MyAdapter(MainActivity.this, R.layout.list_item, teachers);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        floatingActionButton.setOnClickListener(addListener);
        listView.setOnItemLongClickListener(longClickListener);
        listView.setOnItemClickListener(clickListener);

        registerForContextMenu(listView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("操作");
        getMenuInflater().inflate(R.menu.listview_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("⚠警告");
                builder.setMessage("确定删除教师 " + teachers.get(longPosition).name + " 吗？");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTeacher(longPosition);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.menu_edit:
                editTeacher(longPosition);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_edit_password:
                editPassword();
                break;
            case R.id.menu_close_account:
                closeAccount();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void closeAccount() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String name = bundle.getString("name");
        String password = bundle.getString("password");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("⚠警告");
        builder.setMessage("确定注销账户 " + name + " 吗？\n您将无法登录！");
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User user = userDao.findUser(name, password);
                userDao.delete(user);
                Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intent1);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    ActivityResultLauncher<Intent> editPasswordLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Bundle bundle = result.getData().getExtras();
                        User user = userDao.findUser(getIntent().getExtras().getString("name"),
                                getIntent().getExtras().getString("password"));
                        user.password = bundle.getString("new_password");
                        userDao.update(user);
                        //重新登录
                        Toast.makeText(MainActivity.this, "请重新登录..", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
            }
    );

    public void editPassword() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Intent intent1 = new Intent(MainActivity.this, EditPasswordActivity.class);
        intent1.putExtras(bundle);
        editPasswordLauncher.launch(intent1);
    }

}