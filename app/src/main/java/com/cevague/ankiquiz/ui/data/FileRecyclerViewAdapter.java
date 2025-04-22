package com.cevague.ankiquiz.ui.data;

import static com.cevague.ankiquiz.utils.ImgUtils.scaledImageFromPath;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.utils.AudioPlayer;
import com.google.android.material.chip.Chip;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FileRecyclerViewAdapter extends RecyclerView.Adapter<FileRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<FileModel> listFile;
    ArrayList<Boolean> listChecked;


    public FileRecyclerViewAdapter(Context context, ArrayList<FileModel> listFile) {
        this.context = context;
        this.listFile = listFile;

        this.listChecked = new ArrayList<>();
        for(int i=0;i<listFile.size();i++){
            this.listChecked.add(Boolean.FALSE);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_file, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        FileModel file = listFile.get(position);

        holder.textViewFileName.setText(file.getFileName());

        // Choi de l'image selon le type de fichier
        if(file.getType().equals("jpg")){
            Bitmap image = scaledImageFromPath(file.getAbsolutePath(context), 300, 300);
            holder.imageViewFile.setImageBitmap(image);
        }else if(file.getType().equals("mp3")){
            holder.imageViewFile.setImageResource(R.drawable.ic_launcher_foreground);
        }else if(file.getType().equals("txt")){
            holder.imageViewFile.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Gérer le clic (joue le son si c'est un mp3)
        holder.itemView.setOnClickListener(v -> {
            // Démarrer l'animation sur l'élément cliqué
            Animation clickAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.clic);
            v.startAnimation(clickAnimation);

            if(file.getType().equals("mp3")){
                AudioPlayer.playAudio(context, file.getAbsolutePath(context));
            }
        });

        // Gestion de la checkbox
        holder.checkBox.setOnClickListener(v -> {
            // Récupération dynamique de position
            int pos = holder.getAdapterPosition();
            // modifie la liste en conséquence
            listChecked.set(pos, holder.checkBox.isChecked());
        });

        holder.checkBox.setChecked(listChecked.get(position));
    }

    @Override
    public int getItemCount() {
        return listFile.size();
    }

    // Supprime un fichier et son checked
    public void deleteFile(FileModel file){
        int position = listFile.indexOf(file);

        if(position != -1){
            listFile.remove(position);
            listChecked.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ArrayList<FileModel> getCheckedFiles(){
        ArrayList<FileModel> listCheckedFile = new ArrayList<>();

        for(int i=0;i<listFile.size();i++){
            if(listChecked.get(i)){
                listCheckedFile.add(listFile.get(i));
            }
        }

        return listCheckedFile;
    }

    public void checkAll(){
        for(int i=0;i<listFile.size();i++){
            listChecked.set(i, Boolean.TRUE);
        }
    }

    public void checkNone(){
        for(int i=0;i<listFile.size();i++){
            listChecked.set(i, Boolean.FALSE);
        }
    }

    public boolean isEmpty(){
        return listFile.isEmpty();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFile;
        CheckBox checkBox;
        TextView textViewFileName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFile = itemView.findViewById(R.id.imageView_file);

            checkBox = itemView.findViewById(R.id.checkBox_file);

            textViewFileName = itemView.findViewById(R.id.textView_file_name);
        }
    }
}