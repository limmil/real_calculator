package com.project.real_calculator.ui.notes;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.real_calculator.R;
import com.project.real_calculator.encryption.AES;
import com.project.real_calculator.encryption.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NewNoteActivity extends AppCompatActivity {

    private String titlePath, contentPath;
    private byte[] titleIv, contentIv;
    private File titleFile, contentFile;
    private EditText title, notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // prevents Android taking a screenshot when app goes to the background
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_new_note);

        buttonSetup();
        contentSetup();

    }

    public void buttonSetup(){
        ImageView backButton = findViewById(R.id.notesBack);
        ImageView saveButton = findViewById(R.id.notesSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void contentSetup(){
        titlePath = getIntent().getStringExtra("titlePath");
        contentPath = getIntent().getStringExtra("contentPath");
        titleIv = getIntent().getByteArrayExtra("titleIv");
        contentIv = getIntent().getByteArrayExtra("contentIv");
        titleFile = new File(titlePath);
        contentFile = new File (contentPath);
        title = findViewById(R.id.inputNoteTitle);
        notes = findViewById(R.id.inputNotes);

        if (titleFile.exists()){
            String savedTitle;
            // decrypt
            AES.setIV(titleIv);
            try {
                savedTitle = Util.decryptToString(getBytes(titleFile));
            }catch (IOException e){
                savedTitle = "";
            }
            title.setText(savedTitle);
        }
        if (contentFile.exists()){
            String savedNotes;
            // decrypt
            AES.setIV(contentIv);
            try {
                savedNotes = Util.decryptToString(getBytes(contentFile));
            }catch (IOException e){
                savedNotes = "";
            }
            notes.setText(savedNotes);
        }
    }

    public byte[] getBytes(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[64 * 1024];

        int len = 0;
        while ((len=is.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void saveNotes(){
        try { // save title
            OutputStream titleOut = new FileOutputStream(titleFile);
            AES.setIV(titleIv);
            titleOut.write(Util.encryptToByte(title.getText().toString()));
        }catch (IOException ignored){ }
        try { // save notes
            OutputStream notesOut = new FileOutputStream(contentFile);
            AES.setIV(contentIv);
            notesOut.write(Util.encryptToByte(notes.getText().toString()));
        }catch (IOException ignored){ }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AES.getSecretKey()==null){
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save file
        saveNotes();
    }
}
