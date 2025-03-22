package com.cevague.ankiquiz.ui.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.CardModel;
import com.cevague.ankiquiz.ui.selection.CardSetRecyclerViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CardRecyclerViewAdapter  extends RecyclerView.Adapter<CardRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<CardModel> cardList;

    public CardRecyclerViewAdapter(Context context, ArrayList<CardModel> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_card_end_game, parent, false);
        return new CardRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardRecyclerViewAdapter.MyViewHolder holder, int position) {
        CardModel card = cardList.get(position);

        holder.tvName.setText(card.getInfo().getName());
        holder.tvDesc.setText(card.getInfo().getDescription());

        Date date = card.getNext_time();
        String dateString = (String) android.text.format.DateFormat.format("dd/MM", date);

        Date today = new Date();
        String todayString = (String) android.text.format.DateFormat.format("dd/MM", today);

        boolean lose = todayString.equals(dateString);
        holder.tvWin.setText(lose ? "Echec" : "Réussite");

        if(lose){
            holder.tvWin.setTextColor(ContextCompat.getColor(context, R.color.colorError));
        }else{
            holder.tvWin.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryVariant));
        }

        holder.tvNextTime.setText(dateString);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvWin, tvNextTime;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.textView_name_end_game);
            tvDesc = itemView.findViewById(R.id.textView_description_end_game);
            tvWin = itemView.findViewById(R.id.textView_reussite_end_game);
            tvNextTime = itemView.findViewById(R.id.textView_next_time_end_game);

            cardView = itemView.findViewById(R.id.cardView_end_game);
        }
    }
}
