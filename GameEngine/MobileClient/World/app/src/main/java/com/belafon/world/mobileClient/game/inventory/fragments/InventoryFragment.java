package com.belafon.world.mobileClient.game.inventory.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.belafon.world.mobileClient.AbstractActivity;
import com.belafon.world.mobileClient.R;
import com.belafon.world.mobileClient.game.Fragments;
import com.belafon.world.mobileClient.game.inventory.Inventory;
import com.belafon.world.mobileClient.game.visibles.items.Item;
import com.belafon.world.mobileClient.game.visibles.items.ItemInfoFragment;

import java.util.ArrayList;
import java.util.List;






public class InventoryFragment extends Fragment implements OnItemClickListener {
    public final int fragmentContainer;
    public final Inventory inventory;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private Fragments fragments;
    public InventoryFragment(Inventory inventory, int fragmentContainer, Fragments fragments) {
        this.inventory = inventory;
        this.fragmentContainer = fragmentContainer;
        this.fragments = fragments;
        for(Item item : inventory.items.values()){
            addItemToInventory(item);
        }
    }

    @Override
    public void onItemClick(Item item) {
        Fragment newFragment = new ItemInfoFragment(this, fragmentContainer, item);

        requireActivity().getSupportFragmentManager().beginTransaction()
            .replace(fragmentContainer, newFragment)
            .addToBackStack(null)
            .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_inventory, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ItemAdapter(new ArrayList<>(inventory.items.values()), this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public void addItemToInventory(Item item) {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            ((ItemAdapter) recyclerView.getAdapter()).addItem(item);
        }
    }

    public void removeItemFromInventory(Item item) {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            ((ItemAdapter) recyclerView.getAdapter()).removeItem(item);
        }

        var activity = getActivity();
        if(activity == null)
            return;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(fragmentContainer);
        if (fragment != null && fragment instanceof ItemInfoFragment itemInfoFragment) {
            itemInfoFragment.goBack();
        }

    }


    private static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

        private List<Item> itemList;
        private OnItemClickListener itemClickListener;
    
        public ItemAdapter(List<Item> itemList, OnItemClickListener listener) {
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
            Item item = itemList.get(position);
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
               
            public void bind(Item item, OnItemClickListener listener) {
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

        public void addItem(Item item) {
            itemList.add(item);
            notifyItemInserted(itemList.size() - 1);
        }
    
        public void removeItem(Item item) {
            int position = itemList.indexOf(item);
            if (position != -1) {
                itemList.remove(position);
                notifyItemRemoved(position);
            }
        }


    }
}