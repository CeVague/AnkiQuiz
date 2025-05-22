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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.DBHelper;
import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.utils.AudioPlayer;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SetManagementFragment extends Fragment {

    long id_info;

    RecyclerView recyclerView;

    Button btnSelectAll, btnSelectNone, btnDelete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id_info = getArguments().getLong("id_info");
            Log.d("SetManagementFragment", "id_info: " + id_info);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_management, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_files);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        ArrayList<FileModel> listFiles;
        try (DBHelper db = new DBHelper(getContext())) {
            listFiles = db.getAllFiles(id_info);
        }

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

        // Quand on appuie sur supprimer
        btnDelete.setOnClickListener(v -> {
            // On coupe l'audio
            AudioPlayer.stopAudio();

            // On prend la liste des éléments a supprimer
            ArrayList<FileModel> listCheckedFiles = fileRVA.getCheckedFiles();

            try (DBHelper db = new DBHelper(getContext())) {
                // On supprime chaque fichier de la DB et de la liste visible
                for (FileModel file : listCheckedFiles) {
                    db.deleteFile(file);
                    fileRVA.deleteFile(file);
                }
                // Si la liste est vide on supprime Card et Info lié et on ferme le fragment
                if(fileRVA.isEmpty()){
                    db.deleteInfo(id_info);
                    db.deleteCardFromInfo(id_info);

                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            }

        });

        return view;
    }

    @Override
    public void onStop(){
        AudioPlayer.stopAudio();
        super.onStop();
    }
}
