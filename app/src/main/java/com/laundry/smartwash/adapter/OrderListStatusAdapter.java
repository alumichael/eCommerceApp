package com.laundry.smartwash.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laundry.smartwash.Model.OrderPost.OrderedCloths;
import com.laundry.smartwash.R;
import com.laundry.smartwash.retrofit_interface.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderListStatusAdapter extends RecyclerView.Adapter<OrderListStatusAdapter.MyViewHolder> {

    private Context context;

    private List<OrderedCloths> orderedCloths;


    public OrderListStatusAdapter(Context context, List<OrderedCloths> orderedCloths) {
        this.context = context;
        this.orderedCloths = orderedCloths;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list, parent, false);
        ButterKnife.bind(this, view);

        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        OrderedCloths ordered = orderedCloths.get(i);
        holder.cloth_name.setText(ordered.getCloth());
        holder.count_text.setText(String.valueOf(ordered.getQuantity()));

    }


    @Override
    public int getItemCount() {
        return orderedCloths.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.cloth_name)
        TextView cloth_name;
        @BindView(R.id.count_text)
        TextView count_text;


        ItemClickListener itemClickListener;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

           // itemView.setOnClickListener(this);
        }


    }
}
