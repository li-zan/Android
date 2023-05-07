package com.example.app4;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app4.entity.Teacher;

import java.util.List;

public class MyAdapter extends ArrayAdapter<Teacher> {
    private int resourceId;

    public MyAdapter(@NonNull Context context, int resource, @NonNull List<Teacher> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView avatarImage = (ImageView) view.findViewById(R.id.avatar);
        TextView name = (TextView) view.findViewById(R.id.name);
        Teacher teacher = getItem(position);
        name.setText(teacher.getName());
        String avatar = teacher.getAvatar();
        Bitmap bitmap = Util.decode(avatar);
        avatarImage.setImageBitmap(bitmap);

        return view;
    }


}
