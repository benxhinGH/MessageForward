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

import com.usiellau.messageforward.R;
import com.usiellau.messageforward.adapter.NumberRvAdapter;
import com.usiellau.messageforward.database.DBUtil;
import com.usiellau.messageforward.other.Util;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class ForwardEmailActivity extends AppCompatActivity {

    private final String title="转发邮箱";

    private Toolbar toolbar;
    private RecyclerView forwardEmailRv;
    private List<String> emailRvData;
    private NumberRvAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_email);
        findViews();
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        setListeners();

    }

    private void findViews(){
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        forwardEmailRv=(RecyclerView) findViewById(R.id.email_rv);
    }

    private void setListeners(){
        adapter.setOnItemClickListener(new NumberRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                final String email=emailRvData.get(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(ForwardEmailActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除转发邮箱"+email+"?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBUtil.deleteForwardEmailByAddress(email);
                        refreshEmailList();
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
        emailRvData=DBUtil.queryForwardEmail();
        adapter=new NumberRvAdapter(this);
        adapter.setData(emailRvData);
        forwardEmailRv.setAdapter(adapter);
        forwardEmailRv.setLayoutManager(new LinearLayoutManager(this));
        forwardEmailRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        forwardEmailRv.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forward_email,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_email:
                showAddEmailDialog();
                break;
            default:
                break;
        }
        return true;
    }

    private void showAddEmailDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_add_email,null);
        final EditText editText=(EditText)view.findViewById(R.id.email_et);
        builder.setTitle("添加转发邮箱");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address=editText.getText().toString();
                SQLiteDatabase database=DBUtil.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("address",address);
                database.insert("email_forward",null,values);
                refreshEmailList();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void refreshEmailList(){
        List<String> temp=DBUtil.queryForwardEmail();
        Util.listDataCopy(emailRvData,temp);
        adapter.notifyDataSetChanged();
    }
}
