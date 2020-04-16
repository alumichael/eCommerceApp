package com.laundry.smartwash.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laundry.smartwash.Model.Transaction.transactData;
import com.laundry.smartwash.R;
import com.laundry.smartwash.retrofit_interface.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrasactionAdapter extends RecyclerView.Adapter<TrasactionAdapter.MyViewHolder> {

    private Context context;

    private List<transactData> transactList;


    public TrasactionAdapter(Context context, List<transactData> transactList) {
        this.context = context;
        this.transactList = transactList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transact_list, parent, false);
        ButterKnife.bind(this, view);

        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        transactData transact = transactList.get(i);

        holder.mTransactDescription.setText(transact.getTransDesc());
        String amount_text="Amount: NGN "+transact.getTransAmt();
        holder.mTransactAmount.setText(amount_text);
        if(transact.getTransStatus().equals("false")){
            holder.mTransactStatus.setBackgroundColor(Color.RED);
            holder.mTransactStatus.setText("Not Successful");
        }else{
            holder.mTransactStatus.setText("Successful");
        }


    }


    @Override
    public int getItemCount() {
        return transactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.transact_description)
        TextView mTransactDescription;
        @BindView(R.id.transact_amount)
        TextView mTransactAmount;
        @BindView(R.id.transact_status)
        TextView mTransactStatus;


        ItemClickListener itemClickListener;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

           // itemView.setOnClickListener(this);
        }


    }
}

