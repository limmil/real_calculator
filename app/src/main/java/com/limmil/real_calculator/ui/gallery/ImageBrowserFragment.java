package com.limmil.real_calculator.ui.gallery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import com.github.chrisbanes.photoview.PhotoView;
import com.limmil.real_calculator.R;
import com.limmil.real_calculator.database.DataBaseHelper;
import com.limmil.real_calculator.database.models.PhotoModel;
import com.limmil.real_calculator.encryption.AES;
import com.limmil.real_calculator.encryption.EncryptedFileObject;
import com.limmil.real_calculator.interfaces.IImageIndicatorListener;
import com.limmil.real_calculator.ui.gallery.utils.PhotoAdapter;
import com.limmil.real_calculator.ui.gallery.utils.RecyclerViewPagerImageIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.view.ViewCompat.setTransitionName;

public class ImageBrowserFragment extends Fragment implements IImageIndicatorListener {
    private List<PhotoModel> allImages = new ArrayList<>();
    private int position;
    private Context animeContx;
    private PhotoView image;
    private ImageButton playButton;
    private ViewPager imagePager;
    private CardView header;
    private ImageButton backButton, deleteButton;
    private RecyclerView indicatorRecycler;
    private int viewVisibilityController;
    private int viewVisibilitylooper;
    private ImagesPagerAdapter pagingImages;
    private RecyclerViewPagerImageIndicator indicatorAdapter;
    private PhotoAdapter photoAdapter;
    private RecyclerView imageRecycler;
    private int previousSelected = -1;

    public ImageBrowserFragment(){

    }

    public ImageBrowserFragment(List<PhotoModel> allImages, int imagePosition, Context anim, PhotoAdapter photoAdapter, RecyclerView imageRecycler) {
        this.allImages = allImages;
        this.position = imagePosition;
        this.animeContx = anim;
        this.photoAdapter = photoAdapter;
        this.imageRecycler =  imageRecycler;
    }

    public static ImageBrowserFragment newInstance(List<PhotoModel> allImages, int imagePosition, Context anim, PhotoAdapter photoAdapter, RecyclerView imageRecycler) {
        ImageBrowserFragment fragment = new ImageBrowserFragment(allImages,imagePosition,anim,photoAdapter,imageRecycler);
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
        deleteButton = view.findViewById(R.id.imageDelete);

        /**
         * set header button controls
         */
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allImages.isEmpty()){
                    dialogDeleteCurrentImage(getContext(),allImages.get(position));
                }
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

            final PhotoModel photoModel = allImages.get(position);

            View view = layoutinflater.inflate(R.layout.fragment_gallery_browser_pager,null);
            final ProgressBar loadImageProgress = view.findViewById(R.id.loadingImageProgress);
            image = view.findViewById(R.id.image);
            playButton = view.findViewById(R.id.playButton);

            setTransitionName(image, String.valueOf(position)+"picture");

            String[] arr = photoModel.getFileType().split("/");
            File myExternalFile;
            int id = photoModel.getId();
            String fileType = "N/A";
            byte[] nonce = new byte[0];
            if (arr.length==2){
                fileType = arr[0];
                if (fileType.equals("image")){
                    String imageDir = animeContx.getExternalFilesDir("media").getAbsolutePath();
                    myExternalFile = new File(imageDir, String.valueOf(id));
                    nonce = photoModel.getContentIv();
                } else if (fileType.equals("video")){
                    playButton.setVisibility(View.VISIBLE);
                    String imageDir = animeContx.getExternalFilesDir("media/t").getAbsolutePath();
                    myExternalFile = new File(imageDir, String.valueOf(id));
                    nonce = photoModel.getThumbIv();
                } else{
                    String imageDir = animeContx.getExternalFilesDir("media/t").getAbsolutePath();
                    myExternalFile = new File(imageDir, String.valueOf(id));
                    nonce = photoModel.getThumbIv();
                }
            } else{
                String imageDir = animeContx.getExternalFilesDir("media").getAbsolutePath();
                myExternalFile = new File(imageDir, String.valueOf(id));
                nonce = photoModel.getContentIv();
            }


            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (indicatorRecycler.getVisibility() == View.GONE) {
                        indicatorRecycler.setVisibility(View.VISIBLE);
                        header.setVisibility(View.VISIBLE);
                    } else {
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

            if(myExternalFile.exists()) {
                EncryptedFileObject efo = new EncryptedFileObject(myExternalFile, nonce);
                Glide.with(animeContx)
                        .load(efo)
                        .apply(new RequestOptions().fitCenter())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_baseline_image)
                        .skipMemoryCache(true)
                        .listener(new RequestListener<Drawable>() {
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


            }else{
                Glide.with(animeContx)
                        .load(R.drawable.ic_baseline_broken_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(image);
            }

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glide.get(requireActivity()).clearMemory();
                    // get the file and decrypt in onto disk
                    String sourceVideoDir = animeContx.getExternalFilesDir("media").getAbsolutePath();
                    String sourceFileName = String.valueOf(photoModel.getId());
                    final File sourceVideoFile = new File(sourceVideoDir, sourceFileName);

                    Intent move = new Intent(getActivity(), VideoPlayerActivity.class);
                    move.putExtra("sourceVideoPath", sourceVideoFile.getAbsolutePath());
                    AES.setIV(photoModel.getContentIv());

                    startActivity(move);
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
                        // delete file on disk
                        String filePath = requireActivity().getApplicationContext().getExternalFilesDir("media/").getAbsolutePath();
                        String thumbPath = requireActivity().getApplicationContext().getExternalFilesDir("media/t").getAbsolutePath();
                        String name = String.valueOf(photoModel.getId());
                        File deleteFile = new File(filePath, name);
                        File deleteThumb = new File(thumbPath, name);
                        deleteFile.delete();
                        deleteThumb.delete();
                        // delete photo in database
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

                            if(allImages.isEmpty()){
                                requireActivity().onBackPressed();
                                ((ImageBrowseActivity)requireActivity()).empty.setVisibility(View.VISIBLE);
                            }
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



    @Override
    public void onPause() {
        super.onPause();
        if(position<allImages.size()){
            allImages.get(position).setSelected(false);
        }
        Glide.get(requireContext()).clearMemory();
        imageRecycler.scrollToPosition(position);
    }
}