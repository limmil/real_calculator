package com.project.real_calculator.ui.gallery.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.project.real_calculator.database.models.AlbumModel;
import com.project.real_calculator.encryption.Util;
import com.project.real_calculator.interfaces.IClickListener;
import com.project.real_calculator.R;

import java.io.File;
import java.util.List;
import java.util.Random;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder>{

    private List<AlbumModel> albums;
    private Context albumContx;
    private IClickListener listenToClick;

    /**
     *
     * @param albums An ArrayList of String that represents paths to albums on the external storage that contain pictures
     * @param albumContx The Activity or fragment Context
     * @param listen interFace for communication between adapter and fragment or activity
     */
    public AlbumAdapter(List<AlbumModel> albums, Context albumContx, IClickListener listen) {
        this.albums = albums;
        this.albumContx = albumContx;
        this.listenToClick = listen;
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.fragment_gallery_album, parent, false);
        return new AlbumHolder(cell);

    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder holder, final int position) {
        final AlbumModel album = albums.get(position);

        // load album thumbnail
        if (true){
            String dirPath2 = albumContx.getExternalFilesDir("media/1").getAbsolutePath();
            Random rand = new Random();
            int randomNum = rand.nextInt((5 - 1) + 1) + 1;
            File myExternalFile = new File(dirPath2, Integer.toString(randomNum));

            Glide.with(albumContx)
                    .load(myExternalFile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_baseline_album)
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.albumPic);
        }

        //setting the number of images
        String text = ""+album.getAlbumName();
        String albumSizeString=""+album.getItemsCount()+" Media";
        holder.albumSize.setText(albumSizeString);
        holder.albumName.setText(text);

        holder.albumPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listenToClick.onPicClicked(album.getPath(),album.getAlbumName());
                Toast.makeText(albumContx,"you clicked me", Toast.LENGTH_SHORT).show();
            }
        });
        holder.albumPic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listenToClick.onPicHeld(album,v,position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void updateData(){
        notifyDataSetChanged();
    }


    public class AlbumHolder extends RecyclerView.ViewHolder{
        ImageView albumPic;
        TextView albumName;
        //set textview for albumsize
        TextView albumSize;

        CardView albumCard;

        public AlbumHolder(@NonNull View itemView) {
            super(itemView);
            albumPic = itemView.findViewById(R.id.albumPic);
            albumName = itemView.findViewById(R.id.albumName);
            albumSize=itemView.findViewById(R.id.albumSize);
            albumCard = itemView.findViewById(R.id.albumCard);
        }
    }

}
