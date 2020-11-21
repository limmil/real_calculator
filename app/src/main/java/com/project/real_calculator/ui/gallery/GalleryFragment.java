package com.project.real_calculator.ui.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.project.real_calculator.R;

import java.util.ArrayList;
import java.util.Random;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    private ImageSwitcher imgswitcher;
    private Button random;

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
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        //init UI views
        imgswitcher = root.findViewById(R.id.imgswitcher);
        imgswitcher.setFactory(new ViewSwitcher.ViewFactory(){
            @Override
            public View makeView(){
                ImageView imageView = new ImageView(getActivity().getApplicationContext());
                return imageView;
            }
        });
        random = root.findViewById(R.id.random);
        //random button function
        random.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int size = imageUris.size()-1;
                Random rand = new Random();
                int randomNum = rand.nextInt((size - 0) + 1) + 0;
                imgswitcher.setImageURI(imageUris.get(randomNum));
            }
        });

        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
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

    public void pickImagesIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
                        Log.d("pickedimgs","pickedimgs"+imageUri);
                    }
                    // set image to something or save it to db
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