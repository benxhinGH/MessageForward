package com.usiellau.messageforward.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.usiellau.messageforward.R;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class SettingActivity extends AppCompatActivity {

    private String title="设置";

    private Toolbar toolbar;

    private Switch numberForwardSwitch;
    private Switch emailForwardSwitch;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViews();
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initState();

        setListeners();


    }

    private void findViews(){
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        numberForwardSwitch=(Switch)findViewById(R.id.number_forward_switch);
        emailForwardSwitch=(Switch)findViewById(R.id.email_forward_switch);
    }

    private void setListeners(){
        numberForwardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(isChecked){
                    editor.putBoolean("number_forward",true);
                }else {
                    editor.putBoolean("number_forward",false);
                }
                editor.commit();
                Toast.makeText(SettingActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        });
        emailForwardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(isChecked){
                    editor.putBoolean("email_forward",true);
                }else {
                    editor.putBoolean("email_forward",false);
                }
                editor.commit();
                Toast.makeText(SettingActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initState(){
        sharedPreferences=getSharedPreferences("setting",0);
        numberForwardSwitch.setChecked(sharedPreferences.getBoolean("number_forward",false));
        emailForwardSwitch.setChecked(sharedPreferences.getBoolean("email_forward",false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
