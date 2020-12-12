package com.project.real_calculator.interfaces;

import android.view.View;

import com.project.real_calculator.database.models.FolderModel;
import com.project.real_calculator.database.models.MyFileModel;
import com.project.real_calculator.ui.files.MyFileAdapter;

import java.util.List;

public interface IFilesClickListener {
    void onPicClicked(MyFileAdapter.MyFileHolder holder, int position, List<MyFileModel> files);
    void onPicClicked(FolderModel folder);
    void onPicClicked(MyFileModel myFileModel);
    void onPicHeld(FolderModel folder, View view, int position);
}
