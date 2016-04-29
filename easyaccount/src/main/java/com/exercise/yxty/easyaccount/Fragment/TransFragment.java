package com.exercise.yxty.easyaccount.Fragment;

import android.content.ContentValues;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.view.WheelView;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/4/5.
 */
public class TransFragment  extends BaseFragment implements View.OnClickListener {

    final int in_out = 2;  //1——支出  0——收入 2-转账

    public TransFragment() {
        // Required empty public constructor
    }

    @Override
    int getResourceId() {
        return R.layout.trans_fragment;
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
        //Transfer页面初始两个type不一致，所以subType+1
        if (!isEdit) {
            selectSubtype = 2;
        }
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
                dao.queryMain("TABLE_ACCOUNT_TYPE", typeId, type);
                dao.queryMain("TABLE_ACCOUNT_TYPE", subId, subtype);
                //1.读取数据库，找到type下数据
                //2.准备popupwindow
                //3.数据写入wheelView
                this.showTwoSelectPopup(selectType, selectSubtype, new WheelView.OnSelectListener() {
                            @Override
                            public void endSelect(int id, String text) {
                            }

                            @Override
                            public void selecting(int id, String text) {
                                selectType = typeId.get(id);
                                tvType.setText(text);                                   //设置type和subtype显示
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
            case R.id.ll_desc:
                /*
                    第一次点击以上控件后再点击editText总不能显示光标，但再点击其他控件后又恢复显示，原因不明…………
                 */
                edDesc.requestFocus();
//                edDesc.setCursorVisible(true);
                InputMethodManager imm2 = (InputMethodManager) edDesc.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm2.showSoftInput(edDesc, InputMethodManager.SHOW_FORCED);
                break;
            default:
                break;
        }
    }


    protected boolean saveBill() {
        if (fee <= 0 || selectType == selectSubtype) {
            ShowErrorToast();
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put("FEE", fee);
            values.put("IN_OUT",in_out);
            values.put("DATE", calendar.getTimeInMillis() / 1000);
            values.put("TYPE", selectType);
            values.put("SUBTYPE", selectSubtype);
            values.put("DESC", edDesc.getText().toString());
            return (dao.addBills("TABLE_BILL", values));
        }
    }

    @Override
    protected void refreshTypeAndAccount() {
        tvType.setText(dao.getTypeAtPosition("TABLE_ACCOUNT", selectType));
        tvSubtype.setText(dao.getTypeAtPosition("TABLE_ACCOUNT", selectSubtype));
    }

    @Override
    public void modify() {
        if (fee <= 0) {
            ShowErrorToast();
        } else {
            ContentValues values = new ContentValues();
            values.put("IN_OUT", in_out);
            values.put("FEE", fee);
            values.put("DATE", calendar.getTimeInMillis() / 1000);
            values.put("TYPE", selectType);
            values.put("SUBTYPE", selectSubtype);
            values.put("DESC", edDesc.getText().toString());

            if (dao.modifyBill(dateBefore, values)) {
                ShowModifySuccessToast();
                getActivity().setResult(RESULT_SUCCESS);
                getActivity().finish();
            } else {
                ShowModifyFailedToast();
            }
        }
    }

    //重置状态和显示
    protected void initUIAndDATA() {
        tvFee.setText("0.00");
        fee = 0.00;
        tvType.setText("现金账户");
        selectType = 1;
        tvSubtype.setText("金融账户");
        selectSubtype = 2;
        edDesc.setText("");
        //再记一笔时不对日期做修改，但要对时间做刷新
        Calendar now = Calendar.getInstance();
        calendar.set(Calendar.HOUR, now.get(Calendar.HOUR));
        calendar.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, now.get(Calendar.SECOND));
        cloneCalendar = (Calendar) calendar.clone();
    }

}
