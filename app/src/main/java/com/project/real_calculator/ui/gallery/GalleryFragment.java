package com.project.real_calculator.ui.gallery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
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
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.project.real_calculator.R;
import com.project.real_calculator.database.DataBaseHelper;
import com.project.real_calculator.database.models.AlbumModel;
import com.project.real_calculator.database.models.PhotoModel;
import com.project.real_calculator.encryption.Util;
import com.project.real_calculator.interfaces.IClickListener;
import com.project.real_calculator.ui.gallery.utils.AlbumAdapter;
import com.project.real_calculator.ui.gallery.utils.MarginDecoration;
import com.project.real_calculator.ui.gallery.utils.PicHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment implements IClickListener {

    //private GalleryViewModel galleryViewModel;
    private List<AlbumModel> albums;
    RecyclerView albumRecycler;
    AlbumAdapter albumAdapter;
    TextView empty;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //init albums list
        final DataBaseHelper db = new DataBaseHelper(getActivity());
        albums = db.getAlbums();


        //galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);

        empty = root.findViewById(R.id.empty);

        albumRecycler = root.findViewById(R.id.albumRecycler);
        albumRecycler.addItemDecoration(new MarginDecoration(getActivity()));
        albumRecycler.hasFixedSize();

        if(albums.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            albumAdapter = new AlbumAdapter(albums, getActivity(), this);
            albumRecycler.setAdapter(albumAdapter);
            // new thread for encrypting
            //Runnable r_decrypt = new RunDecrypt();
            //new Thread(r_decrypt).start();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create album
                //open user input dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Add New Album");
                dialog.setContentView(R.layout.dialog_add_album);
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
                            AlbumModel newAlbum = new AlbumModel(0,name,"N/A","",0);
                            success = db.addAlbum(newAlbum);
                        }
                        if(success){
                            // update view and see item added
                            List<AlbumModel> tmp = db.getAlbums();
                            // albumAdapter is null when adding the first one
                            if (tmp.size()==1){
                                albumAdapter = new AlbumAdapter(albums, getActivity(), GalleryFragment.this);
                                albumRecycler.setAdapter(albumAdapter);
                                empty.setVisibility(View.GONE);
                            }
                            albums.add(tmp.get(tmp.size()-1));
                            albumAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(),"Created new album " + albums.size(),Toast.LENGTH_SHORT).show();
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
    public void onPicClicked(PicHolder holder, int position, ArrayList<PhotoModel> pics) {

    }

    /**
     * Each time an item in the RecyclerView is clicked this method from the implementation of the transitListerner
     * in this activity is executed, this is possible because this class is passed as a parameter in the creation
     * of the RecyclerView's Adapter, see the adapter class to understand better what is happening here
     * @param pictureAlbumPath a String corresponding to a album path on the device external storage
     */
    @Override
    public void onPicClicked(String pictureAlbumPath,String albumName) {
        //Intent move = new Intent(MainActivity.this,ImageDisplay.class);
        //move.putExtra("albumPath",pictureFolderPath);
        //move.putExtra("albumName",albumName);
        //move.putExtra("recyclerItemSize",getCardsOptimalWidth(4));
        //startActivity(move);
    }

    @Override
    public void onPicHeld(final AlbumModel album, View v, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.album_popup, popup.getMenu());
        popup.setForceShowIcon(true);
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

    public void dialogDelete(Context context, final AlbumModel album){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // clicked yes
                    case DialogInterface.BUTTON_POSITIVE:
                        // TODO: delete photos from album
                        // delete album
                        DataBaseHelper db = new DataBaseHelper(getActivity());
                        boolean success = db.deleteAlbum(album);
                        // remove album from albums array
                        if(success){
                            albums.remove(album);
                            albumAdapter.notifyDataSetChanged();
                        }
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
                    albums.get(position).setAlbumName(newName);
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

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    public byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte[] bytes = new byte[size];
        byte[] tmpBuff = new byte[size];
        FileInputStream fis= new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    public class RunDecrypt implements Runnable{
        @Override
        public void run() {
            for (int i=0; i<albums.size(); i++){
                //TODO: write decryption here
                //simulate decryption
                Util.makePasswordHash("asdf");
                Util.makePasswordHash("asdfd");
                albums.get(i).setAlbumName("thread");

                final int finalI = i;
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        albumAdapter.notifyItemChanged(finalI);
                    }
                });
            }

        }
    }

}