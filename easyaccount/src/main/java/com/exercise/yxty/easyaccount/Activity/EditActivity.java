package com.exercise.yxty.easyaccount.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exercise.yxty.easyaccount.Fragment.ExpenseFragment;
import com.exercise.yxty.easyaccount.Fragment.IncomeFragment;
import com.exercise.yxty.easyaccount.Fragment.TransFragment;
import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.beans.BillBean;
import com.exercise.yxty.easyaccount.db.EasyAccountDAO;

import java.util.List;

public class EditActivity extends FragmentActivity {

    TextView tvTitle;
    ImageView ivArrow;

    Fragment expendFragment, incomeFragment, transferFragment;
    BillBean billBean;

    EasyAccountDAO dao;

    ModifyAndDeleteListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initFrame();
    }

    private void initUI() {
        setContentView(R.layout.activity_edit);
        tvTitle = (TextView) findViewById(R.id.title);
        ivArrow = (ImageView) findViewById(R.id.iv_arrow);
    }

    private void initFrame() {
        //准备fragmentManger
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction tf = fragmentManager.beginTransaction();
        //1.根据startIntent中的date和fee确定唯一的bill记录
        dao = new EasyAccountDAO(this);
        Intent startIntent = getIntent();
        String date = startIntent.getStringExtra("DATE");
        List<BillBean> billBeans = dao.getBillBean(date);
        //2.根据bill中in_out值加载fragment，并设置初始显示
        if (billBeans.size() != 1) {
            Toast.makeText(this, "数据有误", Toast.LENGTH_SHORT).show();
        } else {
            billBean = billBeans.get(0);
            //IN_OUT 1——支出，0——收入，2——转账
            switch (billBean.getIn_out()) {
                case 0:
                    incomeFragment = new IncomeFragment();
                    tf.add(R.id.frame, incomeFragment).commit();
                    listener = (ModifyAndDeleteListener) incomeFragment;
                    listener.setData(billBean);
                    tvTitle.setText("编辑收入");
                    break;
                case 1:
                    expendFragment = new ExpenseFragment();
                    tf.add(R.id.frame, expendFragment).commit();
                    listener = (ModifyAndDeleteListener) expendFragment;
                    listener.setData(billBean);
                    tvTitle.setText("编辑支出");
                    break;
                case 2:
                    transferFragment = new TransFragment();
                    tf.add(R.id.frame, transferFragment).commit();
                    listener = (ModifyAndDeleteListener) transferFragment;
                    listener.setData(billBean);
                    tvTitle.setText("编辑转账");
                    break;
            }
        }
    }

    public void modify(View view) {
        if (listener != null) {
            listener.modify();
        }
    }

    public void delete(View view) {
        if (listener != null) {
            listener.delete();
        }
    }

    public interface ModifyAndDeleteListener {
        void modify();

        void delete();

        void setData(BillBean billBean);
    }
}
