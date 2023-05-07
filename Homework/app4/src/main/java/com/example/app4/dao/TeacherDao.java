package com.example.app4.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app4.entity.Teacher;

import java.util.List;

@Dao
public interface TeacherDao {
    @Query("SELECT * FROM teacher ORDER BY uid")
    List<Teacher> getAll();

    @Insert
    void insert(Teacher teacher);

    @Delete
    void delete(Teacher teacher);

    @Update
    void update(Teacher teacher);


}
