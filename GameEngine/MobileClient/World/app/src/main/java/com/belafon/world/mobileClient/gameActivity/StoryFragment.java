package com.belafon.world.mobileClient.gameActivity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.inventory.fragments.OnItemClickListener;
import com.belafon.world.mobileClient.game.story.Story;
import com.belafon.world.mobileClient.game.story.StoryMessage;
import com.belafon.world.mobileClient.game.story.StoryOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class StoryFragment extends Fragment implements Story.StoryMessageObserver {
    private static final String TAG = "StoryFragment";
    private final Story story;
    private boolean firstLoad = true;
    public volatile boolean disabledChooseOptionsButtons = false;
    public StoryFragment(Story story) {
        this.story = story;

        storyOptionsAdapter = new OptionsItemAdapter(new ArrayList<>(),
                item -> {
                    if(disabledChooseOptionsButtons)
                        return;

                    disabledChooseOptionsButtons = true;

                    Log.d(TAG, "onItemClick: option clicked");
                    story.chooseOption(item.title);
                });
    }
    private final MessagesItemAdapter storyChatAdapter = new MessagesItemAdapter(new ArrayList<>());
    private final OptionsItemAdapter storyOptionsAdapter;

    private void loadStoryHistory(){
        var messageIter = story.storyHistory.iterator();
        while(messageIter.hasNext()){
            var message = messageIter.next();
            showNewMessage(message);
            showOptions(message.options);
        }
        story.storyMessageListener.add(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var rootView = inflater.inflate(R.layout.fragment_story, container, false);
        RecyclerView storyChat = rootView.findViewById(R.id.storyChat);
        storyChat.setLayoutManager(new LinearLayoutManager(getActivity()));
        storyChat.setAdapter(storyChatAdapter);

        RecyclerView storyOptions = rootView.findViewById(R.id.storyOptions);
        storyOptions.setLayoutManager(new LinearLayoutManager(getActivity()));
        storyOptions.setAdapter(storyOptionsAdapter);

        if(firstLoad){
            loadStoryHistory();
            firstLoad = false;
        }

        return rootView;
    }

    public void showNewMessage(StoryMessage message) {
        storyChatAdapter.addItem(message);
    }

    public void showOptions(List<StoryOption> options) {
        storyOptionsAdapter.removeAll();
        for (StoryOption option : options) {
            storyOptionsAdapter.addItem(option);
        }
    }

    @Override
    public void update(StoryMessage storyMessage) {
        showNewMessage(storyMessage);
        showOptions(storyMessage.options);
        disabledChooseOptionsButtons = false;
    }


    private static class MessagesItemAdapter extends RecyclerView.Adapter<MessagesItemAdapter.ItemViewHolder> {

        private final List<StoryMessage> itemList;
        public MessagesItemAdapter(List<StoryMessage> itemList ) {
            this.itemList = itemList;
        }
    
        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_inventory_item, parent, false);
            return new ItemViewHolder(itemView);
        }
    
        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            StoryMessage item = itemList.get(position);
            holder.bind(item);

            final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.itemNameTextView.getLayoutParams();
            params.gravity = GravityCompat.START;
            holder.itemNameTextView.setLayoutParams(params);
        }
    
        @Override
        public int getItemCount() {
            return itemList.size();
        }
    
        public static class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView itemNameTextView;
    
            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                itemNameTextView = itemView.findViewById(R.id.text_view_item);
            }
               
            public void bind(StoryMessage item) {
                itemNameTextView.setText(item.message);
                itemNameTextView.setTextColor(Color.WHITE);
                itemNameTextView.setBackgroundResource(R.drawable.players_position_place_button);
                itemNameTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                itemNameTextView.setPadding(40, 40, 40, 40);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                params.setMargins(0, 0, 0, 20);
                itemView.setLayoutParams(params);
            }
        }

        public void addItem(StoryMessage item) {
            itemList.add(item);
            notifyItemInserted(itemList.size() - 1);
        }
    
        public void removeItem(StoryMessage item) {
            int position = itemList.indexOf(item);
            if (position != -1) {
                itemList.remove(position);
                notifyItemRemoved(position);
            }
        }

    }

    private static class OptionsItemAdapter extends RecyclerView.Adapter<OptionsItemAdapter.ItemViewHolder> {

        private final List<StoryOption> itemList;
        private final OnStoryOptionClickListener itemClickListener;
    
        public OptionsItemAdapter(List<StoryOption> itemList, OnStoryOptionClickListener listener) {
            this.itemList = itemList;
            this.itemClickListener = listener;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_inventory_item, parent, false);
            return new ItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            StoryOption item = itemList.get(position);
            holder.bind(item, itemClickListener);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView itemNameTextView;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                itemNameTextView = itemView.findViewById(R.id.text_view_item);
            }
               
            public void bind(StoryOption item, OnStoryOptionClickListener listener) {
                itemNameTextView.setText(item.toString());
                itemNameTextView.setTextColor(Color.WHITE);
                itemNameTextView.setBackgroundResource(R.drawable.button2);
                itemView.setOnClickListener(view -> {
                    if (listener != null) {
                        listener.onItemClick(item);
                    }
                });
            }
        }

        public void addItem(StoryOption item) {
            itemList.add(item);
            notifyItemInserted(itemList.size() - 1);
        }

        public void removeAll() {
            itemList.clear();
            notifyDataSetChanged();
        }
    }

    public interface OnStoryOptionClickListener {
        void onItemClick(StoryOption item);
    }
}