package com.project.real_calculator.interfaces;

import android.view.View;

import com.project.real_calculator.database.models.AlbumModel;
import com.project.real_calculator.database.models.PhotoModel;
import com.project.real_calculator.ui.gallery.utils.PicHolder;

import java.util.ArrayList;

public interface IClickListener {
    void onPicClicked(PicHolder holder, int position, ArrayList<PhotoModel> pics);
    void onPicClicked(String pictureAlbumPath,String albumName);
    void onPicHeld(AlbumModel album, View view, int position);
}
