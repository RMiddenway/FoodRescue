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


    EditText bFoodDescriptionEditText;
    EditText bQuantityEditText;
    ChipGroup bTagsChipGroup;
    List<String> bTags;

    public AddFoodMiddleFragment() {
        // Required empty public constructor
    }

    public interface OnAddMiddleListener {
        public void onCloseB(String Description, List<String> tags, int quantity);
        public void onDescriptionChanged(String description);
        public void onQuantityChanged(int quantity);

    }
    // TODO: Rename and change types and number of parameters
    public static AddFoodMiddleFragment newInstance(List<String> tags) {// TODO PASS IMAGE PATH AND TITLE, THEN SET
        AddFoodMiddleFragment fragment = new AddFoodMiddleFragment();
//        Log.d("ADDFOOD", path);
        Bundle args = new Bundle();
        args.putStringArrayList("TAGS", new ArrayList<>(tags));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallback = (OnAddMiddleListener) getActivity();

        if (getArguments() != null) {
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