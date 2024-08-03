package com.cholera.eagleeye.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cholera.eagleeye.R;

import java.util.List;

public class GovernorateDetailsDialog extends DialogFragment {

    private List<Notifications.GovernoratePrediction> predictions;

    public GovernorateDetailsDialog(List<Notifications.GovernoratePrediction> predictions) {
        this.predictions = predictions;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create and return a Dialog
        return new Dialog(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_governorate_details, container, false);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ListView listView = view.findViewById(R.id.governorate_list);

        ArrayAdapter<Notifications.GovernoratePrediction> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                predictions
        );
        listView.setAdapter(adapter);

//        // Set up close button
//        ImageView closeButton = view.findViewById(R.id.close_button);
//        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }
}
