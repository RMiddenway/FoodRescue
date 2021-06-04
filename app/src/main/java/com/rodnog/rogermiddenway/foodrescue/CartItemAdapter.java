package com.rodnog.rogermiddenway.foodrescue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rodnog.rogermiddenway.foodrescue.model.Food;

import org.w3c.dom.Text;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>{
    private List<Food> foodItems;
    private Context context;
    private OnRowClickListener listener;

    public CartItemAdapter(List<Food> itemIds, Context context, OnRowClickListener listener){
        this.foodItems = itemIds;
        this.context = context;
        this.listener = listener;
        }
    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cartView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
            return new CartItemViewHolder(cartView, listener);

            }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        Food food = foodItems.get(position);
        holder.foodItemTitle.setText(food.getTitle());
        holder.foodItemQuantity.setText("Quantity: " + String.valueOf(food.getQuantity()));
        holder.foodItemTotalPrice.setText("$" + formatDecimal(food.getQuantity() * food.getPrice()));

    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder{
        public TextView foodItemTitle;
        public TextView foodItemQuantity;
        public TextView foodItemTotalPrice;
        public TextView removeItem;


        public CartItemViewHolder(@NonNull View itemView, CartItemAdapter.OnRowClickListener onRowClickListener) {
            super(itemView);
            foodItemTitle = itemView.findViewById(R.id.kItemTitleTextView);
            foodItemQuantity = itemView.findViewById(R.id.kItemQuantityTextView);
            foodItemTotalPrice = itemView.findViewById(R.id.kItemPriceTotalTextView);
            removeItem = itemView.findViewById(R.id.kRemoveItem);
            removeItem.setOnClickListener(v -> onRowClickListener.onRemoveItem(this.getAdapterPosition()));
        }
    }
    public interface OnRowClickListener {
        void onRemoveItem(int position);
    }
    public String formatDecimal(float number) {
        float epsilon = 0.004f; // 4 tenths of a cent
        if (Math.abs(Math.round(number) - number) < epsilon) {
            return String.format("%10.0f", number); // sdb
        } else {
            return String.format("%10.2f", number); // dj_segfault
        }
    }
}
