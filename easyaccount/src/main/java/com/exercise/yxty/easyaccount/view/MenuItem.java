package com.exercise.yxty.easyaccount.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.R;

/**
 * Created by Administrator on 2016/4/2.
 */
public class MenuItem extends RelativeLayout{

    ImageView ivIcon;
    TextView tvTitle;

    public MenuItem(Context context) {
        super(context);
        initial();
    }


    public MenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    private void initial() {
        View.inflate(getContext(), R.layout.menu_item_main, this);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        tvTitle = (TextView) findViewById(R.id.tv_icon);
    }

    public void setIcon(int resourceId) {
        ivIcon.setImageResource(resourceId);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }
}
