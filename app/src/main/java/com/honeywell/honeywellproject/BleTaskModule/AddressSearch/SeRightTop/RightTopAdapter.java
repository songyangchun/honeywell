package com.honeywell.honeywellproject.BleTaskModule.AddressSearch.SeRightTop;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.WidgeView.RangeSeekBar;
import com.honeywell.honeywellproject.WidgeView.SwitchButton;

import java.util.List;

/**
 * Created by QHT on 2018-01-20.
 * addItemType(RightTopBean.protocol, R.layout.activity_selectaddr_xieyi_topright_item);
 //addItemType(RightTopBean.audio, R.layout.activity_freeaddr_audio_topright_item);
 addItemType(RightTopBean.degree, R.layout.activity_selectaddr_degree_topright_item);
 */
public class RightTopAdapter extends BaseMultiItemQuickAdapter<RightTopBean, BaseViewHolder> {
    public String progressData="50";
    private  int pos;
    public  String cardNumber;
    public interface  RightTopAudioOnCheckedChangeListener{
        void check(SwitchButton button, boolean isSelect);
    }
    public void setRightTopAudioOnCheckedChangeListener(RightTopAudioOnCheckedChangeListener onCheckedChangeListener){
        this.onCheckedChangeListener=onCheckedChangeListener;
    }
    private RightTopAudioOnCheckedChangeListener onCheckedChangeListener;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    private Context context;
    public RightTopAdapter(List<RightTopBean> data, Context context) {
        super(data);
        addItemType(RightTopBean.protocol, R.layout.activity_selectaddr_xieyi_topright_item);
        //addItemType(RightTopBean.audio, R.layout.activity_freeaddr_audio_topright_item);
        addItemType(RightTopBean.searchdegree, R.layout.activity_selectaddr_degree_topright_item);
        this.context=context;
    }

    @Override
    protected void convert(BaseViewHolder helper, RightTopBean item) {
        switch (helper.getItemViewType()) {

            case RightTopBean.protocol:
                final Spinner spinner = ((Spinner)helper.getView(R.id.spinner));
                String protopolType=SharePreferenceUtil.getStringSP("ProtopolType","CLIP");
               if (protopolType.equals("CLIP")){
                    pos=0;
               }
                if (protopolType.equals("DLIP")){
                    pos=1;
                }
                if (protopolType.equals("FLASHSCAN")){
                    pos=2;
                }
               spinner.setSelection(pos);
               spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                       //设置显示当前选择的项

                       arg0.setVisibility(View.VISIBLE);
                       cardNumber = mContext.getResources().getStringArray(R.array.type)[arg2];
                       SharePreferenceUtil.setStringSP("ProtopolType",cardNumber);
//                       ToastUtil.showToastShort(cardNumber);
                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> parent) {

                   }
               });



                break;
            case RightTopBean.searchdegree:
                final RangeSeekBar searchseekBar=((RangeSeekBar)helper.getView(R.id.seek2));
                //这是下次点击九宫格里面有缓存
                //searchseekBar.setValue(Float.parseFloat(SharePreferenceUtil.getStringSP("searchdegree","2")));
                searchseekBar.setValue(Float.parseFloat(SharePreferenceUtil.getStringSP("searchdegree","50")));
                searchseekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
                    @Override
                    public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                        //searchseekBar.setProgressDescription((int)min+"");
                        progressData=((int)min)+"";
                        SharePreferenceUtil.setStringSP("progressData",progressData);
                    }
                    @Override
                    public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                    }

                    @Override
                    public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                        //SharePreferenceUtil.setStringSP("searchdegree",view.getCurrentRange()[0]+"");
                        SharePreferenceUtil.setStringSP("searchdegree",progressData);
                    }
                });
                break;
            default:
        }
    }
}

