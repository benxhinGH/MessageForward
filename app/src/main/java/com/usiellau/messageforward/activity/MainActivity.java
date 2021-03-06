package com.usiellau.messageforward.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.usiellau.messageforward.service.MonitorService;
import com.usiellau.messageforward.R;
import com.usiellau.messageforward.other.Util;
import com.usiellau.messageforward.database.DBUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class MainActivity extends AppCompatActivity {

    private final String title="短信转发Demo";

    private String[] permissions={Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS,
    Manifest.permission.SEND_SMS,Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Toolbar toolbar;

    private ToggleButton msgFrdToggleBtn;
    private Button monitorNumberBtn;
    private Button forwardNumberBtn;
    private Button forwardEmailBtn;
    private Button settingBtn;

    private SharedPreferences stateSp;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        initState();
        setListeners();
        checkPermission();
    }

    private void findViews(){
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        msgFrdToggleBtn=(ToggleButton)findViewById(R.id.msg_frd_toggle_btn);
        monitorNumberBtn=(Button)findViewById(R.id.monitor_number_btn);
        forwardNumberBtn=(Button)findViewById(R.id.forward_number_btn);
        forwardEmailBtn=(Button)findViewById(R.id.forward_email_btn);
        settingBtn=(Button)findViewById(R.id.setting_btn);
    }

    private void initState(){
        stateSp=getSharedPreferences("state",0);
        if(Util.isServiceRunning(this,".service.MonitorService")){
            msgFrdToggleBtn.setChecked(true);
            SharedPreferences.Editor editor=stateSp.edit();
            editor.putBoolean("serviceRunning",false);
            editor.apply();
        }
    }

    private void setListeners(){
        msgFrdToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    List<String> monitorNumber= DBUtil.queryMonitorNumber();
                    if(monitorNumber.size()==0){
                        Toast.makeText(MainActivity.this, "监听号码为空", Toast.LENGTH_SHORT).show();
                        msgFrdToggleBtn.setChecked(false);
                    }else{
                        Intent intent=new Intent(MainActivity.this,MonitorService.class);
                        startService(intent);
                        stateSp.edit().putBoolean("serviceRunning",true).apply();
                    }

                }else{
                    Intent intent=new Intent(MainActivity.this,MonitorService.class);
                    stopService(intent);
                    stateSp.edit().putBoolean("serviceRunning",false).apply();
                }
            }
        });
        monitorNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MonitorNumberActivity.class);
                startActivity(intent);
            }
        });
        forwardNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ForwardNumberActivity.class);
                startActivity(intent);
            }
        });
        forwardEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ForwardEmailActivity.class);
                startActivity(intent);
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkPermission(){
        for(String permission:permissions){
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,permissions,0);
                break;
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "权限未获取，退出应用", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(this, "权限获取成功！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
