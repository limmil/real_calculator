package com.project.real_calculator.ui.files;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.real_calculator.R;
import com.project.real_calculator.database.DataBaseHelper;
import com.project.real_calculator.database.models.FolderModel;
import com.project.real_calculator.database.models.FolderModel;
import com.project.real_calculator.database.models.MyFileModel;
import com.project.real_calculator.database.models.MyFileModel;
import com.project.real_calculator.database.models.MyFileModel;
import com.project.real_calculator.interfaces.IFilesClickListener;
import com.project.real_calculator.ui.gallery.utils.MarginDecoration;

import java.util.List;

public class SelectFolderFragment extends Fragment implements IFilesClickListener {

    private Context context;
    private List<FolderModel> allFolders;
    private List<MyFileModel> selectedMyFiles;
    private int currentFolderId;
    List<MyFileModel> allFiles;
    MyFileAdapter fileAdapter;
    RecyclerView folderRecycler;
    FolderAdapter folderAdapter;
    TextView empty;

    public SelectFolderFragment (Context context, List<MyFileModel> selectedMyFiles, MyFileAdapter fileAdapter, List<MyFileModel> allFiles, int currentFolderId){
        this.context = context;
        this.selectedMyFiles = selectedMyFiles;
        this.allFiles = allFiles;
        this.fileAdapter = fileAdapter;
        this.currentFolderId = currentFolderId;
    }

    public static SelectFolderFragment newInstance(Context context, List<MyFileModel> selectedMyFiles, MyFileAdapter fileAdapter, List<MyFileModel> allFiles, int currentFolderId){
        return new SelectFolderFragment(context, selectedMyFiles, fileAdapter, allFiles, currentFolderId);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //init folders list
        final DataBaseHelper db = new DataBaseHelper(getActivity());
        allFolders = db.getFolders();
        // remove current folder from folder list
        FolderModel currentFolder = null;
        for (FolderModel folder : allFolders){
            if (folder.getId()==currentFolderId){
                currentFolder = folder;
            }
        }
        if(currentFolder!=null){allFolders.remove(currentFolder);}

        View root = inflater.inflate(R.layout.fragment_folders, container, false);


        empty = root.findViewById(R.id.folder_empty);
        empty.setText(getString(R.string.no_folders));

        folderRecycler = root.findViewById(R.id.folderRecycler);
        folderRecycler.addItemDecoration(new MarginDecoration(requireActivity()));
        folderRecycler.hasFixedSize();

        FloatingActionButton fab = root.findViewById(R.id.folder_fab);
        fab.setVisibility(View.GONE);

        if(allFolders.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            folderAdapter = new FolderAdapter(allFolders, getActivity(), this);
            folderRecycler.setAdapter(folderAdapter);
        }

        return root;
    }
    
    @Override
    public void onPicClicked(MyFileAdapter.MyFileHolder holder, int position, List<MyFileModel> files) {
        
    }

    @Override
    public void onPicClicked(FolderModel folder) {
        final int id = folder.getId();
        // code in here
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // clicked yes
                    case DialogInterface.BUTTON_POSITIVE:
                        moveFilesToFolder(context, selectedMyFiles, id);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Moving " + selectedMyFiles.size() + " files to " + folder.getFolderName() + "?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onPicClicked(MyFileModel myFileModel) {

    }

    @Override
    public void onPicHeld(FolderModel folder, View view, int position) {

    }

    public void moveFilesToFolder(Context context, final List<MyFileModel> selectedMyFiles, final int intoFolder){
        final ProgressDialog dialog = ProgressDialog.show(context,
                "Loading", "Moving", true);
        new Thread(){
            public void run(){
                DataBaseHelper db = new DataBaseHelper(getContext());
                final boolean success = db.updateFileFolderIds(selectedMyFiles, intoFolder);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success){
                            for (MyFileModel myFile : selectedMyFiles){
                                allFiles.remove(myFile);
                            }
                            fileAdapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                        requireActivity().onBackPressed();
                    }
                });
            }
        }.start();
    }
}
