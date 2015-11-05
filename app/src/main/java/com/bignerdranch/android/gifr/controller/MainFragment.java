package com.bignerdranch.android.gifr.controller;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.android.gifr.R;
import com.bignerdranch.android.gifr.backend.Manager;
import com.bignerdranch.android.gifr.event.MessagesUpdatedEvent;
import com.bignerdranch.android.gifr.model.GifMessage;
import com.bignerdranch.android.gifr.utils.DateUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.joda.time.DateTime;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

public class MainFragment extends BaseFragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    @Inject
    protected Manager mManager;

    private RecyclerView mRecyclerView;

    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mManager.loadMessages();

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_main_recyclerview);

        // get whatever manager initially has to give us
        MessageAdapter adapter = new MessageAdapter(mManager.getMessages());
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_update:
                mManager.loadMessages();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onEvent(MessagesUpdatedEvent event) {
        List<GifMessage> messages = mManager.getMessages();
        MessageAdapter adapter = new MessageAdapter(messages);
        mRecyclerView.setAdapter(adapter);
    }


    private class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

        private List<GifMessage> mMessages;

        public MessageAdapter(List<GifMessage> messages) {
            mMessages = messages;
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout placeHolder = new LinearLayout(getActivity());
            placeHolder.setLayoutParams(params);
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.view_message, placeHolder);
            return new MessageViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            GifMessage gifMessage = mMessages.get(position);
            holder.bindMessage(gifMessage);
        }

        @Override
        public int getItemCount() {
            if (mMessages == null) {
                return 0;
            }

            return mMessages.size();
        }
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView mDateTextView;
        private final TextView mUsernameTextView;
        private final ImageView mGifImageView;
        private final Drawable mOfflineIcon;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mDateTextView = (TextView) itemView.findViewById(R.id.view_message_date);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.view_message_username);
            mGifImageView = (ImageView) itemView.findViewById(R.id.view_message_image);

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mOfflineIcon = getResources().getDrawable(R.drawable.ic_offline_pin_black_24dp, null);
            } else {
                mOfflineIcon = getResources().getDrawable(R.drawable.ic_offline_pin_black_24dp);
            }
        }

        public void bindMessage(GifMessage message) {

            DateTime messageDate = message.getDateTime();
            if (messageDate == null) {
                mDateTextView.setText("");
            } else if (DateUtils.isToday(messageDate)) {
                mDateTextView.setText("Today");
            } else if (DateUtils.isYesterday(messageDate))  {
                mDateTextView.setText("Yesterday");
            } else {
                String string = DateUtils.beautify(messageDate);
                mDateTextView.setText(string);
            }

            String displayUsername = message.getDisplayUsername();
            if (TextUtils.isEmpty(displayUsername)) {
                mUsernameTextView.setText(message.getUsername());
                mManager.loadUsername(message);
            } else {
                mUsernameTextView.setText(displayUsername);
            }

            String fileName = message.getLocalFileName();

            if (!TextUtils.isEmpty(fileName)) {
                // load local file
                File file = new File(fileName);

                mUsernameTextView.setCompoundDrawablesWithIntrinsicBounds(mOfflineIcon, null, null, null);

                Log.d(TAG, "displaying local file:" + file.getAbsolutePath());
                Glide.with(MainFragment.this)
                        .load(file)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // we do all our disk caching ourselves
                        .into(mGifImageView);

            } else {
                // download
                mUsernameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

                Log.d(TAG, "displaying remote file:" + message.getURL().toString());
                Glide.with(MainFragment.this)
                        .load(message.getURL().toString())
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // we do all our disk caching ourselves
                        .into(mGifImageView);
            }
        }
    }

    @Override
    boolean registerForEvents() {
        return true;
    }
}
