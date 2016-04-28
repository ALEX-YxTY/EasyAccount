package com.exercise.yxty.easyaccount.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.R;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/4/2.
 */
public class ListItemUP extends RelativeLayout {

    TextView tvTitle, tvContent;

    public ListItemUP(Context context) {
        super(context);
        initial();
    }

    public ListItemUP(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    private void initial() {
        View.inflate(getContext(), R.layout.listview_item_up_main, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setContent(double number) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        tvContent.setText("¥ "+ df.format(number));
    }
}
