package com.rodnog.rogermiddenway.foodrescue;

import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.transition.Explode;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.rodnog.rogermiddenway.foodrescue.data.DatabaseHelper;
import com.rodnog.rogermiddenway.foodrescue.model.Food;
import com.rodnog.rogermiddenway.foodrescue.util.LabelerHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FoodItemAdapter.OnRowClickListener{
    private FirebaseAuth mAuth;

    private static final String TAG = "Main";
    DatabaseHelper db;
    List<Food> foodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getWindow().setExitTransition(new Explode());

        mAuth = FirebaseAuth.getInstance();

        db = new DatabaseHelper(this);
        foodList = db.fetchAllFoodItems();

        RecyclerView foodItemRecyclerView = findViewById(R.id.foodRecyclerView);
        FoodItemAdapter foodItemAdapter = new FoodItemAdapter(foodList, MainActivity.this, this);
        foodItemRecyclerView.setAdapter(foodItemAdapter);
        RecyclerView.LayoutManager foodLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(getDrawable(R.drawable.recyclerview_divider));

        foodItemRecyclerView.addItemDecoration(itemDecorator);
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
                        } else if (title.equals("My Cart")) {
                            Intent cartIntent = new Intent(MainActivity.this, CartActivity.class);
                            startActivity(cartIntent);
                            overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);
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
//                ActivityOptions options = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                Intent addIntent = new Intent(MainActivity.this, AddFoodActivity.class);
                startActivityForResult(addIntent, 1);
                overridePendingTransition(R.anim.pop_from_add_in, R.anim.hold);
//                finish();
//                overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }
//    @Override public void finish()
//    {
//        super.finish();
//        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Log.d("MAIN", "ACTIVITY RESULTS");
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
            this.finish();
//            Bundle tempBundle = new Bundle();
//            onCreate(tempBundle);
        }
    }

    @Override
    public void onItemClick(int position, View sharedImageView, View sharedTextView) {
        Log.d("MAIN", "Food List: " + foodList.size());
        Intent viewIntent = new Intent(MainActivity.this, FoodViewActivity.class);
        viewIntent.putExtra("FOOD_ID", foodList.get(position).getFood_id());
        Log.d("MAIN", "Food ID requested: " + foodList.get(position).getFood_id());
        Pair p1 = Pair.create(sharedImageView, "foodItemImage");
        Pair p2 = Pair.create(sharedTextView, "foodItemTitle");
//        ActivityOptionsCompat options = ActivityOptionsCompat.
//                makeSceneTransitionAnimation(MainActivity.this, shared, "foodItemImage");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, p1, p2);
        startActivity(viewIntent, options.toBundle());
//        startActivity(viewIntent);
//
//        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);
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