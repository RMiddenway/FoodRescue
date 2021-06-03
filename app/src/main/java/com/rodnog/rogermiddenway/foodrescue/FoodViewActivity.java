package com.rodnog.rogermiddenway.foodrescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rodnog.rogermiddenway.foodrescue.data.DatabaseHelper;
import com.rodnog.rogermiddenway.foodrescue.model.Food;
import com.rodnog.rogermiddenway.foodrescue.util.PaymentsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class FoodViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    LatLng location;
    String title;
    Food foodItem;
    EditText foodQuantityEditText;
    TextView foodRemainingTextView;
    List<String> foodItemTags;
    Button addToCartButton;
    View googlePayButton;
    int foodQuantityRemaining;


    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private PaymentsClient paymentsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_view);

        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        paymentsClient = PaymentsUtil.createPaymentsClient(this);

        ImageView foodImageView = findViewById(R.id.vFoodImageView);
        TextView foodTitleTextView = findViewById(R.id.vFoodTitleTextView);
        TextView foodDescriptionTextView = findViewById(R.id.vFoodDescriptionTextView);
        TextView foodStartTimeTextView = findViewById(R.id.vFoodStartTimeTextView);
        TextView foodEndTimeTextView = findViewById(R.id.vFoodEndTimeTextView);
        ChipGroup foodTagsChipGroup = findViewById(R.id.vTagChipGroup);

        foodRemainingTextView = findViewById(R.id.vQuantityRemainingTextView);

        foodQuantityEditText = findViewById(R.id.vFoodQuantityEditText);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.vFoodMapView);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Toast.makeText(FoodViewActivity.this, "Invalid Food Id", Toast.LENGTH_SHORT).show();
        } else {
            int foodId = extras.getInt("FOOD_ID");
            DatabaseHelper db = new DatabaseHelper(this);
            Food food = db.fetchFood(foodId);
            foodItem = food;
            Log.d("FOOD", food.getTitle());
            String path = food.getImage_path();
            if(path != null) {
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    foodImageView.setImageBitmap(myBitmap);
                } else {
                    foodImageView.setImageResource(R.drawable.question_mark);
                }
            }
            else{
                foodImageView.setImageResource(R.drawable.question_mark);
            }

            foodTitleTextView.setText(food.getTitle());
            foodDescriptionTextView.setText(food.getDescription());
            foodItemTags = food.getTags();
            foodQuantityRemaining = food.getQuantity();
            foodQuantityEditText.setText(String.valueOf(1));
            foodRemainingTextView.setText(String.valueOf("(" + String.valueOf(food.getQuantity()) + " remaining)"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm");


            foodStartTimeTextView.setText(sdf.format(food.getStartTime()));
            foodEndTimeTextView.setText(sdf.format(food.getEndTime()));

            location = food.getLocation();
            title = food.getTitle();
            mapFragment.getMapAsync(this);
            foodTagsChipGroup.removeAllViews();

            for(String s : foodItemTags){
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.custom_chip, foodTagsChipGroup, false);
                chip.setText(s);
                foodTagsChipGroup.addView(chip);
            }

        }


        addToCartButton = findViewById(R.id.vAddToCartButton);
        if(foodQuantityRemaining < 1){
            addToCartButton.setEnabled(false);
        }
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int quantity = Integer.parseInt(foodQuantityEditText.getText().toString());
                if(foodQuantityRemaining >= quantity) {
                    // Updates quantity of this item in database
                    updateQuantity(quantity);


                    Gson gson = new Gson();
                    String foodJson = gson.toJson(foodItem);

                    SharedPreferences prefs = getSharedPreferences("SHAREPREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    String cartString = prefs.getString("CART", "0");


                    if (cartString == "0") {
                        List<Integer> cartIds = new ArrayList<>();
                        for (int i = 0; i < quantity; i++) {
                            cartIds.add(foodItem.getFood_id());
                        }

                        String json = gson.toJson(cartIds);
                        editor.putString("CART", json);
                        editor.apply();
                        Log.d("FOODVIEW", "Cart length is " + String.valueOf(cartIds.size()));
                    } else {
                        Type collectionType = new TypeToken<Collection<Integer>>() {
                        }.getType();
                        Collection<Integer> cartIds = gson.fromJson(cartString, collectionType);
                        for (int i = 0; i < quantity; i++) {
                            cartIds.add(foodItem.getFood_id());
                        }
                        Toast.makeText(FoodViewActivity.this, quantity + "items added to cart", Toast.LENGTH_SHORT).show();
                        String json = gson.toJson(cartIds);
                        editor.putString("CART", json);
                        editor.apply();
                        Log.d("FOODVIEW", "Cart length is " + String.valueOf(cartIds.size()));
                    }
                }
                else{
                    Toast.makeText(FoodViewActivity.this, "Not enough items remaining", Toast.LENGTH_SHORT).show();
                }
                updateButtons();
            }
        });

        googlePayButton = findViewById(R.id.vGooglePayButton);
        if(foodQuantityRemaining < 1){
            googlePayButton.setEnabled(false);
        }
        googlePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int quantity = Integer.parseInt(foodQuantityEditText.getText().toString());
                if(foodQuantityRemaining >= quantity) {
                    requestPayment(v);
                }
                else{
                    Toast.makeText(FoodViewActivity.this, "Not enough items remaining", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("FOODVOEW", "Map ready");
        map = googleMap;

        map.addMarker(new MarkerOptions().position(location).title(title));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                }

                // Re-enables the Google Pay payment button.
                googlePayButton.setClickable(true);
        }
    }

    private void possiblyShowGooglePayButton() {

        final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            setGooglePayAvailable(task.getResult());
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }

    private void setGooglePayAvailable(boolean available) {
        if (available) {
            googlePayButton.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, R.string.googlepay_status_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            return;
        }

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String token = tokenizationData.getString("token");
            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            Toast.makeText(
                    this, R.string.payments_show_name + billingName,
                    Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d("Google Pay token: ", token);

            //SEND/SHOW ORDER CONFIRMATION

            updateQuantity(Integer.parseInt(foodQuantityEditText.getText().toString()));
            updateButtons();

        } catch (JSONException e) {
            throw new RuntimeException("Payment failed");
        }
    }
    private void handleError(int statusCode) {
        Log.e("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }
    public void requestPayment(View view) {

        // Disables the button to prevent multiple clicks.
        googlePayButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
//        try {
        double foodItemPrice = foodItem.getPrice();
        int quantity = Integer.parseInt(foodQuantityEditText.getText().toString());
        long foodItemPriceCents = Math.round(quantity * foodItemPrice * PaymentsUtil.CENTS_IN_A_UNIT.longValue());
//            long priceCents = foodItemPriceCents + SHIPPING_COST_CENTS;

        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(foodItemPriceCents);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }

        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }

    }
    public void updateQuantity(int removed){
        foodItem.setQuantity(foodItem.getQuantity() - Integer.parseInt(foodQuantityEditText.getText().toString()));
        DatabaseHelper db = new DatabaseHelper(FoodViewActivity.this);
        db.updateFood(foodItem);
        foodQuantityRemaining = foodItem.getQuantity();
        foodRemainingTextView.setText(String.valueOf("(" + String.valueOf(foodItem.getQuantity()) + " remaining"));
        Log.d("FOODVIEW", "Updating with " + foodItem.getFood_id());
    }

    private void updateButtons(){
        if(foodQuantityRemaining < 1){
            addToCartButton.setEnabled(false);
            googlePayButton.setEnabled(false);
        }
        else{
            addToCartButton.setEnabled(true);
            googlePayButton.setEnabled(true);
        }
    }
    
}