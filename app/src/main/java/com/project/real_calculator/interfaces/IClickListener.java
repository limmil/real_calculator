package com.project.real_calculator.interfaces;

import android.view.View;

import com.project.real_calculator.database.models.AlbumModel;
import com.project.real_calculator.database.models.PhotoModel;
import com.project.real_calculator.ui.gallery.utils.PicHolder;

import java.util.List;

public interface IClickListener {
    void onPicClicked(PicHolder holder, int position, List<PhotoModel> pics);
    void onPicClicked(AlbumModel album);
    void onPicHeld(AlbumModel album, View view, int position);
}
