package com.project.real_calculator.ui.slideshow;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.real_calculator.R;
import com.project.real_calculator.encryption.AES;
import com.project.real_calculator.encryption.Util;
import com.project.real_calculator.ui.gallery.GalleryViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Random;

public class SlideshowFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    private ImageView imgswitcher;
    private Button random;
    private int count=1;
    //store image uris in this array list
    private ArrayList<Uri> imageUris;
    //request code to pick images
    private static final int PICK_IMAGES_CODE = 0;
    //position of selected image
    int position = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //init list
        imageUris = new ArrayList<>();

        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        //init UI views
        imgswitcher = root.findViewById(R.id.imgswitcher);/*
        imgswitcher.setFactory(new ViewSwitcher.ViewFactory(){
            @Override
            public View makeView(){
                ImageView imageView = new ImageView(getActivity().getApplicationContext());
                return imageView;
            }
        });*/
        random = root.findViewById(R.id.random);
        //random button function
        random.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int size = imageUris.size()-1;
                Random rand = new Random();
                int randomNum = rand.nextInt((size - 0) + 1) + 0;
                //imgswitcher.setImageURI(imageUris.get(randomNum));
                InputStream iStream = null;
                //==convert uri to bytes
                try {
                    iStream = getActivity().getApplicationContext().getContentResolver().openInputStream(imageUris.get(randomNum));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //==encrypt

                // test save
                String dirPath = getActivity().getExternalFilesDir("media/1").getAbsolutePath();
                //String dirPath = getExternalFilesDir().getAbsolutePath() + File.separator + "testfolder";
                File newFolder = new File(dirPath);

                if (!newFolder.exists()){
                    newFolder.mkdirs();
                }

                File newFile = new File(dirPath, Integer.toString(count));

                if(!newFile.exists()){
                    try {
                        newFile.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try  {
                    FileOutputStream fOut = new FileOutputStream(newFile);
                    byte[] tmp = {3,4,5,6,7,8,0};
                    fOut.write(Util.encryptToByte(getBytes(iStream)));
                    iStream.close();
                    fOut.close();
                /*
                OutputStreamWriter outputWriter=new OutputStreamWriter(fOut);
                outputWriter.write("sadfds");
                outputWriter.close();
                */

                    //display file saved message
                    //Toast.makeText(getBaseContext(), "File saved successfully!",
                    //Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                // end test
                String dirPath2 = getActivity().getExternalFilesDir("media/1").getAbsolutePath();
                File myExternalFile = new File(dirPath2, Integer.toString(count));
                //read from disk
                //Util.decryptToByte(fullyReadFileToBytes(myExternalFile));
                //Util.decryptToByte(getBytes(new FileInputStream(myExternalFile)));
                //AES.setEncryptedBytes(new byte[0]);
                //Glide.with(getActivity().getApplicationContext()).load(inputData).into(imgswitcher)
                /*
                try {
                    Glide.with(getActivity().getApplicationContext()).load(Util.decryptToByte(getBytes(new FileInputStream(myExternalFile)))).diskCacheStrategy(DiskCacheStrategy.NONE).into(imgswitcher);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                Glide.with(getActivity())
                        .load(myExternalFile)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imgswitcher);
                count++;
                // clear heap memory to avoid crash
                //AES.setDecryptedBytes(new byte[0]);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your gallery action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                pickImagesIntent();
            }
        });

        return root;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    public byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte[] bytes = new byte[size];
        byte[] tmpBuff = new byte[size];
        FileInputStream fis= new FileInputStream(f);;
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    public static String getMimeType(Context context, Uri uri) {
        String type = context.getContentResolver().getType(uri);
        // sometimes getType returns null
        if(type == null){
            type = uri.getPath();
        }
        // split by /
        String[] arr = type.split("/");
        // look for file type key words
        for (String tmp : arr){
            switch (tmp) {
                case "images":
                case "image":
                    type = "image";
                    break;
                case "video":
                    type = "video";
                    break;
            }
        }
        return type;
    }

    public void pickImagesIntent(){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_CODE){
            if (resultCode == Activity.RESULT_OK){
                if (data.getClipData() != null){
                    //picked multiple images
                    int cout = data.getClipData().getItemCount();
                    for (int i=0; i<cout; i++){
                        //get image uri at specific index
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                        Log.d("type",getMimeType(getActivity().getApplicationContext(), imageUri));
                    }
                    imgswitcher.setImageURI(imageUris.get(0));
                    position = 0;

                }
                else{
                    //picked one image
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                    // set image to something or save it to db
                    Log.d("pickedimg","pickedimg");
                    position = 0;
                }
            }
        }
    }
}