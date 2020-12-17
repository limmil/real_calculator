package com.limmil.real_calculator.ui.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.limmil.real_calculator.R;
import com.limmil.real_calculator.database.models.NoteModel;
import com.limmil.real_calculator.encryption.AES;
import com.limmil.real_calculator.encryption.Util;
import com.limmil.real_calculator.interfaces.INotesClickListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> implements Filterable {

    private List<NoteModel> notes;
    private List<NoteModel> filterNotes;
    private TextView empty;
    private Context context;
    private INotesClickListener listener;

    public NoteAdapter(Context context, List<NoteModel> notes, List<NoteModel> filterNotes, INotesClickListener listener, TextView empty){
        this.context = context;
        this.notes = notes;
        this.listener = listener;
        this.filterNotes = filterNotes;
        this.empty = empty;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.note_item_container, parent, false);
        return new NoteAdapter.NoteViewHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
        final NoteModel noteModel = notes.get(position);

        // decrypt only once
        if (noteModel.getTitle().isEmpty() && noteModel.getContentPreview().isEmpty()) {
            decryptNoteModel(context, noteModel);
        }
        holder.title.setText(noteModel.getTitle());
        holder.contentPreview.setText(noteModel.getContentPreview());
        holder.dateTime.setText(noteModel.getDateTime());

        holder.noteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoteClicked(noteModel);
            }
        });
        holder.noteItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onNoteHeld(noteModel, v, position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }
    private Filter myFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<NoteModel> filteredList = new ArrayList<>();
            if (constraint==null || constraint.length()==0){
                filteredList.addAll(filterNotes);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (NoteModel item : filterNotes){
                    if (item.getTitle().toLowerCase().contains(filterPattern) ||
                        item.getContentPreview().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes.clear();
            notes.addAll((List)results.values);
            if (notes.isEmpty()){
                empty.setVisibility(View.VISIBLE);
            }else {
                empty.setVisibility(View.GONE);
            }
            notifyDataSetChanged();
        }
    };

    private void decryptNoteModel(Context context, final NoteModel noteModel){

        String titlePath = context.getExternalFilesDir("notes/t").getAbsolutePath();
        String contentPath = context.getExternalFilesDir("notes/").getAbsolutePath();
        String fileName = String.valueOf(noteModel.getId());
        File titleFile = new File(titlePath, fileName);
        File contentFile = new File(contentPath, fileName);

        AES titleDecrypt = new AES(noteModel.getTitleIv());
        noteModel.setTitle(Util.byteToString(titleDecrypt.iDecrypt(getBytes(titleFile))));
        String noteTitle = Util.byteToString(titleDecrypt.iDecrypt(getBytes(titleFile)));
        if (noteTitle.length() > 100){
            noteModel.setTitle(noteTitle.substring(0,100));
        }else{
            noteModel.setTitle(noteTitle);
        }

        AES contentDecrypt = new AES(noteModel.getContentIv());
        String noteContent = Util.byteToString(contentDecrypt.iDecrypt(getBytes(contentFile)));
        if (noteContent.length() > 100){
            noteModel.setContentPreview(noteContent.substring(0,100));
        }else{
            noteModel.setContentPreview(noteContent);
        }
    }

    public byte[] getBytes(File file) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream((int)file.length());
        try {
            InputStream is = new FileInputStream(file);
            byte[] buffer = new byte[64 * 1024];

            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        }catch (IOException ignored){
            byteBuffer = new ByteArrayOutputStream(0);
        }

        return byteBuffer.toByteArray();
    }



    static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView title, contentPreview, dateTime;
        LinearLayout noteItem;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteItem = itemView.findViewById(R.id.noteItem);
            title = itemView.findViewById(R.id.noteTitle);
            contentPreview = itemView.findViewById(R.id.noteContentPreview);
            dateTime = itemView.findViewById(R.id.noteDateTime);
        }
    }
}
