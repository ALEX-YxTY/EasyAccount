package com.exercise.yxty.easyaccount.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.exercise.yxty.easyaccount.R;

/**
 * Created by Administrator on 2016/4/10.
 */
public class digit_keypad extends LinearLayout implements View.OnClickListener {

    OnKeyPadClickListener listener;

    public digit_keypad(Context context) {
        super(context);
        initial();
    }


    public digit_keypad(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    private void initial() {
        View.inflate(getContext(), R.layout.view_decimal, this);
        findViewById(R.id.num_0).setOnClickListener(this);
        findViewById(R.id.num_1).setOnClickListener(this);
        findViewById(R.id.num_2).setOnClickListener(this);
        findViewById(R.id.num_3).setOnClickListener(this);
        findViewById(R.id.num_4).setOnClickListener(this);
        findViewById(R.id.num_5).setOnClickListener(this);
        findViewById(R.id.num_6).setOnClickListener(this);
        findViewById(R.id.num_7).setOnClickListener(this);
        findViewById(R.id.num_8).setOnClickListener(this);
        findViewById(R.id.num_9).setOnClickListener(this);
        findViewById(R.id.op_plus).setOnClickListener(this);
        findViewById(R.id.op_minus).setOnClickListener(this);
        findViewById(R.id.op_equals).setOnClickListener(this);
        findViewById(R.id.op_point).setOnClickListener(this);
        findViewById(R.id.op_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            switch (v.getId()) {
                case R.id.num_0:
                    listener.onNumberClicked(0);
                    break;
                case R.id.num_1:
                    listener.onNumberClicked(1);
                    break;
                case R.id.num_2:
                    listener.onNumberClicked(2);
                    break;
                case R.id.num_3:
                    listener.onNumberClicked(3);
                    break;
                case R.id.num_4:
                    listener.onNumberClicked(4);
                    break;
                case R.id.num_5:
                    listener.onNumberClicked(5);
                    break;
                case R.id.num_6:
                    listener.onNumberClicked(6);
                    break;
                case R.id.num_7:
                    listener.onNumberClicked(7);
                    break;
                case R.id.num_8:
                    listener.onNumberClicked(8);
                    break;
                case R.id.num_9:
                    listener.onNumberClicked(9);
                    break;
                case R.id.op_minus:
                    listener.onOperatorClicked("-");
                    break;
                case R.id.op_plus:
                    listener.onOperatorClicked("+");
                    break;
                case R.id.op_equals:
                    listener.onOperatorClicked("=");
                    break;
                case R.id.op_point:
                    listener.onOperatorClicked(".");
                    break;
                case R.id.op_back:
                    listener.onOperatorClicked("back");
                    break;
            }
        }

    }

    public interface OnKeyPadClickListener {
        void onNumberClicked(int number);

        void onOperatorClicked(String operator);
    }

    public void setOnKeyPadClickListener(OnKeyPadClickListener listener) {
        this.listener = listener;
    }
}
