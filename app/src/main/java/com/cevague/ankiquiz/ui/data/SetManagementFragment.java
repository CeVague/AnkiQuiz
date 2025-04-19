package com.cevague.ankiquiz.ui.data;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.DBHelper;
import com.cevague.ankiquiz.sql.FileModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SetManagementFragment extends Fragment {

    long id_set;

    RecyclerView recyclerView;

    Button btnSelectAll, btnSelectNone, btnDelete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id_set = getArguments().getLong("id_set");
            Log.d("SetManagementFragment", "id_set: " + id_set);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_management, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_files);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        DBHelper db = new DBHelper(getContext());
        ArrayList<FileModel> listFiles = db.getAllFiles(id_set);

        FileRecyclerViewAdapter fileRVA = new FileRecyclerViewAdapter(getContext(), listFiles);
        recyclerView.setAdapter(fileRVA);


        btnSelectAll = view.findViewById(R.id.button_select_all);
        btnSelectNone = view.findViewById(R.id.button_select_nothing);
        btnDelete = view.findViewById(R.id.button_delete);

        btnSelectAll.setOnClickListener(v -> {
            Animation clickAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.clic);
            v.startAnimation(clickAnimation);

            fileRVA.checkAll();
            fileRVA.notifyDataSetChanged();
        });

        btnSelectNone.setOnClickListener(v -> {
            Animation clickAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.clic);
            v.startAnimation(clickAnimation);
            
            fileRVA.checkNone();
            fileRVA.notifyDataSetChanged();
        });

        return view;
    }
}
