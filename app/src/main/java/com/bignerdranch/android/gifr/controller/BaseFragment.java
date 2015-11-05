package com.bignerdranch.android.gifr.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.bignerdranch.android.gifr.GifrApplication;

import de.greenrobot.event.EventBus;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GifrApplication) getActivity().getApplication()).inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (registerForEvents()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registerForEvents()) {
            EventBus.getDefault().unregister(this);
        }
    }

    abstract boolean registerForEvents();
}
