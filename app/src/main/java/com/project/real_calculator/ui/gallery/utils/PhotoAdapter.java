package com.project.real_calculator.ui.gallery.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.project.real_calculator.R;
import com.project.real_calculator.database.models.PhotoModel;
import com.project.real_calculator.interfaces.IClickListener;

import java.io.File;
import java.util.List;

import static androidx.core.view.ViewCompat.setTransitionName;

public class PhotoAdapter extends RecyclerView.Adapter<PicHolder>{

    private List<PhotoModel> photoList;
    private Context contx;
    private final IClickListener listerner;

    /**
     *
     * @param photoList ArrayList of PhotoModel objects
     * @param contx The Activities Context
     * @param listerner An interface for listening to clicks on the RecyclerView's items
     */
    public PhotoAdapter(List<PhotoModel> photoList, Context contx, IClickListener listerner){
        this.photoList = photoList;
        this.contx = contx;
        this.listerner = listerner;
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
        String thumbnailDir = contx.getExternalFilesDir("media/t").getAbsolutePath();
        File myExternalFile = new File(thumbnailDir, String.valueOf(photo.getId()));
        if(!myExternalFile.exists()){
            // TODO: make new thumbnail
            // update db
        }
        Glide.with(contx)
                .load(myExternalFile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(new RequestOptions().centerCrop())
                .into(holder.picture);

        setTransitionName(holder.picture, String.valueOf(position) + "_image");

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listerner.onPicClicked(holder, position, photoList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }
}
