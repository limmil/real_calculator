package com.project.real_calculator.ui.files;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.real_calculator.database.DataBaseHelper;
import com.project.real_calculator.database.models.FolderModel;
import com.project.real_calculator.database.models.MyFileModel;
import com.project.real_calculator.R;
import com.project.real_calculator.interfaces.IFilesClickListener;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderHolder>{

    private List<FolderModel> folders;
    private Context folderContx;
    private IFilesClickListener listenToClick;

    /**
     *
     * @param folders An ArrayList of String that represents paths to folders on the external storage that contain pictures
     * @param folderContx The Activity or fragment Context
     * @param listen interFace for communication between adapter and fragment or activity
     */
    public FolderAdapter(List<FolderModel> folders, Context folderContx, IFilesClickListener listen) {
        this.folders = folders;
        this.folderContx = folderContx;
        this.listenToClick = listen;
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.fragment_gallery_album, parent, false);
        return new FolderHolder(cell);

    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, final int position) {
        final FolderModel folder = folders.get(position);

        DataBaseHelper db = new DataBaseHelper(folderContx);
        List<MyFileModel> myFileIds= db.getFileIdsFromFolder(folder);

        Glide.with(folderContx)
                .load(R.drawable.ic_menu_folder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.folderPic);


        //setting the number of images
        String text = ""+folder.getFolderName();
        String folderSizeString=""+myFileIds.size()+" Files";
        holder.folderSize.setText(folderSizeString);
        holder.folderName.setText(text);

        holder.folderPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenToClick.onPicClicked(folder);
            }
        });
        holder.folderPic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listenToClick.onPicHeld(folder,v,position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public void updateData(){
        notifyDataSetChanged();
    }


    public class FolderHolder extends RecyclerView.ViewHolder{
        ImageView folderPic;
        TextView folderName;
        //set textview for foldersize
        TextView folderSize;

        CardView folderCard;

        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            folderPic = itemView.findViewById(R.id.albumPic);
            folderName = itemView.findViewById(R.id.albumName);
            folderSize=itemView.findViewById(R.id.albumSize);
            folderCard = itemView.findViewById(R.id.albumCard);
        }
    }

}
