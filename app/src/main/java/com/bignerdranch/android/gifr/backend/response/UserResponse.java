package com.bignerdranch.android.gifr.backend.response;

import com.google.gson.annotations.SerializedName;

/**
 * GSON response object from Slack API
 */
public class UserResponse {

    @SerializedName("user")
    protected User mUser;

    public UserResponse() {
    }

    public String getDisplayName() {
        if (mUser != null && mUser.mProfile != null) {
            return mUser.mProfile.name;
        }
        return null;
    }

    public class User {

        @SerializedName("profile")
        protected ProfileResponse mProfile;

        public class ProfileResponse {

            @SerializedName("real_name")
            protected String name;
        }
    }
}
