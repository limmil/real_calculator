package com.project.real_calculator.ui.files;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.Fade;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.real_calculator.R;
import com.project.real_calculator.database.DataBaseHelper;
import com.project.real_calculator.database.models.FolderModel;
import com.project.real_calculator.database.models.MyFileModel;
import com.project.real_calculator.encryption.AES;
import com.project.real_calculator.encryption.Util;
import com.project.real_calculator.interfaces.IFilesClickListener;
import com.project.real_calculator.tools.FileMetaData;
import com.project.real_calculator.ui.gallery.utils.MarginDecoration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.CipherInputStream;

public class FilesActivity extends AppCompatActivity implements IFilesClickListener {
    //request code to pick files
    private static final int PICK_FILES_CODE = 0;
    //store image uris in this array list
    private ArrayList<Uri> filesUris;
    RecyclerView fileRecycler;
    MyFileAdapter fileAdapter;
    List<MyFileModel> allFiles;
    ProgressBar load;
    int folderId;
    TextView folderName;
    ImageButton backButton, menuButton, deleteButton, moveButton, exportButton;
    Button undoButton;
    boolean selecting;
    TextView empty;
    DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DataBaseHelper(getApplicationContext());

        // prevents Android taking a screenshot when app goes to the background
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_image_browse);

        filesUris = new ArrayList<>();
        selecting = false;
        setupButtons();

        empty = findViewById(R.id.iEmpty);
        empty.setText(getString(R.string.no_files));

        folderName = findViewById(R.id.imageAlbumName);
        String name = getIntent().getStringExtra("folderName");
        folderId = getIntent().getIntExtra("folderId", 0);
        folderName.setText(name);

        allFiles = new ArrayList<>();
        fileRecycler = findViewById(R.id.imageRecycler);
        fileRecycler.addItemDecoration(new MarginDecoration(this));
        fileRecycler.hasFixedSize();

        load = findViewById(R.id.loader);
        load.setVisibility(View.VISIBLE);
        DataBaseHelper db = new DataBaseHelper(this);
        // db.getFilesFromFolder only needs the id field
        allFiles = db.getFilesFromFolder(new FolderModel(folderId, name,"",0));
        fileAdapter = new MyFileAdapter(allFiles, this,this);
        fileRecycler.setAdapter(fileAdapter);
        load.setVisibility(View.GONE);

        if (allFiles.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.imagefab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selecting) {
                    pickImagesIntent();
                }
            }
        });

    }

    /**
     *
     * @param holder The ViewHolder for the clicked picture
     * @param position The position in the grid of the picture that was clicked
     * @param files An ArrayList of all the items in the Adapter
     */
    @Override
    public void onFileClicked(MyFileAdapter.MyFileHolder holder, int position, List<MyFileModel> files) {

        if(selecting) {
            holder.checkBox.setChecked(!files.get(position).getCheckBox());
            files.get(position).setCheckBox(!files.get(position).getCheckBox());
        }

    }

    @Override
    public void onFolderClicked(FolderModel folderModel) {

    }

    @Override
    public void onFileClicked(MyFileModel myFileModel) {

    }

    @Override
    public void onFolderHeld(FolderModel folderModel, View view, int position) {

    }

    @Override
    public void onFileHeld(final MyFileModel file, View view, final int position) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.album_popup, popup.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final int delete = R.id.action_delete;
                final int edit = R.id.action_edit;
                switch (item.getItemId()){
                    case delete:
                        // delete button clicked
                        dialogDelete(FilesActivity.this, file);
                        break;
                    case edit:
                        // edit button clicked
                        editFileName(file, position);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public void dialogDelete(final Context context, final MyFileModel file){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // clicked yes
                    case DialogInterface.BUTTON_POSITIVE:
                        new Thread(){
                            public void run(){
                                // file paths
                                String filePath = getApplicationContext().getExternalFilesDir("folder/").getAbsolutePath();
                                // delete all files in folder
                                DataBaseHelper db = new DataBaseHelper(context);
                                String name = String.valueOf(file.getId());
                                File deleteFile = new File(filePath, name);
                                deleteFile.delete();

                                boolean success = db.deleteFile(file);
                                if (success){
                                    allFiles.remove(file);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            fileAdapter.notifyDataSetChanged();
                                            if (allFiles.isEmpty()){
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
        builder.setMessage("Are you sure? "+file.getName()+" will be permanently deleted.")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void editFileName(final MyFileModel file, final int position){
        // show dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_album);

        TextView title = dialog.findViewById(R.id.addNewAlbum);
        TextView fileSize = dialog.findViewById(R.id.fileSize);
        TextView fileType = dialog.findViewById(R.id.fileType);
        final EditText fileName = dialog.findViewById(R.id.addAlbumName);
        Button yesButton = dialog.findViewById(R.id.dialog_album_btn_yes);
        yesButton.setText(getString(R.string.update));
        Button noButton = dialog.findViewById(R.id.dialog_album_btn_no);

        dialog.setTitle("Edit File");
        title.setText(getString(R.string.edit_file));

        String fType = "File Type: unknown";
        StringBuffer fName = new StringBuffer(file.getName());
        String fSize = "File Size: " + file.getSize();
        String fExt = "";
        if (file.getName().contains(".")){
            String[] nameSplits = file.getName().split("\\.");
            fName = new StringBuffer();
            int i = 0;
            do{
                fName.append(nameSplits[i]);
                i++;
            }while(i<nameSplits.length-1);
            fExt = nameSplits[nameSplits.length-1];
            fType = "File Type: " + fExt;
        }
        fileName.setText(fName);
        fileSize.setVisibility(View.VISIBLE);
        fileSize.setText(fSize);
        fileType.setVisibility(View.VISIBLE);
        fileType.setText(fType);

        dialog.show();
        // buttons
        final String finalFExt = fExt;
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseHelper db = new DataBaseHelper(getApplicationContext());
                // get text from fileName
                // update name in folder
                String newName = fileName.getText().toString().trim();
                boolean hasDot = newName.contains(".");
                String finalName = newName + "." + finalFExt;
                // save change to db and folders array
                boolean success = false;
                if(newName.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Name cannot be empty",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(hasDot){
                        Toast.makeText(getApplicationContext(),"Name cannot contain .",Toast.LENGTH_SHORT).show();
                    }else {
                        file.setName(finalName);
                        success = db.updateFileName(file);
                    }
                }
                if(success){
                    allFiles.get(position).setName(finalName);
                    fileAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),"Updated file name",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }


    public void pickImagesIntent(){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        String[] mimeTypes = {"*/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // do not allow AppController to clear AES key after opening intent
        AES.setAllowClearing(false);
        startActivityForResult(Intent.createChooser(intent, "Select File(s)"), PICK_FILES_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data){
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_FILES_CODE){
            if (resultCode == Activity.RESULT_OK){

                final ProgressDialog dialog = ProgressDialog.show(this,
                        "Loading", "Encrypting", true);

                new Thread() {
                    public void run() {
                        if (data.getClipData() != null) {
                            // picked multiple images
                            int cout = data.getClipData().getItemCount();
                            final int startRange = allFiles.size();
                            for (int i = 0; i < cout; i++) {
                                // update dialog
                                final int finalI = i;
                                final int finalCout = cout;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String message = "Encrypting " +
                                                (finalI + 1) + "/" +
                                                finalCout;
                                        dialog.setMessage(message);
                                    }
                                });
                                // get image uri at specific index
                                Uri fileUri = data.getClipData().getItemAt(i).getUri();
                                try {
                                    saveFileFromURI(fileUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            final int endRange = allFiles.size();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fileAdapter.notifyItemRangeChanged(startRange, endRange);
                                }
                            });
                        } else {
                            // picked one image
                            Uri fileUri = data.getData();
                            try {
                                saveFileFromURI(fileUri);
                            } catch (IOException e) {
                                //e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fileAdapter.notifyItemChanged(allFiles.size());
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                empty.setVisibility(View.GONE);
                                // allow key clearing after file encryption
                                AES.setAllowClearing(true);
                            }
                        });
                    }
                }.start();
            }
        }
    }

    public void saveFileFromURI(Uri uri) throws IOException{
        FileMetaData fileMetaData = FileMetaData.getFileMetaData(getApplicationContext(), uri);

        MyFileModel saveFile = new MyFileModel(0,"N/A","N/A","N/A","N/A",0);
        FolderModel intoFolder = new FolderModel(folderId,"","",0);

        int id = (int) db.addFile(saveFile, intoFolder);
        String storageFileName = String.valueOf(id);

        // encrypt and save image
        String filePath = getApplicationContext().getExternalFilesDir("folder/").getAbsolutePath();
        InputStream imageStream = getApplicationContext().getContentResolver().openInputStream(uri);
        File newImage = new File(filePath, storageFileName);
        FileOutputStream imageOut = new FileOutputStream(newImage);

        // file maybe too large to fit in memory for encryption
        // Get length of file in MB
        long fileSize = fileMetaData.size;
        long fileSizeInMB = fileSize / (1024*1024);
        if (fileSizeInMB < 100 && fileSize!=0){
            // faster but more memory cost
            imageOut.write(Util.encryptToByte(getBytes(imageStream)));
        }else {
            // slower but less memory cost
            CipherInputStream cis = new CipherInputStream(imageStream, AES.getEncryptionCipher());

            byte[] buffer = new byte[64*1024];
            int len = 0;
            while ((len=cis.read(buffer)) != -1) {
                imageOut.write(buffer, 0, len);
            }
            cis.close();
        }

        imageStream.close();
        imageOut.close();
        // file checksum
        FileInputStream in = new FileInputStream(newImage);
        String imageHash = Util.md5(in);
        in.close();

        // update saveFile
        saveFile.setId(id);
        saveFile.setFolder(folderId);
        saveFile.setName(fileMetaData.displayName);
        saveFile.setFileType(fileMetaData.mimeType);
        saveFile.setContent(imageHash);
        saveFile.setSize(readableFileSize(fileSize));
        db.updateFile(saveFile);
        // for fileAdapter.notifyDataSetChanged();
        allFiles.add(saveFile);

    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[64 * 1024];

        int len = 0;
        while ((len=inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void setupButtons(){
        
        backButton = findViewById(R.id.browseBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        undoButton = findViewById(R.id.undo_select);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoButtonFunction();
            }
        });

        deleteButton = findViewById(R.id.browse_delete_btn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if any are selected
                List<Integer> positions = new ArrayList<>();
                for (int i=0; i<allFiles.size(); i++){
                    if (allFiles.get(i).getCheckBox()){
                        positions.add(i);
                    }
                }
                if (!positions.isEmpty()){
                    dialogDeleteSelectedImages(FilesActivity.this, allFiles, positions);
                }
            }
        });

        moveButton = findViewById(R.id.browse_move_btn);
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move selected images to different album
                List<Integer> positions = new ArrayList<>();
                for (int i=0; i<allFiles.size(); i++){
                    if (allFiles.get(i).getCheckBox()){
                        positions.add(i);
                    }
                }
                if (!positions.isEmpty()){
                    dialogMoveSelectedImages(FilesActivity.this, allFiles, positions);
                }
            }
        });

        exportButton = findViewById(R.id.browse_export_btn);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // export selected images to folder
                List<Integer> positions = new ArrayList<>();
                for (int i=0; i<allFiles.size(); i++){
                    if (allFiles.get(i).getCheckBox()){
                        positions.add(i);
                    }
                }
                if (!positions.isEmpty()){
                    dialogExportSelectedFiles(FilesActivity.this, allFiles, positions);
                }
            }
        });


        menuButton = findViewById(R.id.browseMenu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.image_browser_popup, popup.getMenu());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popup.setForceShowIcon(true);
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final int delete = R.id.browser_action_delete;
                        final int move = R.id.browser_action_move;
                        final int export = R.id.browser_action_export;
                        switch (item.getItemId()){
                            case delete:
                                // delete button clicked
                                deleteButton.setVisibility(View.VISIBLE);
                                menuClick();
                                break;
                            case move:
                                // edit button clicked
                                moveButton.setVisibility(View.VISIBLE);
                                menuClick();
                                break;
                            case export:
                                // export
                                exportButton.setVisibility(View.VISIBLE);
                                menuClick();
                                break;
                        }
                        return true;
                    }
                    public void menuClick(){
                        menuButton.setVisibility(View.GONE);
                        undoButton.setVisibility(View.VISIBLE);
                        selecting = true;
                        for (MyFileModel fileModel : allFiles){
                            fileModel.setCheckBoxVisibility(true);
                        }
                        fileAdapter.notifyDataSetChanged();
                    }
                });
                popup.show();
            }
        });
        
    }

    public void dialogDeleteSelectedImages(Context context, final List<MyFileModel> fileModels, final List<Integer> positions){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // clicked yes
                    case DialogInterface.BUTTON_POSITIVE:
                        final ProgressDialog pDialog = ProgressDialog.show(FilesActivity.this,
                                "Loading", "Deleting", true);
                        new Thread(){
                            public void run(){
                                // delete file
                                DataBaseHelper db = new DataBaseHelper(getApplicationContext());
                                List<MyFileModel> selectedFiles = new ArrayList<>();
                                for (Integer i : positions){
                                    selectedFiles.add(fileModels.get(i));
                                }
                                // remove files from allImage array
                                // delete file on disk
                                String filePath = getApplicationContext().getExternalFilesDir("folder/").getAbsolutePath();

                                for (MyFileModel fileModel : selectedFiles){
                                    allFiles.remove(fileModel);
                                    String name = String.valueOf(fileModel.getId());
                                    File deleteFile = new File(filePath, name);
                                    deleteFile.delete();
                                }

                                boolean success = db.deleteFiles(selectedFiles);
                                if(success){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            undoButtonFunction();
                                        }
                                    });
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pDialog.dismiss();
                                        fileAdapter.notifyDataSetChanged();
                                        if (allFiles.isEmpty()){
                                            empty.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                        }.start();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure? This will be permanently delete " +positions.size()+ " items.")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
        
    }
    public void dialogMoveSelectedImages(Context context, final List<MyFileModel> fileModels, final List<Integer> positions){

        List<MyFileModel> selectedFiles = new ArrayList<>();
        for (Integer i : positions){
            selectedFiles.add(fileModels.get(i));
        }

        SelectFolderFragment fragment = SelectFolderFragment.newInstance(context, selectedFiles, fileAdapter, allFiles, folderId, empty);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setEnterTransition(new Fade());
            fragment.setExitTransition(new Fade());
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.displayContainer, fragment)
                .addToBackStack(null)
                .commit();

    }
    public void dialogExportSelectedFiles(Context context, final List<MyFileModel> fileModels, final List<Integer> positions){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // clicked yes
                    case DialogInterface.BUTTON_POSITIVE:
                        List<MyFileModel> selectedFiles = new ArrayList<>();
                        for (Integer i : positions){
                            selectedFiles.add(fileModels.get(i));
                        }
                        exportFiles(selectedFiles);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure? This will export " +positions.size()+ " items.")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void exportFiles(final List<MyFileModel> fileModels){

        final ProgressDialog dialog = ProgressDialog.show(this,
                "Loading", "Exporting", true);
        new Thread(){
            public void run(){
                // export
                String sourceDir = getExternalFilesDir("folder/").getAbsolutePath();
                final int count = fileModels.size();
                for (int i=0; i<count; i++){
                    final int finalI = i;
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            String message = "Exporting " +
                                    (finalI + 1) + "/" +
                                    count;
                            dialog.setMessage(message);
                        }
                    });

                    String sourceFileName = String.valueOf(fileModels.get(i).getId());
                    File sourceFile = new File(sourceDir, sourceFileName);
                    ContentResolver resolver = getContentResolver();
                    Uri outUri = null;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        ContentValues cv = new ContentValues();
                        cv.put(MediaStore.MediaColumns.DISPLAY_NAME, fileModels.get(i).getName());
                        cv.put(MediaStore.MediaColumns.MIME_TYPE, fileModels.get(i).getFileType());

                        outUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);
                    }else{
                        File DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        String authority = getApplicationContext().getPackageName() + ".provider";
                        String fileName = fileModels.get(i).getName()+"."+fileModels.get(i).getFileType().split("/")[1];
                        File exportFile = new File(DOWNLOAD_DIR, fileName);
                        outUri = FileProvider.getUriForFile(getApplicationContext(), authority, exportFile);
                    }
                    FileInputStream fis;
                    OutputStream out;
                    try{
                        fis = new FileInputStream(sourceFile);
                        CipherInputStream cis = new CipherInputStream(fis, AES.getDecryptionCipher());
                        out = resolver.openOutputStream(outUri);

                        byte[] buffer = new byte[64 * 1024];

                        int len = 0;
                        while ((len=cis.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                        cis.close();
                        out.close();
                        fis.close();

                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        undoButtonFunction();
                    }
                });
            }
        }.start();
    }

    public void undoButtonFunction(){

        undoButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        moveButton.setVisibility(View.GONE);
        exportButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.VISIBLE);
        for (MyFileModel fileModel : allFiles){
            fileModel.setCheckBoxVisibility(false);
            fileModel.setCheckBox(false);
        }
        selecting = false;
        fileAdapter.notifyDataSetChanged();
    }



    @Override
    public void onResume() {
        super.onResume();
        AES.setAllowClearing(true);
        if(fileAdapter != null){
            fileAdapter.notifyDataSetChanged();
        }
        if(allFiles.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }
        if (AES.getSecretKey()==null){
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Glide.get(this).clearMemory();
    }
}
