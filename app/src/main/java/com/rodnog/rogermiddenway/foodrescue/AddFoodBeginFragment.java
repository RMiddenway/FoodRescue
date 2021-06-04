package com.rodnog.rogermiddenway.foodrescue;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFoodBeginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFoodBeginFragment extends Fragment {
    private FirebaseFunctions mFunctions;
    OnAddBeginListener mCallback;

    EditText titleEditText;
    Button addImageButton;
    String imagePath;

    public AddFoodBeginFragment() {
        // Required empty public constructor
    }


    public interface OnAddBeginListener {
        public void onPictureAdded(String path);
        public void onCloseA(String title);
        public void onTagsReceived(List<String> tags);
        public void onTitleChanged(String title);
    }

    // TODO: Rename and change types and number of parameters
    public static AddFoodBeginFragment newInstance() {
        AddFoodBeginFragment fragment = new AddFoodBeginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallback = (OnAddBeginListener) getActivity();
        if (getArguments() != null) {
        }
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                float aspectRatio = selectedImage.getWidth() /
                        (float) selectedImage.getHeight();
                int height = 480;
                int width = Math.round(height * aspectRatio);

                Bitmap scaledImage = Bitmap.createScaledBitmap(
                        selectedImage, width, height, false);

                imagePath = saveImage(getActivity(), scaledImage);

                onPictureAdded(imagePath);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
//            Toast.makeText(AddFoodActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_food_begin, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        aFoodImageView = getView().findViewById(R.id.aFoodImageView);
        titleEditText = getView().findViewById(R.id.aTitleEditText);
        addImageButton = getView().findViewById(R.id.aAddImageButton);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                            },
                            101);
                } else {
//                    Toast.makeText(getActivity().this, "Permission already granted", Toast.LENGTH_SHORT).show();
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 2);
                }
            }
        });
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString() != ""){
                    onTitleChanged(s.toString());

                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onCloseA(titleEditText.getText().toString());
    }

    public void onPictureAdded(String path) {
        if(mCallback != null)
        {
            mCallback.onPictureAdded(path);
        }
    }

    public void onCloseA(String title){
        if(mCallback != null)
        {
            mCallback.onCloseA(title);
        }
    }
    public void onTagsReceived(List<String> tags){
        if(mCallback != null)
        {
            mCallback.onTagsReceived(tags);
        }
    }

    public void onTitleChanged(String title){
        if(mCallback != null)
        {
            mCallback.onTitleChanged(title);
        }
    }

    private String saveImage(Context context, Bitmap image) {

        getAnnotations(image);

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
//            Toast.makeText(AddFoodActivity.this, String.valueOf(success), Toast.LENGTH_LONG).show();
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
        }
        return savedImagePath;
    }

    private void getAnnotations(Bitmap bitmap){

        List<String> result = new ArrayList<>();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        mFunctions = FirebaseFunctions.getInstance();
        // Create json request to cloud vision
        JsonObject request = new JsonObject();
// Add image to request
        JsonObject image = new JsonObject();
        image.add("content", new JsonPrimitive(base64encoded));
        request.add("image", image);
//Add features to the request
        JsonObject feature = new JsonObject();
        feature.add("maxResults", new JsonPrimitive(10));
        feature.add("type", new JsonPrimitive("LABEL_DETECTION"));
        JsonArray features = new JsonArray();
        features.add(feature);
        request.add("features", features);

        annotateImage(request.toString())
                .addOnCompleteListener(new OnCompleteListener<JsonElement>() {
                    @Override
                    public void onComplete(@NonNull Task<JsonElement> task) {
                        if (!task.isSuccessful()) {
                            Log.d("MAIN", task.getException().toString());
                            // Task failed with an exception
                            // ...
                        } else {
                            Log.d("MAIN", "SUCCESS: " + task.getResult().toString());
                            for (JsonElement label : task.getResult().getAsJsonArray().get(0).getAsJsonObject().get("labelAnnotations").getAsJsonArray()) {
                                JsonObject labelObj = label.getAsJsonObject();
                                String text = labelObj.get("description").getAsString();
                                String entityId = labelObj.get("mid").getAsString();
                                float score = labelObj.get("score").getAsFloat();
                                Log.d("ADDFOOD", text);
                                String[] uselessLabels = new String[]{"Food", "Recipe", "Ingredient", "Tableware", "Cuisine"};
                                if(!Arrays.stream(uselessLabels).anyMatch(text::equals)){
                                    result.add(text);
                                }


                            }
                            onTagsReceived(result);
                        }
                    }
                });
    }

    private Task<JsonElement> annotateImage(String requestJson) {
        return mFunctions
                .getHttpsCallable("annotateImage")
                .call(requestJson)
                .continueWith(new Continuation<HttpsCallableResult, JsonElement>() {
                    @Override
                    public JsonElement then(@NonNull Task<HttpsCallableResult> task) {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return JsonParser.parseString(new Gson().toJson(task.getResult().getData()));
                    }
                });
    }
}