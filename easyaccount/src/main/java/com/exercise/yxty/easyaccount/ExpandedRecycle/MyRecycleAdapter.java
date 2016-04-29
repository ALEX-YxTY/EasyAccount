package com.exercise.yxty.easyaccount.ExpandedRecycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.exercise.yxty.easyaccount.Activity.EditActivity;
import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.Utils.DecimalFormatUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/4/17.
 */
public class MyRecycleAdapter extends ExpandableRecyclerAdapter<MyParentViewHolder,MyChildViewHolder> {

    private LayoutInflater mInflater;
    private Context context;

    public MyRecycleAdapter(Context context,List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public MyParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        //attachToRoot == false, 则不把此view作为parentView,而是仅仅继承他的layoutParams
        View view = mInflater.inflate(R.layout.item_parent_detailed_list, viewGroup, false);

        return new MyParentViewHolder(view);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.item_child_detailed_list, viewGroup, false);
        return new MyChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(MyParentViewHolder myParentViewHolder, int i, ParentListItem o) {
        MyParentObject parentObject = (MyParentObject) o;
        myParentViewHolder.setTvMonth(parentObject.getMonth());
        myParentViewHolder.setTvIncome(parentObject.getIncome());
        myParentViewHolder.setTvExpend(parentObject.getExpend());
        myParentViewHolder.setTvBalance(parentObject.getBalance());
        myParentViewHolder.setTvDate(parentObject.getDate());
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder myChildViewHolder, int i, Object o) {
        MyChildObject childObject = (MyChildObject) o;
        int color = R.color.transfer;
        if (childObject.getInOrOut() < 2) {
            color = childObject.getInOrOut() > 0 ? R.color.expend : R.color.income;
        }
        final String date = childObject.getDate();
        final double fee = childObject.getFee();
        myChildViewHolder.setTvFeeTextColor(context.getResources().getColor(color));
        myChildViewHolder.setTvFee("¥ " + DecimalFormatUtil.decimalFormat(fee));
        myChildViewHolder.setTvDayOfMonth(childObject.getDayOfMonth());
        myChildViewHolder.setTvDayOfWeek(childObject.getDayOfWeek());
        myChildViewHolder.setTvDesc(childObject.getDesc());
        myChildViewHolder.setTvSubtype(childObject.getSubType());

        myChildViewHolder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("DATE", date);
                Activity activity = (Activity) context;
                activity.startActivityForResult(intent, 1);
            }
        });
    }

}
