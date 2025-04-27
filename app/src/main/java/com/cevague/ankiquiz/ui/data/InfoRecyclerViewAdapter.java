package com.cevague.ankiquiz.ui.data;

import static com.cevague.ankiquiz.utils.ImgUtils.imageFromPath;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.sql.InfoModel;

import java.io.File;
import java.util.ArrayList;

public class InfoRecyclerViewAdapter extends RecyclerView.Adapter<InfoRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<InfoModel> list_info;
    ArrayList<FileModel> list_file;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(InfoModel item);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public InfoRecyclerViewAdapter(Context context, ArrayList<InfoModel> list_info, ArrayList<FileModel> list_file) {
        this.context = context;
        this.list_info = list_info;
        this.list_file = list_file;
    }

    @NonNull
    @Override
    public InfoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row_info, parent, false);
        return new InfoRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoRecyclerViewAdapter.MyViewHolder holder, int position) {
        InfoModel info = list_info.get(position);

        holder.tv_name.setText(info.getName());
        holder.tvw_desc.setText(info.getDescription());

        holder.tv_nb_snd.setText(String.valueOf(countFile(info.getId_i(), "mp3")));
        holder.tv_nb_img.setText(String.valueOf(countFile(info.getId_i(), "jpg")));
        holder.tv_nb_txt.setText(String.valueOf(countFile(info.getId_i(), "txt")));

        File directory = context.getFilesDir(); // Récupère le répertoire privé de l'application
        File imageFile = new File(directory, info.getImg_absolute_path());
        if (imageFile.exists() && imageFile.isFile()) {
            Bitmap bitmap = imageFromPath(imageFile.getAbsolutePath());
            holder.image_view.setImageBitmap(bitmap);
        } else {
            // Image par défaut si le fichier n'existe pas
            holder.image_view.setImageResource(R.drawable.ic_launcher_background);
        }

        // Gérer le clic en passant l'élément cliqué au Fragment
        holder.itemView.setOnClickListener(v -> {
            // Démarrer l'animation sur l'élément cliqué
            Animation clickAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.clic);
            v.startAnimation(clickAnimation);

            if (listener != null) {
                listener.onItemClick(info);
            }
        });
    }

    private int countFile(long id_i, String type){
        int nb = 0;

        for(FileModel file : list_file){
            if(file.getType().equals(type) && (file.getIdI() == id_i)){
                nb++;
            }
        }

        return nb;
    }

    @Override
    public int getItemCount() {
        return list_info.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view;
        TextView tv_nb_img, tv_nb_snd, tv_nb_txt, tv_name, tvw_desc;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image_view = itemView.findViewById(R.id.imageView_img);

            tv_nb_img = itemView.findViewById(R.id.textViewImg);
            tv_nb_snd = itemView.findViewById(R.id.textViewSound);
            tv_nb_txt = itemView.findViewById(R.id.textViewTxt);

            tv_name = itemView.findViewById(R.id.textView_name);
            tvw_desc = itemView.findViewById(R.id.textView_description);
        }
    }
}
