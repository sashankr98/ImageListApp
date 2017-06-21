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
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by sashank on 18/6/17.
 */

public class CaptionDialog extends DialogFragment implements View.OnClickListener{

    EditText editText;
    Button confirm,cancel;
    Communicator communicator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity temp = (Activity) context;
        communicator = (Communicator) temp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        setCancelable(false);

        View view = inflater.inflate(R.layout.caption_dialog,null);
        editText = (EditText) view.findViewById(R.id.caption_edit_text);
        confirm = (Button) view.findViewById(R.id.confirm_caption);
        confirm.setOnClickListener(this);
        cancel = (Button) view.findViewById(R.id.cancel_caption);
        cancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.confirm_caption:
                if (!editText.getText().toString().equals(""))
                    communicator.sendCaptionMessage(editText.getText().toString());
                else {
                    communicator.sendCaptionMessage("Caption");
                    Toast.makeText(getActivity(), "Default Caption Set", Toast.LENGTH_SHORT).show();
                }
                dismiss();
                break;

            case R.id.cancel_caption:
                communicator.sendCaptionMessage("Caption");
                dismiss();
                Toast.makeText(getActivity(), "Default Caption Set", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
