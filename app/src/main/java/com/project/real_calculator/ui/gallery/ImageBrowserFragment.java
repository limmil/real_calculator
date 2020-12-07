package com.project.real_calculator.ui.gallery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.project.real_calculator.R;
import com.project.real_calculator.database.DataBaseHelper;
import com.project.real_calculator.database.models.AlbumModel;
import com.project.real_calculator.database.models.PhotoModel;
import com.project.real_calculator.encryption.AES;
import com.project.real_calculator.interfaces.IImageIndicatorListener;
import com.project.real_calculator.ui.gallery.utils.PhotoAdapter;
import com.project.real_calculator.ui.gallery.utils.RecyclerViewPagerImageIndicator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.CipherInputStream;

import static androidx.core.view.ViewCompat.setTransitionName;
import static java.lang.Thread.sleep;

public class ImageBrowserFragment extends Fragment implements IImageIndicatorListener {
    private List<PhotoModel> allImages = new ArrayList<>();
    private int position;
    private Context animeContx;
    private ImageView image;
    private ImageButton playButton;
    private ViewPager imagePager;
    private CardView header;
    private ImageButton backButton, menuButton;
    private RecyclerView indicatorRecycler;
    private int viewVisibilityController;
    private int viewVisibilitylooper;
    private ImagesPagerAdapter pagingImages;
    private RecyclerViewPagerImageIndicator indicatorAdapter;
    private PhotoAdapter photoAdapter;
    private int previousSelected = -1;

    public ImageBrowserFragment(){

    }

    public ImageBrowserFragment(List<PhotoModel> allImages, int imagePosition, Context anim, PhotoAdapter photoAdapter) {
        this.allImages = allImages;
        this.position = imagePosition;
        this.animeContx = anim;
        this.photoAdapter = photoAdapter;
    }

    public static ImageBrowserFragment newInstance(List<PhotoModel> allImages, int imagePosition, Context anim, PhotoAdapter photoAdapter) {
        ImageBrowserFragment fragment = new ImageBrowserFragment(allImages,imagePosition,anim,photoAdapter);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_browser, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(allImages.isEmpty())

        /**
         * initialisation of the recyclerView visibility control integers
         */
        viewVisibilityController = 0;
        viewVisibilitylooper = 0;

        /**
         * setting up the viewPager with images
         */
        imagePager = view.findViewById(R.id.imagePager);
        pagingImages = new ImagesPagerAdapter();
        imagePager.setAdapter(pagingImages);
        imagePager.setOffscreenPageLimit(1);
        imagePager.setCurrentItem(position);//displaying the image at the current position passed by the ImageBrowse Activity


        /**
         * setting up the recycler view indicator for the viewPager
         */
        indicatorRecycler = view.findViewById(R.id.indicatorRecycler);
        indicatorRecycler.hasFixedSize();
        indicatorRecycler.setLayoutManager(new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false));
        indicatorAdapter = new RecyclerViewPagerImageIndicator(allImages,getContext(),this);
        indicatorRecycler.setAdapter(indicatorAdapter);

        //adjusting the recyclerView indicator to the current position of the viewPager, also highlights the image in recyclerView with respect to the
        //viewPager's position

        // random crash if allImages size went to 0
        if(allImages.size()==0){requireActivity().finish();}

        allImages.get(position).setSelected(true);
        previousSelected = position;
        indicatorAdapter.notifyDataSetChanged();
        indicatorRecycler.scrollToPosition(position);

        header = view.findViewById(R.id.imageHead);
        backButton = view.findViewById(R.id.iBrowseBack);
        menuButton = view.findViewById(R.id.imageMenu);

        /**
         * set header button controls
         */
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.album_popup, popup.getMenu());
                popup.setForceShowIcon(true);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final int delete = R.id.action_delete;
                        final int edit = R.id.action_edit;
                        switch (item.getItemId()){
                            case delete:
                                // delete button clicked
                                dialogDeleteCurrentImage(getContext(),allImages.get(position));
                                break;
                            case edit:
                                // edit button clicked
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });


        /**
         * this listener controls the visibility of the recyclerView
         * indication and its current position in respect to the image ViewPager
         */
        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // update the viewing position
                ImageBrowserFragment.this.position = position;
                Log.d("adsf","asdf "+allImages.get(position).getId());
            }

            @Override
            public void onPageSelected(int position) {
                if(previousSelected != -1){
                    if(previousSelected < allImages.size()){
                        allImages.get(previousSelected).setSelected(false);
                    }
                    previousSelected = position;
                    allImages.get(position).setSelected(true);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                    indicatorRecycler.scrollToPosition(position);
                }else{
                    previousSelected = position;
                    allImages.get(position).setSelected(true);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                    indicatorRecycler.scrollToPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        indicatorRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 *  uncomment the below condition to control recyclerView visibility automatically
                 *  when image is clicked also uncomment the condition set on the image's onClickListener in the ImagesPagerAdapter adapter
                 */
                /*if(viewVisibilityController == 0){
                    indicatorRecycler.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    visibiling();
                }else{
                    viewVisibilitylooper++;
                }*/
                return false;
            }
        });

    }


    /**
     * this method of the imageIndicatorListener interface helps in communication between the fragment and the recyclerView Adapter
     * each time an item in the adapter is clicked the position of that item is communicated in the fragment and the position of the
     * viewPager is adjusted as follows
     * @param ImagePosition The position of an image item in the RecyclerView Adapter
     */
    @Override
    public void onImageIndicatorClicked(int ImagePosition) {

        //the below lines of code highlights the currently select image in  the indicatorRecycler with respect to the viewPager position
        if(previousSelected != -1){
            if (previousSelected<allImages.size() && previousSelected!=ImagePosition){
                allImages.get(previousSelected).setSelected(false);
            }
            previousSelected = ImagePosition;
            indicatorRecycler.getAdapter().notifyDataSetChanged();
        }else{
            previousSelected = ImagePosition;
        }

        imagePager.setCurrentItem(ImagePosition);
    }

    /**
     * the imageViewPager's adapter
     */
    private class ImagesPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return allImages.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup containerCollection, final int position) {
            LayoutInflater layoutinflater = (LayoutInflater) containerCollection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutinflater.inflate(R.layout.fragment_gallery_browser_pager,null);
            final ProgressBar loadImageProgress = view.findViewById(R.id.loadingImageProgress);
            image = view.findViewById(R.id.image);
            playButton = view.findViewById(R.id.playButton);

            setTransitionName(image, String.valueOf(position)+"picture");

            String[] arr = allImages.get(position).getFileType().split("/");
            File myExternalFile;
            int id = allImages.get(position).getId();
            String fileType = "N/A";
            String fileExtension = "N/A";
            if (arr.length==2){
                fileType = arr[0];
                fileExtension = arr[1];
                if (fileType.equals("image")){
                    String imageDir = animeContx.getExternalFilesDir("media").getAbsolutePath();
                    myExternalFile = new File(imageDir, String.valueOf(id));
                } else if (fileType.equals("video")){
                    playButton.setVisibility(View.VISIBLE);
                    String imageDir = animeContx.getExternalFilesDir("media/t").getAbsolutePath();
                    myExternalFile = new File(imageDir, String.valueOf(id));
                } else{
                    String imageDir = animeContx.getExternalFilesDir("media/t").getAbsolutePath();
                    myExternalFile = new File(imageDir, String.valueOf(id));
                }
            } else{
                String imageDir = animeContx.getExternalFilesDir("media").getAbsolutePath();
                myExternalFile = new File(imageDir, String.valueOf(id));
            }


            Glide.with(animeContx)
                    .load(myExternalFile)
                    .apply(new RequestOptions().fitCenter())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .listener(new RequestListener<Drawable>(){
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {
                            loadImageProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource,
                                                       Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            loadImageProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(indicatorRecycler.getVisibility() == View.GONE){
                        indicatorRecycler.setVisibility(View.VISIBLE);
                        header.setVisibility(View.VISIBLE);
                    }else{
                        indicatorRecycler.setVisibility(View.GONE);
                        header.setVisibility(View.GONE);
                    }

                    /**
                     * uncomment the below condition and comment the one above to control recyclerView visibility automatically
                     * when image is clicked
                     */
                    /*if(viewVisibilityController == 0){
                        indicatorRecycler.setVisibility(View.VISIBLE);
                        header.setVisibility(View.VISIBLE);
                        visibiling();
                    }else{
                        viewVisibilitylooper++;
                    }*/



                }
            });


            final String finalFileExtension = fileExtension.toLowerCase();
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glide.get(requireActivity()).clearMemory();
                    // get the file and decrypt in onto disk
                    String sourceVideoDir = animeContx.getExternalFilesDir("media").getAbsolutePath();
                    String sourceFileName = String.valueOf(allImages.get(position).getId());
                    final File sourceVideoFile = new File(sourceVideoDir, sourceFileName);

                    String tempFileDir = animeContx.getFilesDir().getAbsolutePath();
                    final File tempVideoFile = new File(tempFileDir, "temp." + finalFileExtension);

                    final ProgressDialog dialog = ProgressDialog.show(getActivity(),
                            "Loading", "Decrypting video", true);

                    new Thread() {
                        public void run() {
                            FileInputStream fis;
                            FileOutputStream out;
                            try {
                                fis = new FileInputStream(sourceVideoFile);
                                CipherInputStream cis = new CipherInputStream(fis, AES.getDecryptionCipher());
                                out = new FileOutputStream(tempVideoFile);

                                byte[] buffer = new byte[1024 * 1024];

                                int len = 0;
                                while ((len=cis.read(buffer)) != -1) {
                                    out.write(buffer, 0, len);
                                }
                                // fast but uses too much memory
                                //out.write(Util.decryptToByte(getBytes(fis)));
                                cis.close();
                                out.close();
                                fis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Intent move = new Intent(getActivity(), VideoPlayerActivity.class);
                            move.putExtra("tempVideoFile", tempVideoFile.getAbsolutePath());

                            startActivity(move);

                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }.start();

                }
            });



            ((ViewPager) containerCollection).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup containerCollection, int position, Object view) {
            ((ViewPager) containerCollection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object){
            return PagerAdapter.POSITION_NONE;
        }
    }

    /**
     * function for controlling the visibility of the recyclerView indicator
     */
    private void visibiling(){
        viewVisibilityController = 1;
        final int checker = viewVisibilitylooper;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(viewVisibilitylooper > checker){
                    visibiling();
                }else{
                    indicatorRecycler.setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                    viewVisibilityController = 0;

                    viewVisibilitylooper = 0;
                }
            }
        }, 4000);
    }

    // delete one image
    public void dialogDeleteCurrentImage(Context context, final PhotoModel photoModel){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    // clicked yes
                    case DialogInterface.BUTTON_POSITIVE:
                        // delete photo
                        DataBaseHelper db = new DataBaseHelper(getActivity());
                        boolean success = db.deletePhoto(photoModel);
                        // remove album from albums array
                        if(success){
                            imagePager.setAdapter(null);
                            allImages.remove(photoModel);
                            pagingImages = new ImagesPagerAdapter();
                            imagePager.setAdapter(pagingImages);
                            pagingImages.notifyDataSetChanged();
                            imagePager.setOffscreenPageLimit(1);
                            imagePager.setCurrentItem(position);
                            indicatorAdapter.notifyDataSetChanged();
                            photoAdapter.notifyDataSetChanged();
                            if (position<allImages.size()){
                                allImages.get(position).setSelected(true);
                            }
                            // delete file on disk
                            String filePath = getActivity().getApplicationContext().getExternalFilesDir("media/").getAbsolutePath();
                            String thumbPath = getActivity().getApplicationContext().getExternalFilesDir("media/t").getAbsolutePath();
                            String name = String.valueOf(photoModel.getId());
                            File deleteFile = new File(filePath, name);
                            File deleteThumb = new File(thumbPath, name);
                            deleteFile.delete();
                            deleteThumb.delete();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure? This will be permanently deleted.")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        int len = 0;
        while ((len=inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }



    @Override
    public void onPause() {
        super.onPause();
        if(position<allImages.size()){
            allImages.get(position).setSelected(false);
        }
        Glide.get(requireContext()).clearMemory();
    }
}
