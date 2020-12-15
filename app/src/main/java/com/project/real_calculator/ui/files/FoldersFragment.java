package com.project.real_calculator.ui.files;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.real_calculator.R;
import com.project.real_calculator.database.DataBaseHelper;
import com.project.real_calculator.database.models.AlbumModel;
import com.project.real_calculator.database.models.FolderModel;
import com.project.real_calculator.database.models.MyFileModel;
import com.project.real_calculator.database.models.PhotoModel;
import com.project.real_calculator.interfaces.IFilesClickListener;
import com.project.real_calculator.ui.gallery.ImageBrowseActivity;
import com.project.real_calculator.ui.gallery.utils.MarginDecoration;
import com.project.real_calculator.ui.gallery.utils.PicHolder;

import java.io.File;
import java.util.List;

public class FoldersFragment extends Fragment implements IFilesClickListener {

    private List<FolderModel> allFolders;
    RecyclerView folderRecycler;
    FolderAdapter folderAdapter;
    TextView empty;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_folders, container, false);
        //init folders list
        final DataBaseHelper db = new DataBaseHelper(getActivity());
        allFolders = db.getFolders();
        

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.folder_fab);

        empty = root.findViewById(R.id.folder_empty);

        folderRecycler = root.findViewById(R.id.folderRecycler);
        folderRecycler.addItemDecoration(new MarginDecoration(requireActivity()));
        folderRecycler.hasFixedSize();

        if(allFolders.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            folderAdapter = new FolderAdapter(allFolders, getActivity(), this);
            folderRecycler.setAdapter(folderAdapter);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create folder
                //open user input dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Add New Folder");
                dialog.setContentView(R.layout.dialog_add_album);
                // reusing dialog_add_album
                TextView title = dialog.findViewById(R.id.addNewAlbum);
                title.setText(getString(R.string.add_new_folder));
                dialog.show();

                //setting button and EditText from dialog
                final EditText folderName = (EditText) dialog.findViewById(R.id.addAlbumName);
                folderName.setHint("Folder Name");
                Button yesButton = (Button) dialog.findViewById(R.id.dialog_album_btn_yes);
                Button noButton = (Button) dialog.findViewById(R.id.dialog_album_btn_no);

                yesButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // add new folder to database
                        String name = folderName.getText().toString().trim();
                        boolean success = false;
                        if(name.isEmpty()){
                            Toast.makeText(getActivity(),"Name cannot be empty",Toast.LENGTH_SHORT).show();
                        }else{
                            FolderModel newFolder = new FolderModel();
                            newFolder.setFolderName(name);
                            success = db.addFolder(newFolder);
                        }
                        if(success){
                            // update view and see item added
                            List<FolderModel> tmp = db.getFolders();
                            // folderAdapter is null when adding the first one
                            if (tmp.size()==1){
                                folderAdapter = new FolderAdapter(allFolders, getActivity(), FoldersFragment.this);
                                folderRecycler.setAdapter(folderAdapter);
                                empty.setVisibility(View.GONE);
                            }
                            if (!tmp.isEmpty()) {
                                allFolders.add(tmp.get(tmp.size() - 1));
                                folderAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "Created new folder", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getActivity(),"Something went wrong.",Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();

                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });


        return root;
    }


    @Override
    public void onFileClicked(MyFileAdapter.MyFileHolder holder, int position, List<MyFileModel> files) {

    }

    @Override
    public void onFolderClicked(FolderModel folder) {
        Intent move = new Intent(getActivity(), FilesActivity.class);
        move.putExtra("folderId", folder.getId());
        move.putExtra("folderName", folder.getFolderName());

        startActivity(move);
    }

    @Override
    public void onFileClicked(MyFileModel myFileModel) {

    }

    @Override
    public void onFolderHeld(final FolderModel folder, View view, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.album_popup, popup.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final int delete = R.id.action_delete;
                final int edit = R.id.action_edit;
                switch (item.getItemId()){
                    case delete:
                        // delete button clicked
                        dialogDelete(getContext(), folder);
                        break;
                    case edit:
                        // edit button clicked
                        editFolderName(folder, position);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onFileHeld(MyFileModel file, View view, int position) {

    }

    public void dialogDelete(Context context, final FolderModel folder){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // clicked yes
                    case DialogInterface.BUTTON_POSITIVE:
                        new Thread(){
                            public void run(){
                                // file paths
                                String filePath = requireActivity().getApplicationContext().getExternalFilesDir("folder/").getAbsolutePath();
                                // delete all files in folder
                                DataBaseHelper db = new DataBaseHelper(getActivity());
                                List<MyFileModel> fileIds = db.getFileIdsFromFolder(folder);
                                if (!fileIds.isEmpty()){
                                    for (MyFileModel fileModel : fileIds){
                                        String name = String.valueOf(fileModel.getId());
                                        File deleteFile = new File(filePath, name);
                                        deleteFile.delete();
                                    }
                                }
                                // delete all files
                                db.deleteAllFilesFromFolder(folder);
                                // delete folder
                                boolean success = db.deleteFolder(folder);
                                // remove album from albums array
                                if(success){
                                    allFolders.remove(folder);
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            folderAdapter.notifyDataSetChanged();
                                            if (allFolders.isEmpty()){
                                                empty.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                                }
                            }
                        }.start();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure? All contents in this folder will be deleted.")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void editFolderName(final FolderModel folderModel, final int position){
        // show dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_album);

        TextView title = dialog.findViewById(R.id.addNewAlbum);
        final EditText folderName = dialog.findViewById(R.id.addAlbumName);
        Button yesButton = dialog.findViewById(R.id.dialog_album_btn_yes);
        Button noButton = dialog.findViewById(R.id.dialog_album_btn_no);

        dialog.setTitle("Edit Folder");
        title.setText(getString(R.string.edit_folder));
        folderName.setText(folderModel.getFolderName());
        dialog.show();
        // buttons
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseHelper db = new DataBaseHelper(getActivity());
                // get text from folderName
                // update name in folder
                String newName = folderName.getText().toString().trim();
                // save change to db and albums array
                boolean success = false;
                if(newName.isEmpty()){
                    Toast.makeText(getActivity(),"Name cannot be empty",Toast.LENGTH_SHORT).show();
                }else{
                    folderModel.setFolderName(newName);
                    success = db.updateFolder(folderModel);
                }
                if(success){
                    allFolders.get(position).setFolderName(newName);
                    folderAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(),"Updated folder name",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(folderAdapter != null){
            folderAdapter.notifyDataSetChanged();
        }
    }
}
