package com.developpeuseoc.go4lunch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesViewHolder> {

    private RequestManager glide;

    //Create constructor
    public WorkmatesAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    //Create ViewHolder
    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workmates_item, parent, false);
        return new WorkmatesViewHolder(view);
    }

    //Update viewHolder
    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder workmatesViewHolder, int position, @NonNull User model) {
        workmatesViewHolder.updateWithDetails(model, this.glide);
    }

}
