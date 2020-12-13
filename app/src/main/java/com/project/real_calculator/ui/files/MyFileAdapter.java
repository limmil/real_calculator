package com.project.real_calculator.ui.files;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.real_calculator.R;
import com.project.real_calculator.database.models.MyFileModel;
import com.project.real_calculator.interfaces.IFilesClickListener;

import java.util.List;

public class MyFileAdapter extends RecyclerView.Adapter<MyFileAdapter.MyFileHolder>{
    private List<MyFileModel> myFiles;
    private Context myFileContx;
    private IFilesClickListener listenToClick;

    /**
     *
     * @param myFiles An ArrayList of String that represents paths to myFiles on the external storage that contain pictures
     * @param myFileContx The Activity or fragment Context
     * @param listen interFace for communication between adapter and fragment or activity
     */
    public MyFileAdapter(List<MyFileModel> myFiles, Context myFileContx, IFilesClickListener listen) {
        this.myFiles = myFiles;
        this.myFileContx = myFileContx;
        this.listenToClick = listen;
    }

    @NonNull
    @Override
    public MyFileAdapter.MyFileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.fragment_gallery_album, parent, false);
        return new MyFileAdapter.MyFileHolder(cell);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyFileAdapter.MyFileHolder holder, final int position) {
        final MyFileModel myFile = myFiles.get(position);

        holder.checkBox.setVisibility(myFile.getCheckBoxVisibility() ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(myFile.getCheckBox());

        Glide.with(myFileContx)
                .load(R.drawable.ic_baseline_file)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.myFilePic);


        //setting the number of files
        StringBuffer fileName = new StringBuffer(myFile.getName());
        String myFileTypeString = "unknown";
        if (myFile.getName().contains(".")){
            String[] splits = myFile.getName().split("\\.");
            fileName = new StringBuffer();
            int i = 0;
            do{
                fileName.append(splits[i]);
                i++;
            }while(i<splits.length-1);
            myFileTypeString = splits[splits.length-1];
        }
        holder.myFileType.setText(myFileTypeString);
        holder.myFileName.setText(fileName);

        holder.myFilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenToClick.onFileClicked(holder, position, myFiles);
            }
        });
        holder.myFilePic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listenToClick.onFileHeld(myFile,v,position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return myFiles.size();
    }

    public void updateData(){
        notifyDataSetChanged();
    }


    public class MyFileHolder extends RecyclerView.ViewHolder{
        ImageView myFilePic;
        TextView myFileName;
        //set textview for myFilesize
        TextView myFileType;

        CardView myFileCard;
        CheckBox checkBox;

        public MyFileHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.albumCheckBox);
            myFilePic = itemView.findViewById(R.id.albumPic);
            myFileName = itemView.findViewById(R.id.albumName);
            myFileType=itemView.findViewById(R.id.albumSize);
            myFileCard = itemView.findViewById(R.id.albumCard);
        }
    }
}
