package com.limmil.real_calculator.ui.gallery.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.limmil.real_calculator.R;
import com.limmil.real_calculator.database.models.PhotoModel;
import com.limmil.real_calculator.encryption.EncryptedFileObject;
import com.limmil.real_calculator.interfaces.IGalleryClickListener;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static androidx.core.view.ViewCompat.setTransitionName;

public class PhotoAdapter extends RecyclerView.Adapter<PicHolder>{

    private List<PhotoModel> photoList;
    private Context contx;
    private final IGalleryClickListener listener;
    private String thumbnailDir;

    /**
     *
     * @param photoList ArrayList of PhotoModel objects
     * @param contx The Activities Context
     * @param listener An interface for listening to clicks on the RecyclerView's items
     */
    public PhotoAdapter(List<PhotoModel> photoList, Context contx, IGalleryClickListener listener, String thumbnailDir){
        this.photoList = photoList;
        this.contx = contx;
        this.listener = listener;
        this.thumbnailDir = thumbnailDir;
    }

    @NonNull
    @Override
    public PicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.fragment_gallery_photo, parent, false);
        return new PicHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull final PicHolder holder, final int position) {
        final PhotoModel photo = photoList.get(position);

        holder.checkBox.setVisibility(photo.getCheckBoxVisibility() ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(photo.getCheckBox());

        // load thumbnail
        File myExternalFile = new File(thumbnailDir, String.valueOf(photo.getId()));

        //AES.setIV(photo.getThumbIv());
        EncryptedFileObject efo = new EncryptedFileObject(myExternalFile, photo.getThumbIv());
        Glide.with(contx)
                .load(efo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_baseline_image)
                .apply(new RequestOptions().centerCrop())
                .into(holder.picture);

        setTransitionName(holder.picture, String.valueOf(position) + "_image");

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPicClicked(holder, position, photoList);
            }
        });
        holder.picture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onPicHeld(photo,v,position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }
}
