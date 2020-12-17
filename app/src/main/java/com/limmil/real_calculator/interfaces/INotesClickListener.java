package com.limmil.real_calculator.interfaces;

import android.view.View;

import com.limmil.real_calculator.database.models.NoteModel;

public interface INotesClickListener {
    void onNoteClicked(NoteModel noteModel);
    void onNoteHeld(NoteModel noteModel, View view, int position);
}
