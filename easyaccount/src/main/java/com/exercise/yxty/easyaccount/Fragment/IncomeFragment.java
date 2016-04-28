package com.exercise.yxty.easyaccount.Fragment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.view.WheelView;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/4/5.
 */
public class IncomeFragment extends BaseFragment implements View.OnClickListener{

    TextView tvAccount,tvPro;
    Button btDate,btProject;
    ImageView proCancel;
    RelativeLayout rlPro;
    LinearLayout llAccount;

    final int in_out = 0;  //1——支出  0——收入

    public IncomeFragment() {
        // Required empty public constructor
    }

    @Override
    int getResourceId() {
        return R.layout.incom_fragment;
    }

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
        rlPro.setOnClickListener(this);
        tvAccount = (TextView) view.findViewById(R.id.tv_account);
        tvPro = (TextView) view.findViewById(R.id.tv_pro);

        btProject = (Button) view.findViewById(R.id.bt_project);
        btProject.setOnClickListener(this);
        proCancel = (ImageView) view.findViewById(R.id.pro_cancel);
        proCancel.setOnClickListener(this);

        btDate = (Button) view.findViewById(R.id.bt_date);

        calendar = Calendar.getInstance();
        cloneCalendar = (Calendar) calendar.clone();
        btDate.setText(showDate(calendar));
        btDate.setOnClickListener(this);
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
                dao.queryMain("TABLE_INCOME_TYPE", typeId, type);
                dao.querySub("TABLE_INCOME_SUBTYPE", subId, subtype, selectType);
                //1.读取数据库，找到type下数据
                //2.准备popupwindow
                //3.数据写入wheelView
                this.showTwoSelectPopup(selectType, selectSubtype, new WheelView.OnSelectListener() {
                            @Override
                            public void endSelect(int id, String text) {
                            }

                            @Override
                            public void selecting(int id, String text) {
//                Log.i("test", "id = " + id);
                                selectType = typeId.get(id);
                                dao.querySub("TABLE_INCOME_SUBTYPE", subId, subtype, selectType);        //重新获取subtype和id
                                tvType.setText(text);                                   //设置type和subtype显示
                                tvSubtype.setText(subtype.get(selectSubtype - 1));        //ID号比位置大1

                                wlvSub.resetData(subtype);                              //重设wheel_sub数据
                                wlvSub.setDefault(0);
//                Log.i("test", subtype.toString());

                            }
                        },
                        new WheelView.OnSelectListener() {
                            @Override
                            public void endSelect(int id, String text) {
                            }

                            @Override
                            public void selecting(int id, String text) {
                                selectSubtype = subId.get(id);
                                tvSubtype.setText(text);
//                Log.i("test", "typeid + subtypeId =" + selectType + "," + selectSubtype);
                            }
                        });
                break;
            case R.id.ll_account:
//                edDesc.setCursorVisible(false);
                dao.queryMain("TABLE_ACCOUNT", typeId, type);
                this.showSingleSelectPop(selectAccount, new WheelView.OnSelectListener() {
                    @Override
                    public void endSelect(int id, String text) {

                    }

                    @Override
                    public void selecting(int id, String text) {
                        selectAccount = typeId.get(id);
                        tvAccount.setText(text);
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

                    }

                    @Override
                    public void selecting(int id, String text) {
                        selectPro = typeId.get(id);
                        tvPro.setText(text);
                    }
                });
                break;
            case R.id.bt_project:
                rlPro.setVisibility(View.VISIBLE);
                btProject.setVisibility(View.GONE);
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
            values.put("FEE", fee);
            values.put("DATE", calendar.getTimeInMillis() / 1000);
            values.put("TYPE", selectType);
            values.put("SUBTYPE", selectSubtype);
            values.put("DESC", edDesc.getText().toString());
            values.put("ACCOUNT_ID", selectAccount);
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
        tvType.setText("职业收入");
        selectType = 1;
        tvSubtype.setText("工资收入");
        selectSubtype = 1;
        tvAccount.setText("现金账户");
        selectAccount = 1;
        edDesc.setText("");
        tvPro.setText("无项目");
        selectPro = -1;
        //再记一笔时不对日期做修改，但要对时间做刷新
        Calendar now = Calendar.getInstance();
        calendar.set(Calendar.HOUR, now.get(Calendar.HOUR));
        calendar.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, now.get(Calendar.SECOND));
        cloneCalendar = (Calendar) calendar.clone();
    }
}
