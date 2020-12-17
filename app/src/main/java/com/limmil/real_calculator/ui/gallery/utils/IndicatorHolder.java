package com.limmil.real_calculator.ui.gallery.utils;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.limmil.real_calculator.R;

public class IndicatorHolder extends RecyclerView.ViewHolder{
    public ImageView image;
    private CardView card;
    View positionController;

    IndicatorHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.imageIndicator);
        card = itemView.findViewById(R.id.indicatorCard);
        positionController = itemView.findViewById(R.id.activeImage);
    }
}
