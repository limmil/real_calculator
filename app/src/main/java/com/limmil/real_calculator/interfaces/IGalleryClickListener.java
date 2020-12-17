package com.limmil.real_calculator.interfaces;

import android.view.View;

import com.limmil.real_calculator.database.models.AlbumModel;
import com.limmil.real_calculator.database.models.PhotoModel;
import com.limmil.real_calculator.ui.gallery.utils.PicHolder;

import java.util.List;

public interface IGalleryClickListener {
    void onPicClicked(PicHolder holder, int position, List<PhotoModel> pics);
    void onPicClicked(AlbumModel album);
    void onPicHeld(AlbumModel album, View view, int position);
    void onPicHeld(PhotoModel photo, View view, int position);
}
