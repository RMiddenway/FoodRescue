package com.rodnog.rogermiddenway.foodrescue;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFoodSuccessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFoodSuccessFragment extends Fragment {


    public AddFoodSuccessFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddFoodSuccessFragment newInstance() {
        AddFoodSuccessFragment fragment = new AddFoodSuccessFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_food_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView helicopter = getActivity().findViewById(R.id.sHelicopterIcon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Path path1 = new Path();
            path1.arcTo(-500, 0, 1500, 500, 0, 180, true);
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(helicopter, View.X, View.Y, path1);
            anim1.setInterpolator(new LinearInterpolator());
            anim1.setDuration(2000);
            anim1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Activity activity = getActivity();
                    activity.finish();
                    activity.overridePendingTransition(R.anim.enter_down, R.anim.exit_down);
                }
            });
            anim1.start();
        }

    }

}