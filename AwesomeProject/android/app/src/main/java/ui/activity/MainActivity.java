package ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.awesomeproject.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_start_online_tv).setOnClickListener(this);
        findViewById(R.id.main_start_assets_tv).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent it = new Intent();
        switch (v.getId()){
            case R.id.main_start_online_tv:
                it.setClass(this,ReactOnlineActivity.class);
                break;
            case R.id.main_start_assets_tv:
                it.setClass(this,ReactAssetActivity.class);
                break;
        }
        startActivity(it);
    }
}
