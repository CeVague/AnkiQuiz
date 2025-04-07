package com.cevague.ankiquiz.ui.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.cevague.ankiquiz.R;

public class CustomButton extends LinearLayout {

    private ImageView imageView;
    private TextView textView;
    private CardView cardView;

    private int type = 0; // 1 si image, -1 si texte

    public CustomButton(Context context) {
        super(context);
        init(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Inflater pour charger le fichier XML de layout
        LayoutInflater.from(context).inflate(R.layout.custom_button_layout, this, true);

        // Récupérer les vues à partir du layout inflaté
        imageView = findViewById(R.id.custom_image);
        textView = findViewById(R.id.custom_text);

        cardView = findViewById(R.id.custom_card);
    }

    // Méthodes pour personnaliser l'image et le texte
    public void setImageResource(int resId) {
        imageView.setImageResource(resId);

        textView.setVisibility(GONE);
        cardView.setVisibility(GONE);

        type = 1;
    }
    public void setImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);

        textView.setVisibility(GONE);
        cardView.setVisibility(GONE);

        type = 1;
    }

    public void setText(String text) {
        textView.setText(text);

        imageView.setVisibility(GONE);

        type = -1;
    }

    @Override
    public void setBackgroundColor(int color) {
        if(type == 1){
            super.setBackgroundColor(color);
        }else if(type == -1){
            cardView.setCardBackgroundColor(color);
        }else{
            super.setBackgroundColor(color);
        }
    }
}
