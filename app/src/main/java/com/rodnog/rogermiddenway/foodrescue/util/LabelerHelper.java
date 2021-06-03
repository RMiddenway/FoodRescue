package com.rodnog.rogermiddenway.foodrescue.util;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.rodnog.rogermiddenway.foodrescue.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class LabelerHelper {
//
//    private final String TAG = "LabelerHelper";
//    List<String> labelsList = new ArrayList<>();
//
//    public LabelerHelper(Context context) {
//        File file = new File("src/app/assets/labels_mobilenet_quant_v1_224.txt");
//        Log.d(TAG, file.toString());
//
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(
//                    new InputStreamReader(context.getAssets().open("labels_mobilenet_quant_v1_224.txt")));
//
//            // do reading, usually loop until end of file reading
//            String line;
//            while ((line = reader.readLine()) != null) {
//                labelsList.add(line.toString());
//            }
//        } catch (IOException e) {
//            //log the exception
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    //log the exception
//                }
//            }
//        }
//        Log.d(TAG, "Length: " + labelsList.size());
//    }
//
//    public void getLabels(Context context, Uri imageUri){
//        ArrayList<String> detectedLabels = new ArrayList<>();
//
//        InputImage image = null;
//        try {
//            image = InputImage.fromFilePath(context, imageUri);
//        } catch (IOException e) {
//            Log.d(TAG, e.toString());
//            e.printStackTrace();
//        }
//
//        LocalModel localModel =
//                new LocalModel.Builder()
//                        .setAssetFilePath("mobilenet_v1_1.0_224_quant.tflite")
//                        .build();
//
//        CustomImageLabelerOptions customImageLabelerOptions =
//                new CustomImageLabelerOptions.Builder(localModel)
//                        .setConfidenceThreshold(0.1f)
//                        .setMaxResultCount(5)
//                        .build();
//        ImageLabeler labeler = ImageLabeling.getClient(customImageLabelerOptions);
//        Log.d(TAG, localModel.toString());
//        labeler.process(image)
//                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
//                    @Override
//                    public void onSuccess(List<ImageLabel> labels) {
//                        Log.d(TAG, "GREAT SUCCESS " + labels.toString());
//
//
//                        for (ImageLabel label : labels) {
//                            String text = label.getText();
//                            float confidence = label.getConfidence();
//                            int index = label.getIndex();
//
//                            detectedLabels.add(labelsList.get(index));
//                        }
//                        Log.d(TAG, detectedLabels.toString());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, e.toString());
//                    }
//                });
//    }
}
