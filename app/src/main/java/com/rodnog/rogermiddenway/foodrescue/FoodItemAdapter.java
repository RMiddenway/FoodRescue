package com.rodnog.rogermiddenway.foodrescue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rodnog.rogermiddenway.foodrescue.model.Food;

import java.io.File;
import java.util.List;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {
    private List<Food> foodList;
    private Context context;
    private OnRowClickListener listener;

    public FoodItemAdapter(List<Food> foodList, Context context, OnRowClickListener listener) {
        this.foodList = foodList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View foodView = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new FoodItemViewHolder(foodView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
        String path = foodList.get(position).getImage_path();
        if(path != null) {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.foodItemImageView.setImageBitmap(myBitmap);
            } else {
                holder.foodItemImageView.setImageResource(R.drawable.question_mark);
            }
        }
        else{
            holder.foodItemImageView.setImageResource(R.drawable.question_mark);
        }
        holder.foodTitleTextView.setText(foodList.get(position).getTitle());
        holder.foodDescriptionTextView.setText(foodList.get(position).getShortDescription());

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class FoodItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView foodItemImageView;
        public TextView foodTitleTextView;
        public TextView foodDescriptionTextView;
        public ImageButton shareImageButton;
        public OnRowClickListener onRowClickListener;


        public FoodItemViewHolder(@NonNull View itemView, OnRowClickListener onRowClickListener) {
            super(itemView);
            foodItemImageView = itemView.findViewById(R.id.pFoodImageView);
            foodTitleTextView = itemView.findViewById(R.id.pFoodTitleTextView);
            foodDescriptionTextView = itemView.findViewById(R.id.pFoodDescriptionTextView);
            shareImageButton = itemView.findViewById(R.id.shareImageButton);

            itemView.setOnClickListener(v -> onRowClickListener.onItemClick(this.getAdapterPosition()));
            shareImageButton.setOnClickListener(v -> onRowClickListener.onShareButtonClick(this.getAdapterPosition()));
        }
    }

    public interface OnRowClickListener {
        void onItemClick(int position);
        void onShareButtonClick(int position);
    }
}
