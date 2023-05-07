package com.example.app4;

import android.os.Binder;

import com.example.app4.entity.Teacher;

/**
 * bundle.putBinder使用
 * avatar编码base64后太大bundle.putString传oom
 * 自定义binder传大容量
 */
public class BigBinder extends Binder {
    public int uid;
    public String avatar;
    public String name;
    public String description;

    public BigBinder(String avatar, String name, String description) {
        this.avatar = avatar;
        this.name = name;
        this.description = description;
    }
    public BigBinder(int uid, String avatar, String name, String description) {
        this(avatar, name, description);
        this.uid = uid;
    }

    public BigBinder(Teacher teacher) {
        this(teacher.uid, teacher.avatar, teacher.name, teacher.description);
    }

}
