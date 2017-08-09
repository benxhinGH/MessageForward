package com.usiellau.messageforward;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.List;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class ForwardNumberActivity extends AppCompatActivity {

    private final String title="转发号码";

    private Toolbar toolbar;
    private RecyclerView forwardNumberRv;
    private List<String> numberListData;
    private NumberRvAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_number);
        findViews();
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        initViews();
        setListeners();

    }

    private void findViews(){
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        forwardNumberRv=(RecyclerView) findViewById(R.id.number_rv);
    }

    private void setListeners(){
        adapter.setOnItemClickListener(new NumberRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                final String number=numberListData.get(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(ForwardNumberActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除转发号码"+number+"?");
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
    }

    private void initViews(){
        numberListData=DBUtil.queryForwardNumber();
        adapter=new NumberRvAdapter(this);
        adapter.setData(numberListData);
        forwardNumberRv.setAdapter(adapter);
        forwardNumberRv.setLayoutManager(new LinearLayoutManager(this));
        forwardNumberRv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forward_number,menu);
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
        builder.setTitle("添加转发号码");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String number=editText.getText().toString();
                SQLiteDatabase database=DBUtil.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("number",number);
                database.insert("number_forward",null,values);
                refreshNumberList();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void refreshNumberList(){
        List<String> temp=DBUtil.queryForwardNumber();
        Util.listDataCopy(numberListData,temp);
        adapter.notifyDataSetChanged();
    }
}
