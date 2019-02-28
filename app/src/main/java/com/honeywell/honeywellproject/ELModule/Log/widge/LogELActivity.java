package com.honeywell.honeywellproject.ELModule.Log.widge;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.ELModule.Log.adapter.LogELAdapter;
import com.honeywell.honeywellproject.ELModule.Log.data.LogELBean;
import com.honeywell.honeywellproject.LoginModule.widge.LoginELActivity;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.ConstantUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * @author QHT
 */
public class LogELActivity extends ToolBarActivity {

    @BindView(R.id.rv_logtask)
    RecyclerView rvLogtask;
    @BindView(R.id.view_empty)
    TextView     viewEmpty;
    private LogELAdapter madapter;
    private List<LogELBean> logBeanList = new ArrayList<LogELBean>();

    private BleDevice mDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initLogList();


 }



    @Override
    public int getContentViewId() {
        return R.layout.activity_log;
    }


    private void initView() {
        getToolbarTitle().setText("历史记录");
        rvLogtask.setLayoutManager(new LinearLayoutManager(this));
        madapter = new LogELAdapter(logBeanList);
        rvLogtask.setAdapter(madapter);
    }

    private void initLogList() {
        logBeanList.clear();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String data = simpleDateFormat.format(new Date());
        //清除历史的编址信息，只保留当天的
        DataSupport.deleteAll(LogELBean.class, "substr(data,1,10)  != ? ", data);
        List<LogELBean>  logBeanList2 = DataSupport.where("user = ? ", SharePreferenceUtil.getStringSP("currentusername_el", "")).find(LogELBean.class);
        logBeanList.addAll(logBeanList2);
        if (logBeanList.size() == 0) {
            viewEmpty.setVisibility(View.VISIBLE);
        } else {
            madapter.notifyDataSetChanged();
        }
    }
}
