package com.example.sashank.imagelistapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by sashank on 19/6/17.
 */

public class EditDialog extends DialogFragment implements View.OnClickListener {

    Button delete, crop, cancel;
    Communicator communicator;
    int position;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity temp = (Activity) context;
        communicator = (Communicator) temp;

    }

    public static EditDialog newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt("Position",position);

        EditDialog fragment = new EditDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_dialog,null);

        delete = (Button) v.findViewById(R.id.delete_button);
        delete.setOnClickListener(this);

        crop = (Button) v.findViewById(R.id.crop_button);
        crop.setOnClickListener(this);

        cancel = (Button) v.findViewById(R.id.cancel_edit);
        cancel.setOnClickListener(this);

        position = getArguments().getInt("Position");
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_button:
                communicator.sendDeleteMessage(position);
                dismiss();
                break;

            case R.id.cancel_edit:
                dismiss();
                break;

        }
    }
}
