package com.limmil.real_calculator.interfaces;

import android.view.View;

import com.limmil.real_calculator.database.models.FolderModel;
import com.limmil.real_calculator.database.models.MyFileModel;
import com.limmil.real_calculator.ui.files.MyFileAdapter;

import java.util.List;

public interface IFilesClickListener {
    void onFileClicked(MyFileAdapter.MyFileHolder holder, int position, List<MyFileModel> files);
    void onFolderClicked(FolderModel folder);
    void onFileClicked(MyFileModel myFileModel);
    void onFolderHeld(FolderModel folder, View view, int position);
    void onFileHeld(MyFileModel file, View view, int position);
}
