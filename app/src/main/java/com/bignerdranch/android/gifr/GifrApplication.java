package com.bignerdranch.android.gifr;

import android.app.Application;

import com.bignerdranch.android.gifr.backend.DiModule;

import dagger.ObjectGraph;

public class GifrApplication extends Application {

    protected ObjectGraph mApplicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationGraph = createObjectGraph();
    }

    protected ObjectGraph createObjectGraph() {
        return ObjectGraph.create(new DiModule(this));
    }

    public final void inject(Object object) {
        mApplicationGraph.inject(object);
    }

}
