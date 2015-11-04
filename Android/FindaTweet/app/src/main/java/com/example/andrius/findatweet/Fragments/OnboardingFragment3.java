package com.example.andrius.findatweet.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.andrius.findatweet.R;
/**
 * Created by Andrius on 2015-10-14.
 */
public class OnboardingFragment3  extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle s) {

        return inflater.inflate(
                R.layout.onboarding_screen3,
                container,
                false
        );

    }
}