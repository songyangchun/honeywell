package com.honeywell.honeywellproject.Util;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.honeywell.honeywellproject.R;

public class AddressDialogUtil {


    public interface  OnTipsClick {

        void click(Dialog dialog, String newadd, String position);

        void clickCancel(Dialog dialog);


    }

        public static void showTwoButtonDialog(Context context , String message, final String position,
                                               final OnTipsClick clicker) {
            View view = LayoutInflater.from(context).inflate(R.layout.addressdialog, null);
            TextView confirm;    //确定按钮
            TextView cancel;     //取消按钮
            final TextView content;    //内容
            final TextView address;
            TextView initaddress;
            final TextView newaddress;
            final EditText et_newaddress;
            final EditText editText = new EditText(context);


            confirm = (TextView) view.findViewById(R.id.dialog_btn_comfirm);
            content = (TextView) view.findViewById(R.id.dialog_txt_content);
            cancel = (TextView) view.findViewById(R.id.dialog_btn_cancel);
            address = (TextView) view.findViewById(R.id.tv_address);
            initaddress = (TextView) view.findViewById(R.id.initaddress);
            newaddress = (TextView) view.findViewById(R.id.newaddress);

            initaddress = (TextView) view.findViewById(R.id.initaddress);
            et_newaddress = (EditText) view.findViewById(R.id.et_newaddress);
            initaddress.setText(position);
            content.setText(message);

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(view);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clicker.click(dialog,et_newaddress.getText().toString().trim(),position);
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clicker.clickCancel(dialog);
                }
            });
            dialog.show();
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int displayWidth = dm.widthPixels;
            int displayHeight = dm.heightPixels;
            android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
            p.width = (int) (displayWidth * 0.9);    //宽度设置为屏幕的0.5
            p.height = (int) (displayHeight * 0.5);    //宽度设置为屏幕的0.5
            dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            dialog.getWindow().setAttributes(p);     //设置生效


        }







    }


