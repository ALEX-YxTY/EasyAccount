package com.exercise.yxty.easyaccount.Fragment;

import android.app.DatePickerDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.exercise.yxty.easyaccount.Activity.AddTransactionActivity;
import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.db.EasyAccountDAO;
import com.exercise.yxty.easyaccount.view.WheelView;
import com.exercise.yxty.easyaccount.view.digit_keypad;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/4/6.
 * <p/>
 * 基础Fragment 实现公用控件初始化，公用数据定义
 * 公用方法（弹出wheelview选择框）实现
 * 以及与Activity的接口方法（save和onMore）
 */
public abstract class BaseFragment extends Fragment implements AddTransactionActivity.SaveAndOneMoreInterface {

    TextView tvType, tvSubtype, tvFee;
    EditText edDesc;
    LinearLayout llDesc;
    ImageView ivOk;

    PopupWindow mPop;
    View popView;
    WheelView wlvMain, wlvSub;
    digit_keypad keypad;

    DatePickerDialog datePickerDialog;

    //可重用
    ArrayList<String> type, subtype;
    ArrayList<Integer> typeId, subId;

    //屏幕高
    int windowHeight;

    //选中的支出类别、子类别
    int selectType, selectSubtype;
    //最终金额
    double fee;
    //选中的账户类别，商家类别，项目类别
    int selectAccount, selectStore, selectPro;
    //最终的日期
    Calendar calendar, cloneCalendar;

    EasyAccountDAO dao;
    //数字格式处理工具
    DecimalFormat df;
    //日期格式处理工具
    SimpleDateFormat sdf;
    int resourceId;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                datePickerDialog.getDatePicker().setEnabled(true);
            }
        }
    };

    abstract int getResourceId();

    abstract void initSpecialUI(View view);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            resourceId = getResourceId();
            View view = inflater.inflate(resourceId, container, false);
            initUI(view);
            initSpecialUI(view);
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void initUI(View view) {
        tvFee = (TextView) view.findViewById(R.id.tv_fee);
        tvType = (TextView) view.findViewById(R.id.tv_type);
        tvSubtype = (TextView) view.findViewById(R.id.tv_subtype);
        edDesc = (EditText) view.findViewById(R.id.et_desc);
        //根据获焦的状况决定光标的显示
        edDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edDesc.setCursorVisible(true);
                } else {
                    edDesc.setCursorVisible(false);
                }
            }
        });
        llDesc = (LinearLayout) view.findViewById(R.id.ll_desc);
    }

    //initData
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initData
        dao = new EasyAccountDAO(getContext());
        selectType = 1;     //默认食
        selectSubtype = 1;  //默认早午晚餐
        selectAccount = 1;  //默认现金
        selectPro = -1;     //默认不选为空
        selectStore = -1;   //默认不选为空

        type = new ArrayList<>();
        subtype = new ArrayList<>();
        typeId = new ArrayList<>();
        subId = new ArrayList<>();

        df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        //SimpleDateFormat线程不安全，但是单线程中使用时可以只建立一个对象，方便复用
        //如果要在多线程中使用，可以加上synchronize修饰
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    public void save() {
        if (saveBill()) {
            Toast.makeText(getContext(),"添加成功",Toast.LENGTH_SHORT).show();
            getActivity().setResult(1);
            getActivity().finish();
        }
    }

    @Override
    public void oneMore() {
        if (saveBill()) {
            Toast.makeText(getContext(),"添加成功",Toast.LENGTH_SHORT).show();
            initUIAndDATA();
            edDesc.clearFocus();
            InputMethodManager imm2 = (InputMethodManager) edDesc.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm2.showSoftInput(edDesc, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    protected abstract void initUIAndDATA();

    protected abstract boolean saveBill();


    //显示两个wheelView联动的popup
    public void showTwoSelectPopup(int selectItemMain, int selectItemSub,
                                   WheelView.OnSelectListener listenerMain, WheelView.OnSelectListener listenerSub) {
        popView = View.inflate(getContext(), R.layout.popup_two_select, null);
//        Log.i("test", type.toString());
//        Log.i("test", typeId.toString());
//        Log.i("test", subId.toString());

        mPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                windowHeight / 2);
        mPop.setBackgroundDrawable(new BitmapDrawable());
        mPop.setFocusable(true);
        mPop.setTouchable(true);
        mPop.setOutsideTouchable(true);

        //设置图标点击
        ivOk = (ImageView) popView.findViewById(R.id.iv_ok);
        ivOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDismiss();
            }
        });
        //设置wheelView
        wlvMain = (WheelView) popView.findViewById(R.id.wlv_main);
        wlvSub = (WheelView) popView.findViewById(R.id.wlv_sub);
        wlvMain.setData(type);
        wlvMain.setDefault(selectItemMain - 1);
        wlvSub.setData(subtype);
        wlvSub.setDefault(selectItemSub - 1);
        wlvMain.setOnSelectListener(listenerMain);
        wlvSub.setOnSelectListener(listenerSub);

        mPop.showAtLocation(getActivity().findViewById(R.id.ll_main), Gravity.NO_GRAVITY, 0, windowHeight / 2);
        //此处动画的位置是以showAtLocation的位置为基点的
        Animation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animTranslate.setDuration(300);
        animTranslate.setFillAfter(true);
        popView.startAnimation(animTranslate);
    }

    //显示单个wheelView的popup
    public void showSingleSelectPop(int selectItem, WheelView.OnSelectListener listener) {
        //生成pop对象
        popView = View.inflate(getContext(), R.layout.popup_single_select, null);
        mPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                windowHeight / 2);
        mPop.setBackgroundDrawable(new BitmapDrawable());
        mPop.setFocusable(true);
        mPop.setTouchable(true);
        mPop.setOutsideTouchable(true);
        //设置图标点击
        ivOk = (ImageView) popView.findViewById(R.id.iv_ok);
        ivOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDismiss();
            }
        });
        //设置wheelView
        //有数据时执行
        if (type.size() > 0) {
            wlvMain = (WheelView) popView.findViewById(R.id.wlv_main);
            wlvMain.setData(type);
            //判断默认选项为-1时的操作
            wlvMain.setDefault(selectItem > 0 ? selectItem - 1 : 0);
            wlvMain.setOnSelectListener(listener);
        }
        //设置动画
        mPop.showAtLocation(getActivity().findViewById(R.id.ll_main), Gravity.NO_GRAVITY, 0, windowHeight / 2);
        //此处动画的位置是以showAtLocation的位置为基点的
        Animation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animTranslate.setDuration(300);
        animTranslate.setFillAfter(true);
        popView.startAnimation(animTranslate);
    }

    public int getWindowHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    //显示DatePicker的popup
    public void showDatePicker(DatePickerDialog.OnDateSetListener listener) {
        datePickerDialog = new DatePickerDialog(getContext(), listener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //设置焦点向下传递，使得不弹出键盘！
//        datePickerDialog.getDatePicker().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        datePickerDialog.getDatePicker().setEnabled(false);
        datePickerDialog.show();
        handler.sendEmptyMessage(0);
    }

    //显示数字键盘的popup
    public void showDecimalPop() {
        //生成pop对象
        popView = View.inflate(getContext(), R.layout.popup_fee_select, null);
        //屏幕适配问题，meizu手机getWindowHeight返回值包含底部虚拟按键高度，但其他手机不包括，暂时还没有解决办法……
        mPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                windowHeight / 2 - 150);
        mPop.setBackgroundDrawable(new BitmapDrawable());
        mPop.setFocusable(true);
        mPop.setTouchable(true);
        mPop.setOutsideTouchable(true);
        //设置图标点击
        ivOk = (ImageView) popView.findViewById(R.id.iv_ok);
        ivOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDismiss();
            }
        });
        //设置kayPad
        keypad = (digit_keypad) popView.findViewById(R.id.keypad);
        //每次弹出键盘是重设金额
        fee = 0.00;
        tvFee.setText(df.format(fee));
        keypad.setOnKeyPadClickListener(new MyOnKeyPadClickListener());

        //设置动画
        mPop.showAtLocation(getActivity().findViewById(R.id.ll_main), Gravity.NO_GRAVITY, 0, getWindowHeight() / 2);
        //此处动画的位置是以showAtLocation的位置为基点的
        Animation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animTranslate.setDuration(300);
        animTranslate.setFillAfter(true);
        popView.startAnimation(animTranslate);
    }

    public String showDate(Calendar calendarNew) {
        String date = (calendarNew.get(Calendar.MONTH) + 1) + "月" + calendarNew.get(Calendar.DAY_OF_MONTH) + "日";
        if (calendarNew.get(Calendar.YEAR) != cloneCalendar.get(Calendar.YEAR)) {
            return calendarNew.get(Calendar.YEAR) + "年" + date;
        } else {
            String plus = "";
            int dayGap = (int) (calendarNew.getTimeInMillis() - cloneCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000);
            if (dayGap == -2) {
                plus = "前天";
            }
            if (dayGap == -1) {
                plus = "昨天";
            }
            if (dayGap == 0) {
                plus = "今天";
            }
            if (dayGap == 1) {
                plus = "明天";
            }
            if (dayGap == 2) {
                plus = "后天";
            }
            return plus + date;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        popDismiss();
    }


    private void popDismiss() {
        if (mPop != null && mPop.isShowing()) {
            //设置动画
            //此处动画的位置是以showAtLocation的位置为基点的
            Animation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
            animTranslate.setDuration(200);
            animTranslate.setFillAfter(true);
            popView.startAnimation(animTranslate);
            mPop.dismiss();
            mPop = null;
        }
    }

    public class MyOnKeyPadClickListener implements digit_keypad.OnKeyPadClickListener {

        //标识整数部分和小数部分
        boolean isFractional = false;
        //标识小数部分位数，最大两位
        int place = 0;
        //用来储存计算中已经输入过的数字
        ArrayList<Double> feeNumber = new ArrayList<>();
        //用来标识之前输入数字的加或减操作
        int sign = 1;

        @Override
        public void onNumberClicked(int number) {
            if (!isFractional && place == 0) {
                fee = fee * 10 + number;
                tvFee.setText(df.format(fee));
//                Log.i("testl", fee+"");
            }
            if (place >= 2) {
                AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
                aa.setDuration(100);
                aa.setRepeatCount(2);
                tvFee.startAnimation(aa);
            }

            if (isFractional && place < 2) {
                fee += Math.pow(0.1, place + 1) * number;
                place++;
                tvFee.setText(df.format(fee));
            }
//            Log.i("test", "fee:" + fee + ",isFractional:" + isFractional + ",place:" + place);
//            Log.i("test", feeNumber.toString());
        }

        @Override
        public void onOperatorClicked(String operator) {
            if (operator.equals(".")) {
                isFractional = true;
            }
            if (operator.equals("+")) {
                feeNumber.add(sign * fee);
                sign = 1;
                //状态还原
                fee = 0;
                initData();
            }
            if (operator.equals("-")) {
                feeNumber.add(sign * fee);
                sign = -1;
                fee = 0;
                initData();
            }
            if (operator.equals("=")) {
                feeNumber.add(sign * fee);
                fee = total(feeNumber);
                tvFee.setText(df.format(fee));
                feeNumber.clear();
                //通过取余的方法得出isFractional和place值,以及恢复sign
                if (fee % 1 == 0) {
                    isFractional = false;
                    place = 0;
                } else {
                    isFractional = true;
                    if (fee * 10 % 1 == 0) {
                        place = 1;
                    } else {
                        place = 2;
                    }
                }
                sign = 1;
            }
            if (operator.equals("back")) {
//                Log.i("test", df.format(15.23 % 0.1) + "," + df.format(15.23 % 1) + "," + df.format(15.23 % 0.01));
                if (!isFractional) {
                    fee = (int)fee / 10;
                    tvFee.setText(df.format(fee));
                } else {
                    if (place > 0) {
                        place--;
                        fee -= fee % Math.pow(0.1, place);
                        tvFee.setText(df.format(fee));
                        if (place == 0) {
                            isFractional = false;
                        }
                    }
                }
            }
//            Log.i("test", "fee:" + fee + ",isFractional:" + isFractional + ",place:" + place);
//            Log.i("test", feeNumber.toString());
        }

        //初始化状态
        private void initData() {
            isFractional = false;
            place = 0;
        }

        //计算之前的式子
        protected double total(ArrayList<Double> feeNumber) {
            double result = 0.00;
            for (int i = 0; i < feeNumber.size(); i++) {
                result += feeNumber.get(i);
            }
            return result;
        }


    }

    public void ShowErrorToast() {
        Toast.makeText(getContext(),"输入数据不合法",Toast.LENGTH_SHORT).show();
    }

}
