package com.project.real_calculator.interfaces;

import com.project.real_calculator.database.models.PhotoModel;
import com.project.real_calculator.ui.gallery.utils.PicHolder;

import java.util.ArrayList;

public interface IClickListener {
    void onPicClicked(PicHolder holder, int position, ArrayList<PhotoModel> pics);
    void onPicClicked(String pictureFolderPath,String folderName);
}
