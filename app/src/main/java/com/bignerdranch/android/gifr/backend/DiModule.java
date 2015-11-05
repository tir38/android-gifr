package com.bignerdranch.android.gifr.backend;


import android.content.Context;

import com.bignerdranch.android.gifr.controller.BaseFragment;
import com.bignerdranch.android.gifr.controller.MainFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        injects = {
                BaseFragment.class,
                MainFragment.class
        },
        library = true
)
public class DiModule {

    private static final String SLACK_ENDPOINT = "https://slack.com/api/";
    private Context mContext;

    public DiModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return mContext;
    }

    @Provides
    @Singleton
        // TODO make up your mind about constructor or field injection
    Manager providesManager(Context context,
                            SlackService slackService,
                            MessageStore store) {
        return new LiveManager(context, slackService, store);
    }

    @Provides
    @Singleton
    SlackService provideRestAdapter(Class serviceClass) {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(SLACK_ENDPOINT)
                .build();

        return (SlackService) adapter.create(serviceClass);
    }

    @Provides
    @Singleton
    Class provideWebServiceInterface() {
        return SlackService.class;
    }

    @Provides
    @Singleton
    MessageStore providesMessageStore(Context context) {
        return new LiveMessageStore(context);
    }
}
