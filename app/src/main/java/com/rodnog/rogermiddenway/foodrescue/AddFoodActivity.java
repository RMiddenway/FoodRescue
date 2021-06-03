package com.rodnog.rogermiddenway.foodrescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.rodnog.rogermiddenway.foodrescue.data.DatabaseHelper;
import com.rodnog.rogermiddenway.foodrescue.model.Food;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddFoodActivity extends AppCompatActivity implements AddFoodBeginFragment.OnAddBeginListener,
        AddFoodMiddleFragment.OnAddMiddleListener, AddFoodEndFragment.OnAddEndListener {


    Button backButton;
    Button forwardButton;

    Fragment currentFragment;

    DatabaseHelper db;
//    int userId;
    ImageView foodImageView;
    ProgressBar tagProgressBar;

    TextView foodTitleTextView;
    String foodTitle;
    String foodImagePath;
    String foodDescription;
    int foodQuantity;
    List<String> possibleFoodTags;
    List<String> foodTags;
    LatLng foodLocation;
    long foodStartTime;
    long foodEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        foodImageView = findViewById(R.id.aFoodImageView);
        foodTitleTextView = findViewById(R.id.aFoodTitleTextView);
        tagProgressBar = findViewById(R.id.tagProgressBar);

        backButton = findViewById(R.id.aBackButton);
        forwardButton = findViewById(R.id.aForwardButton);
        setButtons(0);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        forwardButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        Fragment fragment = AddFoodBeginFragment.newInstance(); //TODO SEND IT LIST OF KEYWORDS FROM ANNOTATE
        currentFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.addFoodFragmentContainer, fragment)
                .addToBackStack("0")
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 2);
            } else {
                Toast.makeText(this, "Wrong permissions received", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(this, "Wrong request code", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImage(Context context, Bitmap image) {

        String savedImagePath = null;

        // Create the new file in the external storage
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/FoodRescue");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        Toast.makeText(AddFoodActivity.this, String.valueOf(success), Toast.LENGTH_LONG).show();
        // Save the new Bitmap
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Show a Toast with the save location
//            String savedMessage = "Saved";
//            Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show();
        }

        return savedImagePath;
    }


//    @Override
//    public void onBeginAdded(String path, String title) {
//        //TODO ADD DETAILS TO FIELDS
//        foodImagePath = path;
//        foodTitle = title;
//        Fragment fragment = AddFoodMiddleFragment.newInstance(path, title); //TODO SEND IT LIST OF KEYWORDS FROM ANNOTATE
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.addFoodFragmentContainer, fragment)
//                .addToBackStack("0")
//                .commit();
//    }

//    @Override
//    public void onCancel() {
//        finish();
//    }

//    @Override
//    public void onMiddleAdded(String description, List<String> tags, int quantity) {
//        foodDescription = description;
//        foodTags = tags;
//        foodQuantity = quantity;
//        Fragment fragment = AddFoodEndFragment.newInstance(foodImagePath, foodTitle);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.addFoodFragmentContainer, fragment)
//                .addToBackStack("0")
//                .commit();
//    }

    @Override
    public void onCloseC(LatLng location, long startTime, long endTime) {
        //TODO WRITE IT ALL TO THE DB AND TOAST IF IT WORKED
//        foodLocation = location;
//        foodStartTime = startTime;
//        foodEndTime = endTime;
//
//        Food newFoodItem = new Food();
//        newFoodItem.setTitle(foodTitle);
//        newFoodItem.setImage_path(foodImagePath);
//        newFoodItem.setDescription(foodDescription);
//        newFoodItem.setQuantity(foodQuantity);
//        newFoodItem.setLocation(foodLocation.latitude, foodLocation.longitude);
//        newFoodItem.setStartTime(foodStartTime);
//        newFoodItem.setEndTime(foodEndTime);
//        newFoodItem.setPrice(10); //TODO integrate price entry into UI
//        DatabaseHelper db = new DatabaseHelper(this);
//        long result = db.insertFood(newFoodItem);
//        if(result > 0){
//            Log.d("ADDFOOD", "Food Item Added");
//            Toast.makeText(this, "Food Item Added!", Toast.LENGTH_SHORT).show();
//        }
//        setResult(RESULT_OK, null);
//        finish();
    }

    @Override
    public void onLocationSelected(LatLng location) {
        foodLocation = location;
    }

    @Override
    public void onStartTimeSelected(long startTime) {
        foodStartTime = startTime;
    }

    @Override
    public void onEndTimeSelected(long endTime) {
        foodEndTime = endTime;
    }

    @Override
    public void onPictureAdded(String path) {
        Log.d("ADDFOOD", "File path is " + path);
        foodImagePath = path;
        File foodImage = new File(path);
        Bitmap foodBitmap = BitmapFactory.decodeFile(foodImage.getAbsolutePath());
        foodImageView.setImageBitmap(foodBitmap);
        tagProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCloseA(String title) { // ONE FOR EACH FRAGMENT?
//        foodTitle = title;
        foodTitleTextView.setText(title);

    }

    @Override
    public void onTagsReceived(List<String> tags) {
        Log.d("ADDFOOD", "Tags: " + tags.toString());
        possibleFoodTags = tags;
        forwardButton.setEnabled(true);
        tagProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTitleChanged(String title) {
        foodTitle = title;
    }

    @Override
    public void onCloseB(String description, List<String> tags, int quantity) {
        foodDescription = description;
        foodTags = tags;
        foodQuantity = quantity;
    }

    @Override
    public void onDescriptionChanged(String description) {
        foodDescription = description;
    }

    @Override
    public void onQuantityChanged(int quantity) {
        foodQuantity = quantity;
    }

    private void setButtons(int step){
        if(step == 0) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_CANCELED, null);
                    finish();
                    overridePendingTransition(R.anim.hold, R.anim.exit_down);
                }
            });
            forwardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(foodTitle != null && foodTitle != "") {
                        Fragment fragment = AddFoodMiddleFragment.newInstance(possibleFoodTags);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                .replace(R.id.addFoodFragmentContainer, fragment)
                                .addToBackStack("0")
                                .commit();
                        setButtons(1);
                    }
                    else{
                        Toast.makeText(AddFoodActivity.this, "Enter a title", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else if (step == 1) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSupportFragmentManager().popBackStackImmediate();
                    setButtons(0);
                }
            });
            forwardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(foodDescription != null && foodQuantity != 0) {
                        Fragment fragment = AddFoodEndFragment.newInstance();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                .replace(R.id.addFoodFragmentContainer, fragment)
                                .addToBackStack("1")
                                .commit();
                        setButtons(2);
                    }
                    else{
                        Toast.makeText(AddFoodActivity.this, "Enter description and quantity", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSupportFragmentManager().popBackStackImmediate();
                    setButtons(1);
                    //TODO back doesn't work
                }
            });
            forwardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Fragment fragment = AddFoodEndFragment.newInstance(); //TODO SEND IT LIST OF KEYWORDS FROM ANNOTATE
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
//                            R.anim.enter_left_to_right, R.anim.exit_left_to_right)
//                            .replace(R.id.addFoodFragmentContainer, fragment)
//                            .addToBackStack("2")
//                            .commit();
//                    getFragmentManager().popBackStackImmediate();
                    if(foodLocation != null && foodStartTime != 0 && foodEndTime != 0) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AddFoodActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();

                        int userId = prefs.getInt("CURRENT_USER", 0);
                        Log.d("ADDFOOD", "user id : " + userId);

                        Food newFoodItem = new Food();
                        newFoodItem.setTitle(foodTitle);
                        newFoodItem.setUser_id(userId);
                        newFoodItem.setImage_path(foodImagePath);
                        newFoodItem.setDescription(foodDescription);
                        newFoodItem.setQuantity(foodQuantity);
                        newFoodItem.setLocation(foodLocation.latitude, foodLocation.longitude);
                        newFoodItem.setStartTime(foodStartTime);
                        newFoodItem.setEndTime(foodEndTime);
                        newFoodItem.setPrice(10); //TODO integrate price entry into UI
                        newFoodItem.setTags(foodTags);
                        DatabaseHelper db = new DatabaseHelper(AddFoodActivity.this);
                        long result = db.insertFood(newFoodItem);
                        if (result > 0) {
                            Log.d("ADDFOOD", "Food Item Added");
//                            Toast.makeText(AddFoodActivity.this, "Food Item Added!", Toast.LENGTH_SHORT).show();
                        }
                        setResult(RESULT_OK, null);
//                    Intent returnIntent = new Intent();
//                    setResult(RESULT_OK, returnIntent);
                        Fragment fragment = AddFoodSuccessFragment.newInstance(); //TODO SEND IT LIST OF KEYWORDS FROM ANNOTATE
                        currentFragment = fragment;
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                .replace(R.id.addFoodFragmentContainer, fragment)
                                .addToBackStack("0")
                                .commit();
//                        finish();
                    }
                    else{
                        Toast.makeText(AddFoodActivity.this, "Enter location and times", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


}