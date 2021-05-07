package com.rodnog.rogermiddenway.foodrescue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.Locale;

public class AddFoodActivity extends AppCompatActivity {

    DatabaseHelper db;
//    int userId;
    ImageView foodImageView;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        foodImageView = findViewById(R.id.foodImageView);
        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        CalendarView calendarView = findViewById(R.id.calendarView);
        EditText timeEditText = findViewById(R.id.timeEditText);
        EditText quantityEditText = findViewById(R.id.quantityEditText);
        EditText locationEditText = findViewById(R.id.locationEditText);

        Button saveButton = findViewById(R.id.saveButton);
        Button addImageButton = findViewById(R.id.addImageButton);

        db = new DatabaseHelper(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(titleEditText.getText().toString().equals("") || descriptionEditText.getText().toString().equals("") ||
                        timeEditText.getText().toString().equals("") || quantityEditText.getText().toString().equals("")){
                    Toast.makeText(AddFoodActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    String title = titleEditText.getText().toString();
                    String description = descriptionEditText.getText().toString();
                    long date = calendarView.getDate();
                    String timeString = timeEditText.getText().toString();
                    DateFormat formatter = new SimpleDateFormat("hh:mm");
                    Date time = new Date(0);
                    try {
                        time = formatter.parse(timeString);
                    }
                    catch(ParseException e){
                        Toast.makeText(AddFoodActivity.this, "Incorrect time format", Toast.LENGTH_SHORT).show();
                    }
                    int quantity = Integer.parseInt(quantityEditText.getText().toString());



                    Food newFoodItem = new Food();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(AddFoodActivity.this);
                    int user_id = sharedPref.getInt("CURRENT_USER", 0);
                    newFoodItem.setUser_id(user_id);
                    newFoodItem.setImage_path(imagePath);
                    newFoodItem.setTitle(title);
                    newFoodItem.setDescription(description);
                    newFoodItem.setDate(date);
                    newFoodItem.setTime(time);
                    newFoodItem.setQuantity(quantity);
                    newFoodItem.setLocation(locationEditText.getText().toString());

                    long result = db.insertFood(newFoodItem);
                    setResult(RESULT_OK, null);
                    finish();
                }
            }
        });
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AddFoodActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                            AddFoodActivity.this,
                            new String[]{
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                            },
                            101);
                } else {
                    Toast.makeText(AddFoodActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 2);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imagePath = saveImage(this, selectedImage);

                File test = new File(imagePath);
                Bitmap myBitmap = BitmapFactory.decodeFile(test.getAbsolutePath());

                foodImageView.setImageBitmap(myBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(AddFoodActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(AddFoodActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
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
            String savedMessage = "Saved";
            Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show();
        }

        return savedImagePath;
    }
}