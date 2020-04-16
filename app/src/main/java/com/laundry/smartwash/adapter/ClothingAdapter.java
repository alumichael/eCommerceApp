package com.laundry.smartwash.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.laundry.smartwash.Constant;
import com.laundry.smartwash.Model.ClothList.ClothGetData;
import com.laundry.smartwash.Model.OrderPost.OrderedCloths;
import com.laundry.smartwash.R;
import com.laundry.smartwash.UserMain_Fragment.service_activities.serviceForm;
import com.laundry.smartwash.UserPreferences;
import com.laundry.smartwash.retrofit_interface.ItemClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.MyViewHolder> {

    private Context context;
    private List<ClothGetData> clothList;
    private List<OrderedCloths> orderedCloths;
    private List<String> countOne;
    private List<String> countList;
    String price;
    String counting_txt="";

    UserPreferences userPreferences;
    String result;
    int count;
    int Deduct_count;
    int total_count=0;
    int total_price=0;
    int sub_price=0;

    boolean check=false;
    String cate_name;

    private SparseBooleanArray selectedItems;
    private MessageAdapterListener listener;

    private static int currentSelectedIndex = -1;


    public ClothingAdapter(Context context, List<ClothGetData> clothList, MessageAdapterListener listener,String price,String cate_name) {
        this.context = context;
        this.clothList = clothList;
        this.listener = listener;
        this.price = price;
        this.cate_name = cate_name;
        selectedItems = new SparseBooleanArray();
        userPreferences=new UserPreferences(context);
        countList=new ArrayList<>();

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

        holder.cloth_name.setText(clothOption.getClothName());

        if(cate_name.equals("Family Wash")){
            if(!clothOption.getClothAmount().equals("0")){
                holder.cloth_amount.setVisibility(View.VISIBLE);

                if(clothOption.getClothName().equals("Liquid Soap")){
                    String clothe_type="NGN "+clothOption.getClothAmount()+": / Litre";
                    holder.cloth_amount.setText(clothe_type);
                }else{
                    String clothe_type="Special Cloth: NGN"+clothOption.getClothAmount();
                    holder.cloth_amount.setText(clothe_type);
                }

            }else{
                holder.cloth_amount.setVisibility(View.INVISIBLE);
            }

        }else{
            if(clothOption.getClothName().equals("Liquid Soap")){
                holder.cloth_amount.setVisibility(View.VISIBLE);
                String clothe_type="NGN "+clothOption.getClothAmount()+": / Litre";
                holder.cloth_amount.setText(clothe_type);
            }else{

                holder.cloth_amount.setVisibility(View.INVISIBLE);
            }
        }


        holder.itemView.setActivated(selectedItems.get(i, false));

        if (userPreferences.getCount() == 0) {
            count = 0;
        }

        holder.choose_cloth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( !holder.choose_cloth.isChecked()) {
                    listener.onClothClicked(i);
                    // holder.choose_cloth.setChecked(false);
                    Deduct_count = Integer.parseInt(String.valueOf(holder.count_text.getText()));
                    userPreferences.setTotalPicked(userPreferences.getTotalPicked()-Deduct_count);



                    if(clothOption.getClothAmount().equals("0")){
                        sub_price=Deduct_count * Integer.parseInt(price);
                        userPreferences.setTotalPrice(userPreferences.getTotalPrice()-sub_price);
                    }else{
                        sub_price=Deduct_count * Integer.parseInt(clothOption.getClothAmount());
                        userPreferences.setTotalPrice(userPreferences.getTotalPrice()-sub_price);
                    }

                    holder.count_text.setText("0");
                    holder.count_layout.setVisibility(View.INVISIBLE);

                }else{

                    holder.count_layout.setVisibility(View.VISIBLE);
                    listener.onClothClicked(i);

                }


            }
        });

        holder.count_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                count = Integer.parseInt(String.valueOf(holder.count_text.getText()));
                Log.i("count", String.valueOf(holder.count_text.getText()));

                total_count = userPreferences.getTotalPicked();

                //count=userPreferences.getCount();

                if (count >= 0) {

                    count--;
                    userPreferences.setCount(count);

                    result = String.valueOf(userPreferences.getCount());

                    holder.count_text.setText(result);
                    clothOption.setQuantity(Integer.parseInt(result));
                    total_count = Integer.parseInt(String.valueOf(holder.count_text.getText()));
                    userPreferences.setTotalPicked(userPreferences.getTotalPicked() - 1);

                    if(clothOption.getClothAmount().equals("0")){
                        sub_price= Integer.parseInt(price);
                        userPreferences.setTotalPrice(userPreferences.getTotalPrice()-sub_price);
                    }else{
                        sub_price=Integer.parseInt(clothOption.getClothAmount());
                        userPreferences.setTotalPrice(userPreferences.getTotalPrice()-sub_price);
                    }

                } else {
                    result = String.valueOf(0);
                    userPreferences.setCount(Integer.parseInt(result));

                }

                userPreferences.setCount(Integer.parseInt(result));

            }
        });

        holder.count_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                count = Integer.parseInt(String.valueOf(holder.count_text.getText()));
                Log.i("count2", String.valueOf(holder.count_text.getText()));



                if (count >= 0) {
                    count++;
                    userPreferences.setCount(count);
                    result = String.valueOf(userPreferences.getCount());
                    holder.count_text.setText(result);
                    clothOption.setQuantity(Integer.parseInt(result));
                    total_count = Integer.parseInt(String.valueOf(holder.count_text.getText()));
                    userPreferences.setTotalPicked(userPreferences.getTotalPicked() + 1);

                    if(clothOption.getClothAmount().equals("0")){
                        sub_price= Integer.parseInt(price);
                        userPreferences.setTotalPrice(userPreferences.getTotalPrice()+sub_price);
                    }else{
                        sub_price= Integer.parseInt(clothOption.getClothAmount());
                        userPreferences.setTotalPrice(userPreferences.getTotalPrice()+sub_price);
                    }


                } else {
                    result = String.valueOf(0);
                    userPreferences.setCount(Integer.parseInt(result));
                }

                userPreferences.setCount(Integer.parseInt(result));

                //countList.add(0,result);

                //Log.i("list",countList.toString());

            }
        });






    }





    public interface MessageAdapterListener {
        void onClothClicked(int position);

    }


    @Override
    public int getItemCount() {
        return clothList.size();
    }



    public void toggleSelection(int pos) {

        if (selectedItems.get(pos, false) ) {
            selectedItems.delete(pos);

        } else {
            selectedItems.put(pos, true);
        }

    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }


    public List<OrderedCloths> getSelectedItems() {

        List<OrderedCloths> items =
                new ArrayList<>(selectedItems.size());

        for (int i = 0; i < selectedItems.size(); i++) {

            ClothGetData cloth_data = clothList.get(selectedItems.keyAt(i));

            OrderedCloths order_data = new OrderedCloths(cloth_data.getClothName(),cloth_data.getQuantity());
            items.add(order_data);

        }
        return items;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.cloth_name)
        TextView cloth_name;

        @BindView(R.id.cloth_amount)
        TextView cloth_amount;

        @BindView(R.id.count_less)
        ImageButton count_less;

        @BindView(R.id.count_text)
        TextView count_text;

        @BindView(R.id.count_more)
        ImageButton count_more;

        @BindView(R.id.choose_cloth)
        AppCompatCheckBox choose_cloth;

        @BindView(R.id.count_layout)
        LinearLayout count_layout;




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

