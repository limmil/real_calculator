package com.limmil.real_calculator.ui.gallery;

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
import com.limmil.real_calculator.R;
import com.limmil.real_calculator.database.DataBaseHelper;
import com.limmil.real_calculator.database.models.AlbumModel;
import com.limmil.real_calculator.database.models.PhotoModel;
import com.limmil.real_calculator.interfaces.IGalleryClickListener;
import com.limmil.real_calculator.ui.gallery.utils.AlbumAdapter;
import com.limmil.real_calculator.ui.gallery.utils.MarginDecoration;
import com.limmil.real_calculator.ui.gallery.utils.PhotoAdapter;
import com.limmil.real_calculator.ui.gallery.utils.PicHolder;

import java.util.List;

public class SelectAlbumFragment extends Fragment implements IGalleryClickListener {

    private Context context;
    private List<AlbumModel> allAlbums;
    private List<PhotoModel> selectedPhotos;
    private int currentAlbumId;
    List<PhotoModel> allImages;
    PhotoAdapter photoAdapter;
    RecyclerView albumRecycler;
    AlbumAdapter albumAdapter;
    TextView empty, photoEmpty;
    
    public SelectAlbumFragment (Context context, List<PhotoModel> selectedPhotos, PhotoAdapter photoAdapter, List<PhotoModel> allImages, int currentAlbumId, TextView photoEmpty){
        this.context = context;
        this.selectedPhotos = selectedPhotos;
        this.allImages = allImages;
        this.photoAdapter = photoAdapter;
        this.currentAlbumId = currentAlbumId;
        this.photoEmpty = photoEmpty;
    }
    
    public static SelectAlbumFragment newInstance(Context context, List<PhotoModel> selectedPhotos, PhotoAdapter photoAdapter, List<PhotoModel> allImages, int currentAlbumId, TextView photoEmpty){
        return new SelectAlbumFragment(context, selectedPhotos, photoAdapter, allImages, currentAlbumId, photoEmpty);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //init albums list
        final DataBaseHelper db = new DataBaseHelper(getActivity());
        allAlbums = db.getAlbums();
        // remove current album from album list
        AlbumModel currentAlbum = null;
        for (AlbumModel album : allAlbums){
            if (album.getId()==currentAlbumId){
                currentAlbum = album;
            }
        }
        if(currentAlbum!=null){allAlbums.remove(currentAlbum);}
        
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);


        empty = root.findViewById(R.id.empty);

        albumRecycler = root.findViewById(R.id.albumRecycler);
        albumRecycler.addItemDecoration(new MarginDecoration(requireActivity()));
        albumRecycler.hasFixedSize();

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        if(allAlbums.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            String dirPath = getActivity().getExternalFilesDir("media/t").getAbsolutePath();
            albumAdapter = new AlbumAdapter(allAlbums, getActivity(), this, dirPath, db);
            albumRecycler.setAdapter(albumAdapter);
        }



        return root;
    }

    @Override
    public void onPicClicked(PicHolder holder, int position, List<PhotoModel> pics) {
        
    }

    @Override
    public void onPicClicked(AlbumModel album) {
        final int id = album.getId();
        // code in here
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // clicked yes
                    case DialogInterface.BUTTON_POSITIVE:
                        moveImagesToAlbum(context, selectedPhotos, id);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Moving " + selectedPhotos.size() + " images to " + album.getAlbumName() + "?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onPicHeld(AlbumModel album, View view, int position) {

    }

    @Override
    public void onPicHeld(PhotoModel photo, View view, int position) {

    }

    public void moveImagesToAlbum(Context context, final List<PhotoModel> selectedPhotos, final int intoAlbum){
        final ProgressDialog dialog = ProgressDialog.show(context,
                "Loading", "Moving", true);
        new Thread(){
            public void run(){
                DataBaseHelper db = new DataBaseHelper(getContext());
                final boolean success = db.updatePhotoAlbumIds(selectedPhotos, intoAlbum);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success){
                            for (PhotoModel photo : selectedPhotos){
                                allImages.remove(photo);
                            }
                            photoAdapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                        if(allImages.isEmpty()){
                            photoEmpty.setVisibility(View.VISIBLE);
                        }
                        requireActivity().onBackPressed();
                    }
                });
            }
        }.start();
    }
}
