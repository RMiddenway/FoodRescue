package com.rodnog.rogermiddenway.foodrescue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rodnog.rogermiddenway.foodrescue.data.DatabaseHelper;
import com.rodnog.rogermiddenway.foodrescue.model.Food;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FoodItemAdapter.OnRowClickListener{

    DatabaseHelper db;
    List<Food> foodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        foodList = db.fetchAllFoodItems();

        RecyclerView foodItemRecyclerView = findViewById(R.id.foodRecyclerView);
        FoodItemAdapter foodItemAdapter = new FoodItemAdapter(foodList, MainActivity.this, this);
        foodItemRecyclerView.setAdapter(foodItemAdapter);
        RecyclerView.LayoutManager foodLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        foodItemRecyclerView.setLayoutManager(foodLayoutManager);

        TextView headerTextView = findViewById(R.id.headerTextView);

        ImageButton optionsMenuButton = findViewById(R.id.optionsMenuButton);
        optionsMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(MainActivity.this, optionsMenuButton);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        CharSequence title = item.getTitle();
                        if (title.equals("Home")) {
                            headerTextView.setText("Discover free food ");
                            db = new DatabaseHelper(MainActivity.this);
                            foodList = db.fetchAllFoodItems();

                            RecyclerView foodItemRecyclerView = findViewById(R.id.foodRecyclerView);
                            FoodItemAdapter foodItemAdapter = new FoodItemAdapter(foodList,
                                    MainActivity.this, MainActivity.this);
                            foodItemRecyclerView.setAdapter(foodItemAdapter);
                            RecyclerView.LayoutManager foodLayoutManager = new LinearLayoutManager(
                                    MainActivity.this, LinearLayoutManager.VERTICAL, false);
                            foodItemRecyclerView.setLayoutManager(foodLayoutManager);
                            //TODO GO HOME
                        } else if (title.equals("Account")) {

                        } else if (title.equals("My List")) {
                            headerTextView.setText("My List");
                            db = new DatabaseHelper(MainActivity.this);
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(
                                    MainActivity.this);
                            int user_id = sharedPref.getInt("CURRENT_USER", -1);
                            foodList = db.fetchAllFoodItems(user_id);

                            RecyclerView foodItemRecyclerView = findViewById(R.id.foodRecyclerView);
                            FoodItemAdapter foodItemAdapter = new FoodItemAdapter(foodList,
                                    MainActivity.this, MainActivity.this);
                            foodItemRecyclerView.setAdapter(foodItemAdapter);
                            RecyclerView.LayoutManager foodLayoutManager = new LinearLayoutManager(
                                    MainActivity.this, LinearLayoutManager.VERTICAL, false);
                            foodItemRecyclerView.setLayoutManager(foodLayoutManager);
//                            Toast.makeText(MainActivity.this, "User id: " + String.valueOf(user_id), Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });

                popup.show();

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this, AddFoodActivity.class);
                startActivityForResult(addIntent, 1);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            this.finish();
        }
    }

    @Override
    public void onItemClick(int position) {
    }
    @Override
    public void onShareButtonClick(int position) {
        Food selectedFood = foodList.get(position);
//        Toast.makeText(MainActivity.this, "Clicked " + selectedFood.getTitle(), Toast.LENGTH_SHORT).show();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Come and get it! - " + selectedFood.getTitle());
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Come and rescue this food before it goes to waste! "
                + "\n" + selectedFood.getTitle() + "\n" + selectedFood.getDescription() + "\nDownload Food Rescue to see the full details");
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}