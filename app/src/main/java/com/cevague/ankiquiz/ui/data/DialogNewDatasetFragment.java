package com.cevague.ankiquiz.ui.data;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cevague.ankiquiz.R;

public class DialogNewDatasetFragment extends DialogFragment {

    Button btn_cancel, btn_add;
    EditText edt_name;
    String btnAddText;

    public interface TextInputListener {
        void onTextEntered(String text);
    }

    private TextInputListener listener;

    public void setTextInputListener(TextInputListener listener) {
        this.listener = listener;
    }

    public void setBtnAddText(String btnAddText){
        this.btnAddText = btnAddText;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());


        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_new_dataset, null);

        btn_cancel = view.findViewById(R.id.button_cancel_add);
        btn_add = view.findViewById(R.id.button_add_add);
        edt_name = view.findViewById(R.id.textView_add);

        edt_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(btn_add.isEnabled()){
                    btn_add.callOnClick();
                }else{
                    btn_cancel.callOnClick();
                }
                return true;
            }
        });

        edt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edt_name.getText().toString().isBlank()){
                    btn_add.setEnabled(false);
                }else{
                    btn_add.setEnabled(true);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTextEntered(edt_name.getText().toString());
                }
                getDialog().dismiss();
            }
        });

        btn_add.setText(btnAddText);

        builder.setView(view);

        return builder.create();
    }
}