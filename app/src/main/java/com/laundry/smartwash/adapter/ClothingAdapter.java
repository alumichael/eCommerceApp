package com.laundry.smartwash.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laundry.smartwash.Constant;
import com.laundry.smartwash.Model.ClothList.ClothGetData;
import com.laundry.smartwash.R;
import com.laundry.smartwash.UserMain_Fragment.service_activities.serviceForm;
import com.laundry.smartwash.UserPreferences;
import com.laundry.smartwash.retrofit_interface.ItemClickListener;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.MyViewHolder> {

    private Context context;
    private List<ClothGetData> clothList;
    String price;
    String counting_txt="";

    UserPreferences userPreferences;
    String result;
    int count;
    int total_count=0;



    public ClothingAdapter(Context context, List<ClothGetData> clothList) {
        this.context = context;
        this.clothList = clothList;
        userPreferences=new UserPreferences(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cloth_list, parent, false);
        ButterKnife.bind(this, view);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        ClothGetData clothOption = clothList.get(i);
        Log.i("countFormer", String.valueOf(userPreferences.getCount()));
         if(userPreferences.getCount()==0){
             count=0;
         }

        holder.cloth_name.setText(clothOption.getClothName());

        holder.count_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count=Integer.parseInt(String.valueOf(holder.count_text.getText()));
                Log.i("count", String.valueOf(holder.count_text.getText()));

                total_count=userPreferences.getTotalPicked();

                //count=userPreferences.getCount();

                if (count>=0){

                    count--;
                    userPreferences.setCount(count);

                    result= String.valueOf(userPreferences.getCount());
                    holder.count_text.setText(result);

                    total_count=Integer.parseInt(String.valueOf(holder.count_text.getText()));
                    userPreferences.setTotalPicked(userPreferences.getTotalPicked()-1);


                }else{
                    result= String.valueOf(0);
                    userPreferences.setCount(Integer.parseInt(result));

                }

                userPreferences.setCount(Integer.parseInt(result));
            }
        });

        holder.count_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count=Integer.parseInt(String.valueOf(holder.count_text.getText()));
                Log.i("count2", String.valueOf(holder.count_text.getText()));

                if (count>=0){
                    count++;
                    userPreferences.setCount(count);

                    result= String.valueOf(userPreferences.getCount());
                    holder.count_text.setText(result);

                    total_count=Integer.parseInt(String.valueOf(holder.count_text.getText()));
                    userPreferences.setTotalPicked(userPreferences.getTotalPicked()+1);

                }else{
                    result= String.valueOf(0);
                    userPreferences.setCount(Integer.parseInt(result));
                }

                userPreferences.setCount(Integer.parseInt(result));

            }
        });




       /* holder.setItemClickListener(pos -> {



            holder.count_less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

            holder.count_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    count[0]++;
                    userPreferences.setCount(count[0]);
                }
            });


            Log.i("count", String.valueOf(userPreferences.getCount()));

            result= String.valueOf(userPreferences.getCount());

            holder.count_text.setText(result);


        });*/
    }



    private void nextActivity(String cate_name,String cate_price, Class productActivityClass) {
        Intent i = new Intent(context, productActivityClass);
        i.putExtra(Constant.CATE_NAME, cate_name);
        i.putExtra(Constant.CATE_PRICE, cate_price);
        context.startActivity(i);
    }





    @Override
    public int getItemCount() {
        return clothList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.cloth_name)
        TextView cloth_name;

        @BindView(R.id.count_less)
        ImageButton count_less;

        @BindView(R.id.count_text)
        TextView count_text;

        @BindView(R.id.count_more)
        ImageButton count_more;



        ItemClickListener itemClickListener;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

           // itemView.setOnClickListener(this);
        }

       /* public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {

            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }*/

    }
}

