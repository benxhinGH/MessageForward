package com.usiellau.messageforward.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.usiellau.messageforward.database.DBUtil;
import com.usiellau.messageforward.adapter.NumberRvAdapter;
import com.usiellau.messageforward.R;
import com.usiellau.messageforward.other.Util;

import java.util.List;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class MonitorNumberActivity extends AppCompatActivity {

    private final String title="监听号码";

    private Toolbar toolbar;
    private RecyclerView monitorNumberRv;
    private List<String> numberListData;
    private NumberRvAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_number);
        findViews();
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        setListeners();

    }

    private void findViews(){
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        monitorNumberRv=(RecyclerView) findViewById(R.id.number_rv);
    }

    private void setListeners(){
        adapter.setOnItemClickListener(new NumberRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                final String number=numberListData.get(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(MonitorNumberActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除监听号码"+number+"?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBUtil.deleteMonitorNumberByNumber(number);
                        refreshNumberList();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews(){
        numberListData=DBUtil.queryMonitorNumber();
        adapter=new NumberRvAdapter(this);
        adapter.setData(numberListData);
        monitorNumberRv.setAdapter(adapter);
        monitorNumberRv.setLayoutManager(new LinearLayoutManager(this));
        monitorNumberRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        monitorNumberRv.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_monitor_number,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_number:
                showAddNumberDialog();
                break;
            default:
                break;
        }
        return true;
    }

    private void showAddNumberDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_add_number,null);
        final EditText editText=(EditText)view.findViewById(R.id.number_et);
        builder.setTitle("添加监听号码");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String number=editText.getText().toString();
                SQLiteDatabase database=DBUtil.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("number",number);
                database.insert("number_monitor",null,values);
                refreshNumberList();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void refreshNumberList(){
        List<String> temp=DBUtil.queryMonitorNumber();
        Util.listDataCopy(numberListData,temp);
        adapter.notifyDataSetChanged();
    }
}
