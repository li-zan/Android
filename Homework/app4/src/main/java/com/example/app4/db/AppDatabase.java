package com.example.app4.db;


import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.app4.dao.TeacherDao;
import com.example.app4.dao.UserDao;
import com.example.app4.entity.Teacher;
import com.example.app4.entity.User;

@Database(entities = {Teacher.class, User.class}, version = 2) //, autoMigrations = {@AutoMigration(from = 1, to = 2)}
public abstract class AppDatabase extends RoomDatabase {
    public abstract TeacherDao teacherDao();
    public abstract UserDao userDao();

}
