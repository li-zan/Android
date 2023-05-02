package com.example.projecttemplate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOne#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOne extends Fragment {

    private EditText username;
    private EditText password;
    private Button btn_login;
    private Button btn_exit;

    boolean loginStauts = false;  //登录状态

    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_login:
                    //用户名、密码自拟，登录成功后loginStauts设置为true，并写入xml文件
                    if (username.getText().toString().equals("lz") && password.getText().toString().equals("123")) {
                        loginStauts = true;
                        Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).viewPager2.setUserInputEnabled(true);
                    } else {
                        loginStauts = false;
                        Toast.makeText(getContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).viewPager2.setUserInputEnabled(false);
                    }
                    SharedPreferences sp = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("loginStatus", loginStauts);
                    editor.apply();
                    clearInfo();
                    if (loginStauts) ((MainActivity)getActivity()).viewPager2.setCurrentItem(1);
                    break;
                case R.id.btn_exit:
                    getActivity().finish();
                    break;
            }
        }
    };


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentOne() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTwo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOne newInstance(String param1, String param2) {
        FragmentOne fragment = new FragmentOne();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void clearInfo() {
        if (username != null && password != null) {
            username.setText("");
            password.setText("");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        username = view.findViewById(R.id.et_username);       //用户名
        password = view.findViewById(R.id.et_password);           //密码
        btn_login = view.findViewById(R.id.btn_login);      //选取联系人
        btn_exit = view.findViewById(R.id.btn_exit);   //发送按钮

        btn_login.setOnClickListener(listener);
        btn_exit.setOnClickListener(listener);

        return view;
    }


}