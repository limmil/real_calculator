package com.project.real_calculator.ui.gallery.utils;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.project.real_calculator.R;

public class PicHolder extends RecyclerView.ViewHolder{
    public ImageView picture;
    public CheckBox checkBox;

    PicHolder(@NonNull View itemView) {
        super(itemView);

        picture = itemView.findViewById(R.id.image);
        checkBox = itemView.findViewById(R.id.imageCheckBox);
    }
}
