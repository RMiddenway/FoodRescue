package com.rodnog.rogermiddenway.foodrescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rodnog.rogermiddenway.foodrescue.data.DatabaseHelper;
import com.rodnog.rogermiddenway.foodrescue.model.Food;
import com.rodnog.rogermiddenway.foodrescue.util.PaymentsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CartActivity extends AppCompatActivity implements CartItemAdapter.OnRowClickListener{

    View googlePayButton;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private PaymentsClient paymentsClient;
    private List<Food> cartItemList;


    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        SharedPreferences prefs = getSharedPreferences("SHAREPREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String cartString = prefs.getString("CART", "0");
        Gson gson = new Gson();
        cartItemList = new ArrayList<Food>();
        DatabaseHelper db = new DatabaseHelper(this);

        if(cartString == "0"){
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();

        } else {
            Type collectionType = new TypeToken<Collection<Integer>>(){}.getType();
            Collection<Integer> cartIds = gson.fromJson(cartString, collectionType);
            HashSet<Integer> cartIdSet =new HashSet<Integer>(cartIds);

            for (Integer i : cartIdSet) {
                Log.d("CART", "Fetching food id " + i);
                Food food = db.fetchFood(i);
                int count = Collections.frequency(cartIds, i);
                food.setQuantity(count);
                cartItemList.add(food);
                total += food.getPrice() * count;
            }
        }

        RecyclerView cartItemRecyclerView = findViewById(R.id.cartItemRecyclerView);
        CartItemAdapter cartItemAdapter = new CartItemAdapter(cartItemList, CartActivity.this, this);
        cartItemRecyclerView.setAdapter(cartItemAdapter);
        RecyclerView.LayoutManager foodLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(getDrawable(R.drawable.recyclerview_divider));

        cartItemRecyclerView.addItemDecoration(itemDecorator);

        cartItemRecyclerView.setLayoutManager(foodLayoutManager);

        TextView totalTextVIew = findViewById(R.id.kTotalTextView);
        totalTextVIew.setText("$" + formatDecimal(total));

        paymentsClient = PaymentsUtil.createPaymentsClient(this);
        googlePayButton = findViewById(R.id.kGooglePayButton);
        googlePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPayment(v);
            }
        });
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
        long foodItemPriceCents = Math.round(total);

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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
    }
    public String formatDecimal(float number) {
        float epsilon = 0.004f; // 4 tenths of a cent
        if (Math.abs(Math.round(number) - number) < epsilon) {
            return String.format("%10.0f", number); // sdb
        } else {
            return String.format("%10.2f", number); // dj_segfault
        }
    }

    @Override
    public void onRemoveItem(int position) {
        DatabaseHelper db = new DatabaseHelper(this);
        Food foodRemoved = cartItemList.get(position);
        Food foodDB = db.fetchFood(foodRemoved.getFood_id());
        foodDB.setQuantity(foodDB.getQuantity() + foodRemoved.getQuantity());

        db.updateFood(foodDB);
        cartItemList.remove(position);

        SharedPreferences prefs = getSharedPreferences("SHAREPREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String cartString = prefs.getString("CART", "0");

        Type collectionType = new TypeToken<Collection<Integer>>() {
        }.getType();
        Collection<Integer> cartIds = gson.fromJson(cartString, collectionType);
        for (int i = 0; i < foodRemoved.getQuantity(); i++) {
            cartIds.remove(foodRemoved.getFood_id());
        }
//        Toast.makeText(FoodViewActivity.this, quantity + "items added to cart", Toast.LENGTH_SHORT).show();
        String json = gson.toJson(cartIds);
        editor.putString("CART", json);
        editor.apply();

        finish();
        overridePendingTransition(R.anim.enter_up, R.anim.exit_up);
        startActivity(getIntent());
    }
}