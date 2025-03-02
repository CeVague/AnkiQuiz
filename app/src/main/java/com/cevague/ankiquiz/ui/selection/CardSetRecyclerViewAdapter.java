package com.cevague.ankiquiz.ui.selection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cevague.ankiquiz.R;

import java.util.ArrayList;
import java.util.Collections;

public class CardSetRecyclerViewAdapter  extends RecyclerView.Adapter<CardSetRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> list_card_set;
    ArrayList<Boolean> list_selection;
    CardSetRecyclerViewAdapter.OnItemClickListener listener;

    public ArrayList<Boolean> getList_selection() {
        return list_selection;
    }

    public interface OnItemClickListener {
        void onItemClick(ArrayList<Boolean> list_selection);
    }

    public void setOnItemClickListener(CardSetRecyclerViewAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    public CardSetRecyclerViewAdapter(Context context, ArrayList<String> list_card_set) {
        this.context = context;
        this.list_card_set = list_card_set;

        this.list_selection = new ArrayList<Boolean>();
        for(int i=0;i<list_card_set.size();i++){
            list_selection.add(false);
        }
    }

    @NonNull
    @Override
    public CardSetRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_card_set, parent, false);
        return new CardSetRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardSetRecyclerViewAdapter.MyViewHolder holder, int position) {
        String card_set = list_card_set.get(position);
        boolean is_selected = list_selection.get(position);

        holder.tv_name.setText(card_set);

        holder.cv.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorOnPrimaryVariant));
        holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.colorOnSecondary));

        setActivatedOrNot(holder, list_selection.get(position));

        // Gérer le clic en passant l'élément cliqué au Fragment
        holder.itemView.setOnClickListener(v -> {
            // Démarrer l'animation sur l'élément cliqué
            Animation clickAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.clic);
            v.startAnimation(clickAnimation);

            list_selection.set(position, !list_selection.get(position));

            setActivatedOrNot(holder, list_selection.get(position));

            listener.onItemClick(list_selection);

        });
    }

    private void setActivatedOrNot(CardSetRecyclerViewAdapter.MyViewHolder holder, boolean activated){
        if(activated){
            holder.cv.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryVariant));
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.colorOnPrimary));
        }else{
            holder.cv.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorOnPrimaryVariant));
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.colorOnSecondary));
        }
    }

    @Override
    public int getItemCount() {
        return list_card_set.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        CardView cv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.textView_rv_selection);
            cv = itemView.findViewById(R.id.cardView_rv_selection);
        }
    }
}
