package com.developpeuseoc.go4lunch.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.developpeuseoc.go4lunch.R;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.HttpException;

public class BaseFragment extends Fragment {


    // --- CONSTRUCTOR ---
    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getView();
    }


    // --- UTILS ---

    protected String getTodayDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    protected void handleError(final Throwable throwable) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (throwable instanceof HttpException) {
                    HttpException httpException = (HttpException) throwable;
                    int statusCode = httpException.code();
                    Log.e("HttpException", "Error code : " + statusCode);
                    Toast.makeText(BaseFragment.this.getContext(), BaseFragment.this.getResources().getString(R.string.http_error_message, statusCode), Toast.LENGTH_SHORT).show();

                } else if (throwable instanceof SocketTimeoutException) {
                    Log.e("SocketTimeoutException", "Timeout from retrofit");
                    Toast.makeText(BaseFragment.this.getContext(), BaseFragment.this.getResources().getString(R.string.timeout_error_message), Toast.LENGTH_SHORT).show();

                } else if (throwable instanceof IOException) {
                    Log.e("IOException", "Error");
                    Toast.makeText(BaseFragment.this.getContext(), BaseFragment.this.getResources().getString(R.string.exception_error_message), Toast.LENGTH_SHORT).show();

                } else {
                    Log.e("Generic handleError", "Error");
                    Toast.makeText(BaseFragment.this.getContext(), BaseFragment.this.getResources().getString(R.string.generic_error_message), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}