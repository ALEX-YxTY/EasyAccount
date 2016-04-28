package com.exercise.yxty.easyaccount.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.Activity.ChartActivity;
import com.exercise.yxty.easyaccount.Activity.InventoryActivity;
import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.Utils.DateUtil;
import com.exercise.yxty.easyaccount.Utils.DecimalFormatUtil;
import com.exercise.yxty.easyaccount.db.EasyAccountDAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by Administrator on 2016/4/21.
 */
public class PieChartFragment extends Fragment implements View.OnClickListener,ChartActivity.OnArrowClickListener{

    PieChartView pieChartView;
    PieChartData pieChartData;
    TextView tvSubtype, tvPercentage, tvFee;

    Calendar calendar;

    EasyAccountDAO dao;

    int[] timeArea;
    double totalFee = 0.0;
    int mode = 1; //in_out 1-支出 0-收入

    final int NO_DATA = 1;
    final int DATA_OK = 2;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NO_DATA:
                    tvFee.setTextColor(Color.BLACK);
                    tvFee.setText("暂无数据");
                    tvSubtype.setText("");
                    tvPercentage.setText("");
                    pieChartView.setPieChartData(null);
                    break;
                case DATA_OK:
                    //设置中心显示内容
                    pieChartData.setCenterText2(DecimalFormatUtil.decimalFormat(totalFee));
                    pieChartView.setPieChartData(pieChartData);

                    //设置下方Text默认显示内容
                    SliceValue defaultValue = pieChartData.getValues().get(0);
                    int defaultColor = defaultValue.getColor();
                    float typeFee = defaultValue.getValue();
                    char[] label = defaultValue.getLabelAsChars();
                    double percentage = typeFee / totalFee * 100;
                    tvFee.setTextColor(defaultColor);
                    tvSubtype.setTextColor(defaultColor);
                    tvPercentage.setTextColor(defaultColor);
                    tvSubtype.setText(String.valueOf(label));
                    tvPercentage.setText(DecimalFormatUtil.decimalFormat(percentage) + " %");
                    tvFee.setText("¥ "+ DecimalFormatUtil.decimalFormat(typeFee));

                    break;
            }
        }
    };

    public PieChartFragment() {
        // Required empty public constructor
    }

    //initUI
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        tvSubtype = (TextView) view.findViewById(R.id.tv_subtype);
        tvPercentage = (TextView) view.findViewById(R.id.tv_percentage);
        tvFee = (TextView) view.findViewById(R.id.tv_fee);
        view.findViewById(R.id.ll_click).setOnClickListener(this);
        pieChartView = (PieChartView) view.findViewById(R.id.pie_chart);
        pieChartView.setOnValueTouchListener(new MyOnTouchListener());
        pieChartView.setChartRotationEnabled(false);
//        Log.i("test", "pieChat启动了");
        return view;
    }

    //initData

    /**
     *  工作：
     *  1.获取当月时间段
     *  2.根据当月时间段获取各分类金额
     *  3.建立pieChartData
     *  4.完成listener初始化
     *
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dao = new EasyAccountDAO(getContext());
        calendar = Calendar.getInstance();
        timeArea = new int[2];
        initPieChartData();
        generateData();

    }

    private void initPieChartData() {
        pieChartData = new PieChartData();
        pieChartData.setHasLabels(true);
//        pieChartData.setHasLabelsOnlyForSelected(true);
//        pieChartData.setHasLabelsOutside(true);
        pieChartData.setHasCenterCircle(true);
        pieChartData.setCenterCircleScale(0.5f);
        pieChartData.setSlicesSpacing(5);
        //设置中心第一行文字
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Bold.ttf");
        //设字体
        pieChartData.setCenterText1Typeface(tf);
        //设字大小
        pieChartData.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        pieChartData.setCenterText1Color(Color.BLACK);
        pieChartData.setCenterText1("支出总计");
        //设置中心第二行文字
        pieChartData.setCenterText2Typeface(tf);
        pieChartData.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
        pieChartData.setCenterText2Color(Color.BLACK);
        pieChartData.setValueLabelTextSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.pie_chart_label_size)));
    }

    private void generateData() {
        //获取到时间段
        DateUtil.getThisMonth(calendar, timeArea);
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<SliceValue> sliceValues = new ArrayList<>();
                totalFee = dao.getExpendSliceValueThisMonth(sliceValues, timeArea);
                if (sliceValues.size() > 0) {
                    pieChartData.setValues(sliceValues);
                    handler.sendEmptyMessage(DATA_OK);
                } else {
                    handler.sendEmptyMessage(NO_DATA);
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_click:
                //进入流水页面
                Bundle bundle = new Bundle();
                bundle.putInt("YEAR", calendar.get(Calendar.YEAR));
                bundle.putInt("MONTH", calendar.get(Calendar.MONTH));
                bundle.putInt("MODE", mode);
                bundle.putString("TYPE", String.valueOf(tvSubtype.getText()));
                Intent intent = new Intent(getContext(), InventoryActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                break;
            default:
                break;
        }
    }

    @Override
    public void onArrowLeftClick() {
        calendar.add(Calendar.MONTH, -1);
        generateData();
    }

    @Override
    public void onArrowRightClick() {
        calendar.add(Calendar.MONTH, 1);
        generateData();
    }


    private class MyOnTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int i, SliceValue sliceValue) {
            int valueColor = sliceValue.getColor();
            float typeFee = sliceValue.getValue();
            char[] label = sliceValue.getLabelAsChars();
            double percentage = typeFee / totalFee * 100;
            tvFee.setTextColor(valueColor);
            tvSubtype.setTextColor(valueColor);
            tvPercentage.setTextColor(valueColor);
            tvSubtype.setText(String.valueOf(label));
            tvPercentage.setText(DecimalFormatUtil.decimalFormat(percentage) + " %");
            tvFee.setText("¥ "+ DecimalFormatUtil.decimalFormat(typeFee));

        }

        @Override
        public void onValueDeselected() {

        }
    }

}
