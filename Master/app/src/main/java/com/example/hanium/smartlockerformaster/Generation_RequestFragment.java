package com.example.hanium.smartlockerformaster;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static java.lang.System.in;

public class Generation_RequestFragment extends Fragment {

    private String TAG = Generation_RequestFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView in Generation_RequestFragment");

        return inflater.inflate(R.layout.fragment_generation_request, container, false);


    }


}
