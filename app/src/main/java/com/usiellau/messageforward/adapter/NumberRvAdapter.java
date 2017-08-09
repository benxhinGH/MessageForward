package com.usiellau.messageforward.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usiellau.messageforward.R;

import java.util.List;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class NumberRvAdapter extends RecyclerView.Adapter<NumberRvAdapter.MyViewHolder> {

    private Context context;
    private List<String> data;

    private OnItemClickListener onItemClickListener;

    public NumberRvAdapter(Context context){
        this.context=context;
    }


    @Override
    public NumberRvAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_number_rv,parent,false);
        MyViewHolder viewHolder=new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.textView.setText(data.get(position));
        if(onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView,pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos=holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView,pos);
                    return true;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<String> data){
        this.data=data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.number_tv);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }
}
