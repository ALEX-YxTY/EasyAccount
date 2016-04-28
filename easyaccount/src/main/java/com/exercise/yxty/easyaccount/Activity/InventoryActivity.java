package com.exercise.yxty.easyaccount.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.ExpandedRecycle.MyChildObject;
import com.exercise.yxty.easyaccount.ExpandedRecycle.MyChildViewHolder;
import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.Utils.DateUtil;
import com.exercise.yxty.easyaccount.db.EasyAccountDAO;

import java.util.Calendar;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tvTitle;

    Calendar calendar;
    int[] timeArea;
    int mode;
    String type;

    List childs;

    EasyAccountDAO dao;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    recyclerView.setAdapter(new MyRecycleAdapter());
                    String month = DateUtil.formatString(calendar.get(Calendar.MONTH) + 1) + " 月  ";
                    if (type.equals("")) {
                        String desc = mode > 0 ? "支出" : "收入";
                        tvTitle.setText(month + desc);
                    } else {
                        tvTitle.setText(month + type);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = getIntent();
        Bundle bundle = startIntent.getExtras();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, bundle.getInt("YEAR"));
        calendar.set(Calendar.MONTH, bundle.getInt("MONTH"));
        mode = bundle.getInt("MODE");
        type = bundle.getString("TYPE", "");

        initUI();
        initData();
    }

    private void initUI() {
        setContentView(R.layout.activity_inventory);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initData() {
        dao = new EasyAccountDAO(this);
        timeArea = new int[2];
        DateUtil.getThisMonthNoReturn(calendar, timeArea);
        new Thread(){
            @Override
            public void run() {
                super.run();
                int typeID = dao.getTypeIDFromString(mode, type);
                childs = dao.getChildObjectsFromTimeArea(timeArea, mode, typeID);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class MyRecycleAdapter extends RecyclerView.Adapter<MyChildViewHolder> {

        @Override
        public MyChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(InventoryActivity.this).
                    inflate(R.layout.item_child_detailed_list, parent, false);
            return new MyChildViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyChildViewHolder holder, int position) {
            MyChildObject childObject = (MyChildObject) childs.get(position);
            int color = childObject.getInOrOut() > 0 ? R.color.expend : R.color.income;
            holder.setTvFeeTextColor(InventoryActivity.this.getResources().getColor(color));
            holder.setTvFee("¥ "+ childObject.getFee());
            holder.setTvSubtype(childObject.getSubType());
            holder.setTvDesc(childObject.getDesc());
            holder.setTvDayOfWeek(childObject.getDayOfWeek());
            holder.setTvDayOfMonth(childObject.getDayOfMonth());
        }

        @Override
        public int getItemCount() {
            return childs.size();
        }
    }
}
