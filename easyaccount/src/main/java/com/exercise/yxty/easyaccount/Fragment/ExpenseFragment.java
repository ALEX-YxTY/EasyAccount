package com.exercise.yxty.easyaccount.Fragment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.Utils.DecimalFormatUtil;
import com.exercise.yxty.easyaccount.view.WheelView;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/4/5.
 */
public class ExpenseFragment extends BaseFragment implements View.OnClickListener{

    TextView tvAccount,tvPro,tvStore;
    Button btDate,btStore,btProject;
    ImageView storeCancel,proCancel;
    RelativeLayout rlPro,rlStore;
    LinearLayout llAccount;

    final int in_out = 1;  //1——支出  0——收入

    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    int getResourceId() {
        return R.layout.expense_fragment;
    }

    //对各个Fragment特有控件初始化
    @Override
    void initSpecialUI(View view) {
        view.findViewById(R.id.ll_fee).setOnClickListener(this);
        view.findViewById(R.id.ll_type).setOnClickListener(this);
//        llFee.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
//        llDesc.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        //先使父view获取焦点
        llDesc.requestFocus();
        llDesc.setOnClickListener(this);
//        view.findViewById(R.id.ll_desc).setOnClickListener(this);
//        edDesc.setOnClickListener(this);


        llAccount = (LinearLayout) view.findViewById(R.id.ll_account);
        llAccount.setOnClickListener(this);
        rlPro = (RelativeLayout) view.findViewById(R.id.rl_pro);
        rlStore = (RelativeLayout) view.findViewById(R.id.rl_store);
        rlPro.setOnClickListener(this);
        rlStore.setOnClickListener(this);
        tvAccount = (TextView) view.findViewById(R.id.tv_account);
        tvStore = (TextView) view.findViewById(R.id.tv_store);
        tvPro = (TextView) view.findViewById(R.id.tv_pro);

        btStore = (Button) view.findViewById(R.id.bt_store);
        btProject = (Button) view.findViewById(R.id.bt_project);
        btProject.setOnClickListener(this);
        btStore.setOnClickListener(this);
        storeCancel = (ImageView) view.findViewById(R.id.store_cancel);
        proCancel = (ImageView) view.findViewById(R.id.pro_cancel);
        storeCancel.setOnClickListener(this);
        proCancel.setOnClickListener(this);

        btDate = (Button) view.findViewById(R.id.bt_date);

        calendar = Calendar.getInstance();
        cloneCalendar = (Calendar) calendar.clone();
        btDate.setText(showDate(calendar));
        btDate.setOnClickListener(this);

        TextView tvFocus = (TextView) view.findViewById(R.id.tv_focus);
        tvFocus.requestFocus();
    }



    @Override
    public void onClick(View v) {
        windowHeight = getWindowHeight();       //获取屏幕大小，后续各pop要用

        switch (v.getId()) {
            case R.id.ll_fee:
//                edDesc.setCursorVisible(false);
                showDecimalPop();
                break;
            case R.id.ll_type:
//                edDesc.setCursorVisible(false);
                //导入
                dao.queryMain("TABLE_EXPENDITURE_TYPE", typeId, type);
                dao.querySub("TABLE_EXPENDITURE_SUBTYPE", subId, subtype, selectType);
                //1.读取数据库，找到type下数据
                //2.准备popupwindow
                //3.数据写入wheelView
                this.showTwoSelectPopup(selectType, selectSubtype, new WheelView.OnSelectListener() {
                    @Override
                    public void endSelect(int id, String text) {
                        selectType = typeId.get(id);
                        Log.i("test", "id = " + selectType);
                        dao.querySub("TABLE_EXPENDITURE_SUBTYPE", subId, subtype, selectType);        //重新获取subtype和id
                        for (int i = 0; i < subId.size(); i++) {
                            Log.i("test", "subtype:" + subtype.get(i) + ", subId:" + subId.get(i));
                        }
                        Log.i("test", "selectSubtype:" + selectSubtype);
                        //重设默认的selectSubType数值，防止subType未做选择直接使用default值
                        selectSubtype = subId.get(0);
                        tvType.setText(text);                                   //设置type和subtype显示
                        tvSubtype.setText(subtype.get(0));                      //ID号比位置大1

                        wlvSub.resetData(subtype);                              //重设wheel_sub数据
                        wlvSub.setDefault(0);                                   //设置默认显示项


                        Log.i("test", "selectSubtype:" + selectSubtype);

//                      Log.i("test", subtype.toString());
                    }

                    @Override
                    public void selecting(int id, String text) {

                    }
                },
                        new WheelView.OnSelectListener() {
                    @Override
                    public void endSelect(int id, String text) {
                        selectSubtype = subId.get(id);
                        tvSubtype.setText(text);
                      Log.i("test", "typeid + subtypeId =" + selectType + "," + selectSubtype);
                    }

                    @Override
                    public void selecting(int id, String text) {
                    }
                });
                break;
            case R.id.ll_account:
//                edDesc.setCursorVisible(false);
                dao.queryMain("TABLE_ACCOUNT", typeId, type);
                this.showSingleSelectPop(selectAccount, new WheelView.OnSelectListener() {
                    @Override
                    public void endSelect(int id, String text) {
                        selectAccount = typeId.get(id);
                        tvAccount.setText(text);
                    }

                    @Override
                    public void selecting(int id, String text) {

                    }
                });
                break;
            case R.id.ll_desc:
                /*
                    第一次点击以上控件后再点击editText总不能显示光标，但再点击其他控件后又恢复显示，原因不明…………
                 */
                edDesc.requestFocus();
//                edDesc.setCursorVisible(true);
                InputMethodManager imm2 = (InputMethodManager) edDesc.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm2.showSoftInput(edDesc, InputMethodManager.SHOW_FORCED);
                break;
            case R.id.rl_pro:
                dao.queryMain("TABLE_PROJECT", typeId, type);
                this.showSingleSelectPop(selectPro, new WheelView.OnSelectListener() {
                    @Override
                    public void endSelect(int id, String text) {
                        selectPro = typeId.get(id);
                        tvPro.setText(text);
                    }

                    @Override
                    public void selecting(int id, String text) {

                    }
                });
                break;
            case R.id.rl_store:
                dao.queryMain("TABLE_STORE", typeId, type);
                this.showSingleSelectPop(selectStore, new WheelView.OnSelectListener() {
                    @Override
                    public void endSelect(int id, String text) {

                    }

                    @Override
                    public void selecting(int id, String text) {
                        selectStore = typeId.get(id);
                        tvStore.setText(text);
                    }
                });
                break;
            case R.id.bt_store:
                rlStore.setVisibility(View.VISIBLE);
                btStore.setVisibility(View.GONE);
                break;
            case R.id.bt_project:
                rlPro.setVisibility(View.VISIBLE);
                btProject.setVisibility(View.GONE);
                break;
            case R.id.store_cancel:
                rlStore.setVisibility(View.GONE);
                btStore.setVisibility(View.VISIBLE);
                break;
            case R.id.pro_cancel:
                rlPro.setVisibility(View.GONE);
                btProject.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_date:
                this.showDatePicker(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        btDate.setText(showDate(calendar));
                    }
                });

                break;
            default:
                break;
        }
    }


    protected boolean saveBill() {
        if (fee <= 0) {
            ShowErrorToast();
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put("IN_OUT",in_out);
            values.put("FEE", DecimalFormatUtil.decimalFormat(fee));
            values.put("DATE", calendar.getTimeInMillis() / 1000);
            values.put("TYPE", selectType);
            values.put("SUBTYPE", selectSubtype);
            values.put("DESC", edDesc.getText().toString());
            values.put("ACCOUNT_ID", selectAccount);
            if (selectStore > 0) {
                values.put("STORE", selectStore);
            }
            if (selectPro > 0) {
                values.put("PROJECT", selectPro);
            }
            return (dao.addBills("TABLE_BILL", values));
        }
    }

    //重置状态和显示
    protected void initUIAndDATA() {
        tvFee.setText("0.00");
        fee = 0.00;
        tvType.setText("食");
        selectType = 1;
        tvSubtype.setText("早午晚餐");
        selectSubtype = 1;
        tvAccount.setText("现金账户");
        selectAccount = 1;
        edDesc.setText("");
        tvPro.setText("无项目");
        selectPro = -1;
        tvStore.setText("无商家");
        selectStore = -1;
        //再记一笔时不对日期做修改，但要对时间做刷新
        Calendar now = Calendar.getInstance();
        calendar.set(Calendar.HOUR, now.get(Calendar.HOUR));
        calendar.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, now.get(Calendar.SECOND));
        cloneCalendar = (Calendar) calendar.clone();
    }

}
