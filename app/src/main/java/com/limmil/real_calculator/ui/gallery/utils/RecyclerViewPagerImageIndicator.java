package com.limmil.real_calculator.ui.gallery.utils;

import android.content.Context;
import android.graphics.Color;
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
import com.limmil.real_calculator.interfaces.IImageIndicatorListener;

import java.io.File;
import java.util.List;

public class RecyclerViewPagerImageIndicator extends RecyclerView.Adapter<IndicatorHolder>{
    List<PhotoModel> pictureList;
    Context pictureContx;
    private final IImageIndicatorListener imageListener;

    /**
     *
     * @param pictureList ArrayList of PhotoModel objects
     * @param pictureContx The Activity of fragment context
     * @param imageListener Interface for communication between adapter and fragment
     */
    public RecyclerViewPagerImageIndicator(List<PhotoModel> pictureList, Context pictureContx, IImageIndicatorListener imageListener) {
        this.pictureList = pictureList;
        this.pictureContx = pictureContx;
        this.imageListener = imageListener;
    }


    @NonNull
    @Override
    public IndicatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.fragment_gallery_indicator_holder, parent, false);
        return new IndicatorHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull IndicatorHolder holder, final int position) {

        final PhotoModel pic = pictureList.get(position);

        holder.positionController.setBackgroundColor(pic.getSelected() ? Color.parseColor("#00000000") : Color.parseColor("#8c000000"));

        // load thumbnail
        String thumbnailDir = pictureContx.getExternalFilesDir("media/t").getAbsolutePath();
        File myExternalFile = new File(thumbnailDir, String.valueOf(pic.getId()));

        EncryptedFileObject efo = new EncryptedFileObject(myExternalFile, pic.getThumbIv());
        Glide.with(pictureContx)
                .load(efo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply(new RequestOptions().centerCrop())
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.card.setCardElevation(5);
                pic.setSelected(true);
                notifyDataSetChanged();
                imageListener.onImageIndicatorClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }
}
