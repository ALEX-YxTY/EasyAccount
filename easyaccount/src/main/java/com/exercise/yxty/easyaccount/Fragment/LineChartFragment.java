package com.exercise.yxty.easyaccount.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.Activity.ChartActivity;
import com.exercise.yxty.easyaccount.Activity.InventoryActivity;
import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.Utils.DateUtil;
import com.exercise.yxty.easyaccount.Utils.DecimalFormatUtil;
import com.exercise.yxty.easyaccount.beans.DateBean;
import com.exercise.yxty.easyaccount.db.EasyAccountDAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Administrator on 2016/4/21.
 */
public class LineChartFragment extends Fragment implements View.OnClickListener,ChartActivity.OnArrowClickListener{

    LineChartView lineChartView;
    LineChartData lineChartData;
    TextView tvMonth, tvInOut,tvFee;
    ImageView ivExpend, ivIncome;

    Calendar calendar;
    int[] timeArea;

    int year;
    int selectMonth = 11;
    int mode = 1; //in_out 1-支出 0-收入


    EasyAccountDAO dao;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    lineChartView.setLineChartData(lineChartData);
                    //设置初始底部显示
                    //默认显示12月支出
                    PointValue defaultValue = lineChartData.getLines().get(1).getValues().get(0);
                    tvMonth.setText(DateUtil.formatString((int) defaultValue.getX())+"月");
                    tvInOut.setText("支出");
                    tvFee.setText("¥ " + DecimalFormatUtil.decimalFormat(defaultValue.getY()));
                    int color = lineChartData.getLines().get(1).getColor();
                    tvFee.setTextColor(color);
                    tvInOut.setTextColor(color);
                    tvMonth.setTextColor(color);

                    //设置图例色块
                    //income_line
                    ivIncome.setBackgroundColor(lineChartData.getLines().get(0).getColor());
                    ivExpend.setBackgroundColor(lineChartData.getLines().get(1).getColor());
                    break;


            }
        }
    };

    public LineChartFragment() {
        // Required empty public constructor
    }

    //initUI
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
        tvMonth = (TextView) view.findViewById(R.id.tv_month);
        tvInOut = (TextView) view.findViewById(R.id.tv_in_out);
        tvFee = (TextView) view.findViewById(R.id.tv_fee);
        ivExpend = (ImageView) view.findViewById(R.id.iv_expend);
        ivIncome = (ImageView) view.findViewById(R.id.iv_income);
        view.findViewById(R.id.ll_click).setOnClickListener(this);
        lineChartView = (LineChartView)view.findViewById(R.id.line_chart);
        lineChartView.setOnValueTouchListener(new MyOnLineClickListener());
//        Log.i("test", "lineChat启动了");

        return view;
    }

    //initData
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dao = new EasyAccountDAO(getContext());
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        timeArea = new int[2];
        initLieChartData();
        generateData();

    }

    private void initLieChartData() {
        lineChartData = new LineChartData();
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("月份");
        axisY.setName("金额");
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Bold.ttf");
        axisX.setTypeface(tf);
        axisY.setTypeface(tf);
        axisX.setTextSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.line_chart_axis_size)));
        axisY.setTextSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.line_chart_axis_size)));
        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);
//        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChartData.setBaseValue(0f);

    }


    private void generateData() {
        final List<DateBean> datePerMonth = getDateBeanPerMonth();
        new Thread(){
            @Override
            public void run() {
                super.run();
                //得到支出line
                Line expendLine = dao.getLinePointValue(datePerMonth, 1);
                //得到收入line
                Line incomeLine = dao.getLinePointValue(datePerMonth, 0);
                initLine(expendLine);
                initLine(incomeLine);
                List<Line> lines = new ArrayList<>();
                lines.add(incomeLine);
                lines.add(expendLine);
                lineChartData.setLines(lines);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initLine(Line line) {
        line.setColor(ChartUtils.nextColor());
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);
        line.setFilled(true);
        line.setHasLabels(true);
//        line.setHasLabelsOnlyForSelected(true);
        line.setHasLines(true);
        line.setHasPoints(true);

    }

    private List<DateBean> getDateBeanPerMonth() {
        List<DateBean> dateBeans = new ArrayList<>();
        calendar.set(Calendar.MONTH, 11);
        while (calendar.get(Calendar.YEAR) == year) {
            DateBean bean = new DateBean();
            DateUtil.getThisMonthNoReturn(calendar, timeArea);
            int month = calendar.get(Calendar.MONTH) + 1;
            bean.setMonth(month);
            bean.setTimeArea(timeArea);
            dateBeans.add(bean);
            //向下一个月循环
            calendar.add(Calendar.MONTH, -1);

        }
        //经过上面的循环，calendar已经是上一个年度了，所以此处加一个月，回到year值显示年度
        calendar.add(Calendar.MONTH, 1);

        return dateBeans;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_click:
                //进入流水页面
                Bundle bundle = new Bundle();
                bundle.putInt("YEAR", year);
                bundle.putInt("MONTH", selectMonth);
                bundle.putInt("MODE", mode);
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
        calendar.add(Calendar.YEAR, -1);
        year--;
        generateData();
    }

    @Override
    public void onArrowRightClick() {
        calendar.add(Calendar.YEAR, 1);
        year++;
        generateData();
    }

    private class MyOnLineClickListener implements LineChartOnValueSelectListener {

        /**
         *
         * @param i Line_index
         * @param i1 Point_index
         * @param pointValue
         */
        @Override
        public void onValueSelected(int i, int i1, PointValue pointValue) {
//            Log.i("test", "i=" + i + ", i1=" + i1);
            tvMonth.setText(DateUtil.formatString(12-i1)+"月");
            //设定选中的month值，供bundle使用
            selectMonth = 11 - i1;
            tvInOut.setText(i>0?"支出":"收入");
            mode = i;
            tvFee.setText("¥ " + DecimalFormatUtil.decimalFormat(pointValue.getY()));
            int color = lineChartData.getLines().get(i).getColor();
            tvFee.setTextColor(color);
            tvInOut.setTextColor(color);
            tvMonth.setTextColor(color);
        }

        @Override
        public void onValueDeselected() {

        }
    }
}
