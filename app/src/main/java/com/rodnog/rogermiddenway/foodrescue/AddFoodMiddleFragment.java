package com.rodnog.rogermiddenway.foodrescue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFoodMiddleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFoodMiddleFragment extends Fragment {

    OnAddMiddleListener mCallback;

    String bFoodImagePath;
    String bFoodTitle;

    EditText bFoodDescriptionEditText;
    EditText bQuantityEditText;
    ChipGroup bTagsChipGroup;
    List<String> bTags;
    Button bNextButton;
    Button bBackButton;

    public AddFoodMiddleFragment() {
        // Required empty public constructor
    }

    public interface OnAddMiddleListener {
//        public void onMiddleAdded(String description, List<String> tags, int quantity);
        public void onCloseB(String Description, List<String> tags, int quantity);
        public void onDescriptionChanged(String description);
        public void onQuantityChanged(int quantity);

    }
    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFoodMiddleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFoodMiddleFragment newInstance(List<String> tags) {// TODO PASS IMAGE PATH AND TITLE, THEN SET
        AddFoodMiddleFragment fragment = new AddFoodMiddleFragment();
//        Log.d("ADDFOOD", path);
        Bundle args = new Bundle();
//        String[] tagArr = new String[tags.size()];
//        tagArr = tags.toArray(tagArr);
        args.putStringArrayList("TAGS", new ArrayList<>(tags));
//        args.putString("PATH", path);
//        args.putString("TITLE", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallback = (OnAddMiddleListener) getActivity();

        if (getArguments() != null) {
//            Log.d("ADDFOOD", getArguments().getString("PATH"));
            bTags = getArguments().getStringArrayList("TAGS");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_food_middle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//
//        ImageView bFoodImageView = getActivity().findViewById(R.id.bFoodImageView);
//        TextView bFoodTitleTextView = getActivity().findViewById(R.id.bFoodTitleTextView);
//        bTags = new ArrayList<>();


//        File foodImage = new File(bFoodImagePath);
//        Bitmap foodBitmap = BitmapFactory.decodeFile(foodImage.getAbsolutePath());
//        bFoodImageView.setImageBitmap(foodBitmap);
//
//        bFoodTitleTextView.setText(bFoodTitle);

        bFoodDescriptionEditText = getActivity().findViewById(R.id.bDescriptionEditText);
        bQuantityEditText = getActivity().findViewById(R.id.bQuantityEditText);

        bFoodDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onDescriptionChanged(s.toString());
            }
        });

        bQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onQuantityChanged(Integer.parseInt(s.toString()));
            }
        });

        bTagsChipGroup = getActivity().findViewById(R.id.bTagsChipGroup);
        bTagsChipGroup.removeAllViews();

        for(String s : bTags){
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.custom_chip, bTagsChipGroup, false);
            chip.setText(s);
            bTagsChipGroup.addView(chip);
        }
//        bNextButton = getActivity().findViewById(R.id.bNextButton);
//        bBackButton = getActivity().findViewById(R.id.bBackButton);

//        bBackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getFragmentManager().popBackStackImmediate();
//            }
//        });
//
//        bNextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(bFoodDescriptionEditText.getText().toString() != "" && bQuantityEditText.getText().toString() != "") {
//                    onMiddleAdded(bFoodDescriptionEditText.getText().toString(), Arrays.asList("TEST"), Integer.parseInt(bQuantityEditText.getText().toString()));
//                }
//                else{
//                    Toast.makeText(getActivity(), "Enter a description and quantity", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            List<Integer> checkedIds = bTagsChipGroup.getCheckedChipIds();
            ArrayList<String> selectedTags = new ArrayList<>();
            for(Integer i : checkedIds){
                Chip chip = (Chip) getActivity().findViewById(i);
                selectedTags.add(chip.getText().toString());
            }

            onCloseB(bFoodDescriptionEditText.getText().toString(), selectedTags, Integer.parseInt(bQuantityEditText.getText().toString()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
//    public void onMiddleAdded(String description, List<String> tags, int quantity) {
//        if(mCallback != null)
//        {
//            mCallback.onMiddleAdded(description, tags, quantity);
//        }
//    }
    public void onCloseB(String description, List<String> tags, int quantity){
        if(mCallback != null)
        {
            mCallback.onCloseB(description, tags, quantity);
        }
    }
    public void onDescriptionChanged(String description){
        if(mCallback != null)
        {
            mCallback.onDescriptionChanged(description);
        }
    }
    public void onQuantityChanged(int quantity){
        if(mCallback != null)
        {
            mCallback.onQuantityChanged(quantity);
        }
    }
}