package com.exercise.yxty.easyaccount.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.R;

/**
 * Created by Administrator on 2016/4/26.
 */
public class BudgetView extends RelativeLayout {

    private TextView tvType, tvBudget, tvRemain;
    private ImageView ivProgress;

    private MyListener listener;

    public int width,height;
    private FrameLayout frame;

    public BudgetView(Context context) {
        super(context);
        init();
    }

    public BudgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.view_budget, this);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvBudget = (TextView) findViewById(R.id.tv_budget);
        tvRemain = (TextView) findViewById(R.id.budget_remain);
        ivProgress = (ImageView) findViewById(R.id.progress);
        frame = (FrameLayout) findViewById(R.id.frame);
    }

    public void setType(String type) {
        this.tvType.setText(type);
    }

    public void setTvBudget(String Budget) {
        this.tvBudget.setText(Budget);
    }

    public void setTvBugetRemain(String BugetRemain) {
        tvRemain.setText(BugetRemain);
    }

    public void setProgressColor(Drawable background) {
        ivProgress.setBackgroundDrawable(background);
    }

    public void setTvBugetRemainColor(int color) {
        tvRemain.setTextColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = frame.getMeasuredWidth();
        height = frame.getMeasuredHeight();
        if (listener != null) {
            listener.onMeasure();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (listener != null) {
            listener.onLayout();
        }
    }

    public void setListener(MyListener listener) {
        this.listener = listener;
    }
    public interface MyListener {
        void onLayout();

        void onMeasure();
    }


    public void setProgress(float rate) {
//        ViewGroup.LayoutParams layoutParams = ivProgress.getLayoutParams();
//        layoutParams.width = (int) (this.width*rate);
//        ivProgress.setLayoutParams(layoutParams);
////        invalidate();
        if (rate > 0) {
            ivProgress.layout(0, 0, (int) (width * rate), height);
//            Log.i("test", "progress.width" + ivProgress.getWidth());
//            invalidate();
        } else {
            ivProgress.layout(0, 0, (int) (width * 0.01), height);

        }
    }

    public int getProgressWidth() {
        return ivProgress.getWidth();
    }

    public void setProgressVisibility(int visibility) {
        ivProgress.setVisibility(visibility);
    }
}
