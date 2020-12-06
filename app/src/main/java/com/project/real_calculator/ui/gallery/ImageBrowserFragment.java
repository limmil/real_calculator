package com.project.real_calculator.ui.gallery;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.project.real_calculator.R;
import com.project.real_calculator.database.models.PhotoModel;
import com.project.real_calculator.encryption.AES;
import com.project.real_calculator.interfaces.IImageIndicatorListener;
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
    private RecyclerView indicatorRecycler;
    private int viewVisibilityController;
    private int viewVisibilitylooper;
    private ImagesPagerAdapter pagingImages;
    private int previousSelected = -1;

    public ImageBrowserFragment(){

    }

    public ImageBrowserFragment(List<PhotoModel> allImages, int imagePosition, Context anim) {
        this.allImages = allImages;
        this.position = imagePosition;
        this.animeContx = anim;
    }

    public static ImageBrowserFragment newInstance(List<PhotoModel> allImages, int imagePosition, Context anim) {
        ImageBrowserFragment fragment = new ImageBrowserFragment(allImages,imagePosition,anim);
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
        imagePager.setCurrentItem(position);//displaying the image at the current position passed by the ImageDisplay Activity


        /**
         * setting up the recycler view indicator for the viewPager
         */
        indicatorRecycler = view.findViewById(R.id.indicatorRecycler);
        indicatorRecycler.hasFixedSize();
        indicatorRecycler.setLayoutManager(new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false));
        RecyclerView.Adapter indicatorAdapter = new RecyclerViewPagerImageIndicator(allImages,getContext(),this);
        indicatorRecycler.setAdapter(indicatorAdapter);

        //adjusting the recyclerView indicator to the current position of the viewPager, also highlights the image in recyclerView with respect to the
        //viewPager's position
        // random crash if allImages size went to 0
        if(allImages.size()==0){requireActivity().finish();}

        allImages.get(position).setSelected(true);
        previousSelected = position;
        indicatorAdapter.notifyDataSetChanged();
        indicatorRecycler.scrollToPosition(position);


        /**
         * this listener controls the visibility of the recyclerView
         * indication and it current position in respect to the image ViewPager
         */
        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(previousSelected != -1){
                    allImages.get(previousSelected).setSelected(false);
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
                    visibiling();
                }else{
                    viewVisibilitylooper++;
                }*/
                return false;
            }
        });

    }


    /**
     * this method of the imageIndicatorListerner interface helps in communication between the fragment and the recyclerView Adapter
     * each time an iten in the adapter is clicked the position of that item is communicated in the fragment and the position of the
     * viewPager is adjusted as follows
     * @param ImagePosition The position of an image item in the RecyclerView Adapter
     */
    @Override
    public void onImageIndicatorClicked(int ImagePosition) {

        //the below lines of code highlights the currently select image in  the indicatorRecycler with respect to the viewPager position
        if(previousSelected != -1){
            allImages.get(previousSelected).setSelected(false);
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
                    .thumbnail(Glide.with(animeContx).load(R.drawable.spin))
                    .apply(new RequestOptions().fitCenter())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(indicatorRecycler.getVisibility() == View.GONE){
                        indicatorRecycler.setVisibility(View.VISIBLE);
                    }else{
                        indicatorRecycler.setVisibility(View.GONE);
                    }

                    /**
                     * uncomment the below condition and comment the one above to control recyclerView visibility automatically
                     * when image is clicked
                     */
                    /*if(viewVisibilityController == 0){
                        indicatorRecycler.setVisibility(View.VISIBLE);
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
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // TODO: progress dialog
                    // get the file and decrypt in onto disk
                    String sourceVideoDir = animeContx.getExternalFilesDir("media").getAbsolutePath();
                    String sourceFileName = String.valueOf(allImages.get(position).getId());
                    final File sourceVideoFile = new File(sourceVideoDir, sourceFileName);

                    String tempFileDir = animeContx.getFilesDir().getAbsolutePath();
                    final File tempVideoFile = new File(tempFileDir, "temp." + finalFileExtension);

                    final ProgressDialog dialog = ProgressDialog.show(getActivity(),
                            "Loading", "Decrypting video.", true);

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
    }

    /**
     * function for controlling the visibility of the recyclerView indicator
     */
    private void visibiling(){
        viewVisibilityController = 1;
        final int checker = viewVisibilitylooper;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(viewVisibilitylooper > checker){
                    visibiling();
                }else{
                    indicatorRecycler.setVisibility(View.GONE);
                    viewVisibilityController = 0;

                    viewVisibilitylooper = 0;
                }
            }
        }, 4000);
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
        Glide.get(requireContext()).clearMemory();
    }
}
