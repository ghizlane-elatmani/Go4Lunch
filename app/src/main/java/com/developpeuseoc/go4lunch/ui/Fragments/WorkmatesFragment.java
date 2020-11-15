package com.developpeuseoc.go4lunch.ui.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.ui.Adapters.WorkmatesAdapter;
import com.developpeuseoc.go4lunch.Models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import io.reactivex.disposables.Disposable;

public class WorkmatesFragment extends BaseFragment {

    private RecyclerView recyclerView;

    private Disposable mDisposable;
    private WorkmatesAdapter workmatesAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionUsers = db.collection("users");

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        recyclerView = view.findViewById(R.id.workmatesRecyclerView);

        setUpRecyclerView();
        return view;
    }

    // For change title action for only this fragment
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActionBar().setTitle(R.string.available);
    }

    // Configure RecyclerView, Adapter, LayoutManager & glue it
    private void setUpRecyclerView() {
        Query query = collectionUsers.orderBy("placeId", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        this.workmatesAdapter = new WorkmatesAdapter(options, Glide.with(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onStart() {
        super.onStart();
        workmatesAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        workmatesAdapter.stopListening();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * dispose subscription
     */
    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }
}

