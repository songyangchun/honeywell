package com.honeywell.honeywellproject.BaseActivity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import butterknife.ButterKnife;

/**
 * The type Super activity.
 * @author QHT
 */
public abstract class AbstractSuperActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
    }

    /**
     * Gets content view id.
     *
     * @return the content view id
     */
    public abstract int getContentViewId();

    //APP字体不随手机字体设置大小而变化
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return resources;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

