package com.example.joongwon.m_a;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

// 형식과 레이아웃이 동일해서 같이 사용하는거고
// 값만 따로 불러와 어댑터 이용해서 사용

public class PermissionListItemView extends LinearLayout {
    TextView text1, text2, text3, text4;

    public PermissionListItemView(Context context) {
        super(context);
        init(context);
    }

    public PermissionListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_permission, this, true);

        text1 = findViewById(R.id.textView);
        text2 = findViewById(R.id.textView2);
        text3 = findViewById(R.id.textView3);
        text4 = findViewById(R.id.textView4);
    }

    public void setText1(String id) {
        this.text1.setText(id);
    }

    public void setText2(String name) {
        this.text2.setText(name);
    }

    public void setText3(String birthday) {
        this.text3.setText(birthday);
    }

    public void setText4(String permission) {
        this.text4.setText(permission);
    }
}

