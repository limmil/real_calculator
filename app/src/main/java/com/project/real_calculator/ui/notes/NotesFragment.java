package com.project.real_calculator.ui.notes;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.real_calculator.R;
import com.project.real_calculator.database.DataBaseHelper;
import com.project.real_calculator.database.models.NoteModel;
import com.project.real_calculator.interfaces.INotesClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment implements INotesClickListener {

    private List<NoteModel> notes, filterNotes;
    RecyclerView notesRecyclerView;
    NoteAdapter noteAdapter;
    DataBaseHelper db;
    TextView empty;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        db = new DataBaseHelper(getContext());

        FloatingActionButton fab = root.findViewById(R.id.notesfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent move = new Intent(getActivity(), NewNoteActivity.class);
                NoteModel noteModel = new NoteModel();
                String titlePath = requireActivity().getApplicationContext().getExternalFilesDir("notes/t").getAbsolutePath();
                String contentPath = requireActivity().getApplicationContext().getExternalFilesDir("notes/").getAbsolutePath();
                String fileName = String.valueOf((int)db.addNote(noteModel));
                File titleFile = new File(titlePath, fileName);
                File contentFile = new File(contentPath, fileName);

                move.putExtra("titlePath", titleFile.getAbsolutePath());
                move.putExtra("contentPath", contentFile.getAbsolutePath());
                move.putExtra("titleIv", noteModel.getTitleIv());
                move.putExtra("contentIv", noteModel.getContentIv());
                startActivity(move);
            }
        });

        final SearchView inputSearch = root.findViewById(R.id.inputSearch);
        // make entire SearchView clickable
        inputSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSearch.setIconified(false);
            }
        });
        inputSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getFilter().filter(newText);
                return true;
            }
        });

        notesRecyclerView = root.findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );

        // uses db
        notes = new ArrayList<>();
        notes = db.getAllNotes();
        filterNotes = new ArrayList<>(notes);

        empty = root.findViewById(R.id.emptyNotes);
        if (notes.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }

        noteAdapter = new NoteAdapter(requireContext(), notes, filterNotes, this, empty);
        notesRecyclerView.setAdapter(noteAdapter);


        return root;
    }

    @Override
    public void onNoteClicked(NoteModel noteModel) {
        Intent move = new Intent(getActivity(), NewNoteActivity.class);
        String titlePath = requireActivity().getApplicationContext().getExternalFilesDir("notes/t").getAbsolutePath();
        String contentPath = requireActivity().getApplicationContext().getExternalFilesDir("notes/").getAbsolutePath();
        String fileName = String.valueOf(noteModel.getId());
        File titleFile = new File(titlePath, fileName);
        File contentFile = new File(contentPath, fileName);

        move.putExtra("titlePath", titleFile.getAbsolutePath());
        move.putExtra("contentPath", contentFile.getAbsolutePath());
        move.putExtra("titleIv", noteModel.getTitleIv());
        move.putExtra("contentIv", noteModel.getContentIv());
        startActivity(move);
    }

    @Override
    public void onNoteHeld(final NoteModel noteModel, View view, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.note_popup, popup.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final int delete = R.id.note_delete;
                switch (item.getItemId()){
                    case delete:
                        // delete button clicked
                        dialogDelete(getContext(), noteModel);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public void dialogDelete(Context context, final NoteModel noteModel){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // clicked yes
                    case DialogInterface.BUTTON_POSITIVE:
                        new Thread(){
                            public void run(){
                                // file paths
                                String titlePath = requireActivity().getApplicationContext().getExternalFilesDir("notes/t").getAbsolutePath();
                                String contentPath = requireActivity().getApplicationContext().getExternalFilesDir("notes").getAbsolutePath();
                                // delete one note from disk
                                String fileName = String.valueOf(noteModel.getId());
                                File titleFile = new File(titlePath, fileName);
                                File contentFile = new File(contentPath, fileName);
                                titleFile.delete();
                                contentFile.delete();

                                // delete one note from database
                                DataBaseHelper db = new DataBaseHelper(getActivity());
                                boolean success = db.deleteNote(noteModel);
                                // remove NoteModel from notes array
                                if(success){
                                    notes.remove(noteModel);
                                    filterNotes.remove(noteModel);
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            noteAdapter.notifyDataSetChanged();
                                            if (notes.isEmpty()){
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
        builder.setMessage("Are you sure? This note will be deleted.")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        if(noteAdapter != null){
            // update newly added notes
            List<NoteModel> newNotes = db.getAllNotes();
            if (newNotes.size()>notes.size()){
                notes.add(newNotes.get(newNotes.size()-1));
                filterNotes.add(newNotes.get(newNotes.size()-1));
            }
            if (notes.isEmpty()){
                empty.setVisibility(View.VISIBLE);
            }else{
                empty.setVisibility(View.GONE);
            }
            noteAdapter.notifyDataSetChanged();
        }
    }

}
