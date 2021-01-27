package com.limmil.real_calculator.ui.gallery;

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

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.limmil.real_calculator.R;
import com.limmil.real_calculator.database.DataBaseHelper;
import com.limmil.real_calculator.database.models.AlbumModel;
import com.limmil.real_calculator.database.models.PhotoModel;
import com.limmil.real_calculator.interfaces.IGalleryClickListener;
import com.limmil.real_calculator.ui.gallery.utils.AlbumAdapter;
import com.limmil.real_calculator.ui.gallery.utils.MarginDecoration;
import com.limmil.real_calculator.ui.gallery.utils.PicHolder;

import java.io.File;
import java.util.List;

public class GalleryFragment extends Fragment implements IGalleryClickListener {

    private List<AlbumModel> allAlbums;
    RecyclerView albumRecycler;
    AlbumAdapter albumAdapter;
    TextView empty;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //init albums list
        final DataBaseHelper db = new DataBaseHelper(getActivity());
        allAlbums = db.getAlbums();


        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);

        empty = root.findViewById(R.id.empty);

        albumRecycler = root.findViewById(R.id.albumRecycler);
        albumRecycler.addItemDecoration(new MarginDecoration(requireActivity()));
        albumRecycler.hasFixedSize();

        if(allAlbums.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            albumAdapter = new AlbumAdapter(allAlbums, getActivity(), this);
            albumRecycler.setAdapter(albumAdapter);
            albumRecycler.scrollToPosition(albumAdapter.getItemCount()-1);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create album
                //open user input dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Add New Album");
                dialog.setContentView(R.layout.dialog_add_album);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                //setting button and EditText from dialog
                final EditText albumName = (EditText) dialog.findViewById(R.id.addAlbumName);
                Button yesButton = (Button) dialog.findViewById(R.id.dialog_album_btn_yes);
                Button noButton = (Button) dialog.findViewById(R.id.dialog_album_btn_no);

                yesButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // add new album to database
                        String name = albumName.getText().toString().trim();
                        boolean success = false;
                        if(name.isEmpty()){
                            Toast.makeText(getActivity(),"Name cannot be empty",Toast.LENGTH_SHORT).show();
                        }else{
                            AlbumModel newAlbum = new AlbumModel();
                            newAlbum.setAlbumName(name);
                            success = db.addAlbum(newAlbum);
                        }
                        if(success){
                            // update view and see item added
                            List<AlbumModel> tmp = db.getAlbums();
                            // albumAdapter is null when adding the first one
                            if (tmp.size()==1){
                                albumAdapter = new AlbumAdapter(allAlbums, getActivity(), GalleryFragment.this);
                                albumRecycler.setAdapter(albumAdapter);
                                empty.setVisibility(View.GONE);
                            }
                            if (!tmp.isEmpty()) {
                                allAlbums.add(tmp.get(tmp.size() - 1));
                                albumAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "Created new album", Toast.LENGTH_SHORT).show();
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
    public void onPicClicked(PicHolder holder, int position, List<PhotoModel> pics) {

    }

    /**
     * Each time an item in the RecyclerView is clicked this method from the implementation of the transitListerner
     * in this activity is executed, this is possible because this class is passed as a parameter in the creation
     * of the RecyclerView's Adapter, see the adapter class to understand better what is happening here
     * @param albumModel a String corresponding to a album path on the device external storage
     */
    @Override
    public void onPicClicked(AlbumModel albumModel) {
        Intent move = new Intent(getActivity(), ImageBrowseActivity.class);
        move.putExtra("albumId", albumModel.getId());
        move.putExtra("albumName", albumModel.getAlbumName());

        startActivity(move);
    }

    @Override
    public void onPicHeld(final AlbumModel album, View v, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
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
                        dialogDelete(getContext(), album);
                        break;
                    case edit:
                        // edit button clicked
                        editAlbumName(album, position);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onPicHeld(PhotoModel photo, View view, int position) {

    }

    public void dialogDelete(Context context, final AlbumModel album){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // clicked yes
                    case DialogInterface.BUTTON_POSITIVE:
                        new Thread(){
                            public void run(){
                                // file paths
                                String filePath = requireActivity().getApplicationContext().getExternalFilesDir("media/").getAbsolutePath();
                                String thumbPath = requireActivity().getApplicationContext().getExternalFilesDir("media/t").getAbsolutePath();
                                // delete all photos in album
                                DataBaseHelper db = new DataBaseHelper(getActivity());
                                List<PhotoModel> photoIds = db.getPhotoIdsFromAlbum(album);
                                if (!photoIds.isEmpty()){
                                    boolean result = db.deleteAllPhotosFromAlbum(album);
                                    if (result){
                                        for (PhotoModel photoModel : photoIds){
                                            String name = String.valueOf(photoModel.getId());
                                            File deleteFile = new File(filePath, name);
                                            File deleteThumb = new File(thumbPath, name);
                                            deleteFile.delete();
                                            deleteThumb.delete();
                                        }
                                    }
                                }
                                // delete album
                                boolean success = db.deleteAlbum(album);
                                // remove album from albums array
                                if(success){
                                    allAlbums.remove(album);
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            albumAdapter.notifyDataSetChanged();
                                            if (allAlbums.isEmpty()){
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
        builder.setMessage("Are you sure? All contents in this album will be deleted.")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void editAlbumName(final AlbumModel albumModel, final int position){
        // show dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_album);

        TextView title = dialog.findViewById(R.id.addNewAlbum);
        final EditText albumName = dialog.findViewById(R.id.addAlbumName);
        Button yesButton = dialog.findViewById(R.id.dialog_album_btn_yes);
        Button noButton = dialog.findViewById(R.id.dialog_album_btn_no);

        dialog.setTitle("Edit Album");
        title.setText(getString(R.string.edit_album));
        albumName.setText(albumModel.getAlbumName());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        // buttons
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseHelper db = new DataBaseHelper(getActivity());
                // get text from albumName
                // update name in album
                String newName = albumName.getText().toString().trim();
                // save change to db and albums array
                boolean success = false;
                if(newName.isEmpty()){
                    Toast.makeText(getActivity(),"Name cannot be empty",Toast.LENGTH_SHORT).show();
                }else{
                    albumModel.setAlbumName(newName);
                    success = db.updateAlbum(albumModel);
                }
                if(success){
                    allAlbums.get(position).setAlbumName(newName);
                    albumAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(),"Updated album name",Toast.LENGTH_SHORT).show();
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
        if(albumAdapter != null){
            albumAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Glide.get(requireContext()).clearMemory();
    }

}