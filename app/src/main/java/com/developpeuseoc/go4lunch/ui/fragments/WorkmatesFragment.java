package com.developpeuseoc.go4lunch.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.adapters.ListRestaurantAdapter;
import com.developpeuseoc.go4lunch.adapters.WorkmatesAdapter;
import com.developpeuseoc.go4lunch.models.User;
import com.developpeuseoc.go4lunch.ui.activities.RestaurantActivity;
import com.developpeuseoc.go4lunch.viewModel.MyViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID_FIELD;

public class WorkmatesFragment extends Fragment implements WorkmatesAdapter.workmateOnClickListener {

    // --- Attribute ---
    private RecyclerView recyclerView;
    private Context context;
    private MyViewModel myViewModel;


    public static WorkmatesFragment newInstance() {
        WorkmatesFragment fragment = new WorkmatesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        recyclerView = view.findViewById(R.id.workmatesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel.class);
        configureRecyclerView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    //
    @Override
    public void workmateOnClick(String placeId, String userName) {
        if (placeId != null) {
            Intent intent = new Intent(context, RestaurantActivity.class);
            intent.putExtra(RESTAURANT_ID_FIELD, placeId);
            startActivity(intent);
        } else {
            Toast.makeText(context, userName + getString(R.string.no_decided), Toast.LENGTH_SHORT).show();
        }
    }


    private void configureRecyclerView() {
        recyclerView.setAdapter(new WorkmatesAdapter(generateOptionsForAdapter(myViewModel.getUsersQuery()
                .orderBy(RESTAURANT_ID_FIELD, Query.Direction.DESCENDING)),
                Glide.with(this), this));
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }
}
