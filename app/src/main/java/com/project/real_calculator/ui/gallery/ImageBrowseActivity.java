package com.project.real_calculator.ui.gallery;import android.app.Activity;import android.app.AlertDialog;import android.app.ProgressDialog;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.content.res.AssetFileDescriptor;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.graphics.Canvas;import android.graphics.Color;import android.graphics.Paint;import android.graphics.PorterDuff;import android.graphics.PorterDuffXfermode;import android.graphics.drawable.Drawable;import android.media.MediaMetadataRetriever;import android.net.Uri;import android.os.Build;import android.os.Bundle;import android.transition.Fade;import android.view.MenuInflater;import android.view.MenuItem;import android.view.View;import android.view.WindowManager;import android.widget.Button;import android.widget.ImageButton;import android.widget.PopupMenu;import android.widget.ProgressBar;import android.widget.TextView;import androidx.annotation.Nullable;import androidx.appcompat.app.AppCompatActivity;import androidx.core.content.ContextCompat;import androidx.core.graphics.drawable.DrawableCompat;import androidx.recyclerview.widget.RecyclerView;import com.bumptech.glide.Glide;import com.bumptech.glide.load.engine.DiskCacheStrategy;import com.bumptech.glide.request.RequestOptions;import com.google.android.material.floatingactionbutton.FloatingActionButton;import com.project.real_calculator.R;import com.project.real_calculator.database.DataBaseHelper;import com.project.real_calculator.database.models.AlbumModel;import com.project.real_calculator.database.models.PhotoModel;import com.project.real_calculator.encryption.AES;import com.project.real_calculator.encryption.Util;import com.project.real_calculator.interfaces.IClickListener;import com.project.real_calculator.ui.gallery.utils.MarginDecoration;import com.project.real_calculator.ui.gallery.utils.PhotoAdapter;import com.project.real_calculator.ui.gallery.utils.PicHolder;import java.io.ByteArrayOutputStream;import java.io.File;import java.io.FileInputStream;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import java.io.OutputStream;import java.util.ArrayList;import java.util.List;import java.util.Locale;import java.util.concurrent.ExecutionException;import java.util.concurrent.TimeUnit;import javax.crypto.CipherInputStream;public class ImageBrowseActivity extends AppCompatActivity implements IClickListener {    //request code to pick images    private static final int PICK_IMAGES_CODE = 0;    //store image uris in this array list    private ArrayList<Uri> imageUris;    RecyclerView imageRecycler;    PhotoAdapter photoAdapter;    List<PhotoModel> allImages;    ProgressBar load;    int albumId;    TextView albumName;    ImageButton backButton, menuButton, deleteButton, moveButton, exportButton;    Button undoButton;    boolean selecting;    TextView empty;    DataBaseHelper db;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        db = new DataBaseHelper(getApplicationContext());        // prevents Android taking a screenshot when app goes to the background        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,                WindowManager.LayoutParams.FLAG_SECURE);        setContentView(R.layout.activity_image_browse);        imageUris = new ArrayList<>();        selecting = false;        setupButtons();        empty = findViewById(R.id.iEmpty);        albumName = findViewById(R.id.imageAlbumName);        String name = getIntent().getStringExtra("albumName");        albumId = getIntent().getIntExtra("albumId", 0);        albumName.setText(name);        allImages = new ArrayList<>();        imageRecycler = findViewById(R.id.imageRecycler);        imageRecycler.addItemDecoration(new MarginDecoration(this));        imageRecycler.hasFixedSize();        load = findViewById(R.id.loader);        load.setVisibility(View.VISIBLE);        DataBaseHelper db = new DataBaseHelper(this);        // db.getPhotosFromAlbum only needs the id field        allImages = db.getPhotosFromAlbum(new AlbumModel(albumId, name,"",0));        photoAdapter = new PhotoAdapter(allImages,ImageBrowseActivity.this,this);        imageRecycler.setAdapter(photoAdapter);        load.setVisibility(View.GONE);        if (allImages.isEmpty()){            empty.setVisibility(View.VISIBLE);        }        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.imagefab);        fab.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                if(!selecting) {                    pickImagesIntent();                }            }        });    }    /**     *     * @param holder The ViewHolder for the clicked picture     * @param position The position in the grid of the picture that was clicked     * @param pics An ArrayList of all the items in the Adapter     */    @Override    public void onPicClicked(PicHolder holder, int position, List<PhotoModel> pics) {        if(!selecting) {            ImageBrowserFragment browser = ImageBrowserFragment.newInstance(pics, position, ImageBrowseActivity.this, photoAdapter);            // Note that we need the API version check here because the actual transition classes (e.g. Fade)            // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment            // ARE available in the support library (though they don't do anything on API < 21)            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {                //browser.setEnterTransition(new Slide());                //browser.setExitTransition(new Slide()); uncomment this to use slide transition and comment the two lines below                browser.setEnterTransition(new Fade());                browser.setExitTransition(new Fade());            }            getSupportFragmentManager()                    .beginTransaction()                    .addSharedElement(holder.picture, position + "picture")                    .add(R.id.displayContainer, browser)                    .addToBackStack(null)                    .commit();        }else{            // select images            holder.checkBox.setChecked(!pics.get(position).getCheckBox());            pics.get(position).setCheckBox(!pics.get(position).getCheckBox());        }    }    @Override    public void onPicClicked(AlbumModel album) {    }    @Override    public void onPicHeld(AlbumModel album, View view, int position) {    }    public void pickImagesIntent(){        Intent intent = new Intent();        intent.setType("*/*");        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);        String[] mimeTypes = {"image/*", "video/*"};        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);        intent.setAction(Intent.ACTION_GET_CONTENT);        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE);    }    @Override    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data){        super.onActivityResult(requestCode, resultCode, data);        if (requestCode == PICK_IMAGES_CODE){            if (resultCode == Activity.RESULT_OK){                final ProgressDialog dialog = ProgressDialog.show(this,                        "Loading", "Encrypting", true);                new Thread() {                    public void run() {                        if (data.getClipData() != null){                            // picked multiple images                            int cout = data.getClipData().getItemCount();                            final int startRange = allImages.size();                            for (int i=0; i<cout; i++){                                // update dialog                                final int finalI = i;                                final int finalCout = cout;                                runOnUiThread(new Runnable(){                                    @Override                                    public void run(){                                        String message = "Encrypting " +                                                (finalI + 1) + "/" +                                                finalCout;                                        dialog.setMessage(message);                                    }                                });                                // get image uri at specific index                                Uri imageUri = data.getClipData().getItemAt(i).getUri();                                try {                                    saveFileFromURI(imageUri);                                } catch (IOException | ExecutionException | InterruptedException e) {                                    e.printStackTrace();                                }                            }                            final int endRange = allImages.size();                            runOnUiThread(new Runnable() {                                @Override                                public void run() {                                    photoAdapter.notifyItemRangeChanged(startRange, endRange);                                }                            });                        }else{                            // picked one image                            Uri imageUri = data.getData();                            try {                                saveFileFromURI(imageUri);                            } catch (IOException | ExecutionException | InterruptedException e) {                                //e.printStackTrace();                            }                            runOnUiThread(new Runnable() {                                @Override                                public void run() {                                    photoAdapter.notifyItemChanged(allImages.size());                                }                            });                        }                        runOnUiThread(new Runnable() {                            @Override                            public void run() {                                dialog.dismiss();                                empty.setVisibility(View.GONE);                            }                        });                        //dialog.dismiss();                    }                }.start();            }        }    }    public void saveFileFromURI(Uri uri) throws IOException, ExecutionException, InterruptedException {        String newFileType = getMimeType(getApplicationContext(), uri);        PhotoModel saveFile = new PhotoModel(0,"N/A","N/A","N/A","N/A","N/A",0);        AlbumModel intoAlbum = new AlbumModel(albumId,"","",0);        int id = (int) db.addPhoto(saveFile, intoAlbum);        String storageFileName = String.valueOf(id);        String fileTypeExtension = "N/A";        // encrypt and save thumbnail        String thumbPath = getApplicationContext().getExternalFilesDir("media/t").getAbsolutePath();        //--String tempFileDir = getApplicationContext().getFilesDir().getAbsolutePath();        //--File tempFile = new File(tempFileDir, "temp");        //--copyFileToInternal(tempFile, uri);        // make thumbnail        Bitmap thumbnail;        ByteArrayOutputStream thumbStream = new ByteArrayOutputStream();        try {            thumbnail = Glide.with(getApplicationContext())                    .asBitmap()                    .load(uri)                    .apply(new RequestOptions().override(300, 100))                    .skipMemoryCache(true)                    .diskCacheStrategy(DiskCacheStrategy.NONE)                    .submit()                    .get();        } catch (Exception e){            thumbnail = getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_baseline_image);        }        if (newFileType.equals("image")){            /*            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){                thumbnail = ThumbnailUtils.createImageThumbnail(tempFile.getAbsolutePath(),                        MediaStore.Images.Thumbnails.MINI_KIND);            } else{                thumbnail = ThumbnailUtils.createImageThumbnail(                        tempFile,                        new Size(150,150),                        null);            }            */            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, thumbStream);            thumbnail.recycle();            fileTypeExtension = getContentResolver().getType(uri);        } else if (newFileType.equals("video")){            String[] videoInfo = getVideoInfo(uri);/*            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){                thumbnail = ThumbnailUtils.createVideoThumbnail(tempFile.getAbsolutePath(),                        MediaStore.Images.Thumbnails.MINI_KIND);            } else {                thumbnail = ThumbnailUtils.createVideoThumbnail(                        tempFile,                        new Size(150, 150),                        null);            }*/            Bitmap out = putOverlay(thumbnail, videoInfo[1]);            out.compress(Bitmap.CompressFormat.PNG, 100, thumbStream);            thumbnail.recycle();            out.recycle();            fileTypeExtension = videoInfo[0];        } else{            Bitmap out = BitmapFactory.decodeResource( getResources(), R.drawable.cat);            out.compress(Bitmap.CompressFormat.PNG, 100, thumbStream);            out.recycle();        }        // save and encrypt thumbnail        File newThumb = new File(thumbPath, storageFileName);        FileOutputStream thumbOut = new FileOutputStream(newThumb);        thumbOut.write(Util.encryptToByte(thumbStream.toByteArray()));        thumbStream.close();        thumbOut.close();        // thumbnail file checksum        FileInputStream fis = new FileInputStream(newThumb);        String thumbHash = Util.md5(fis);        fis.close();        // encrypt and save image        String imagePath = getApplicationContext().getExternalFilesDir("media/").getAbsolutePath();        InputStream imageStream = getApplicationContext().getContentResolver().openInputStream(uri);        File newImage = new File(imagePath, storageFileName);        FileOutputStream imageOut = new FileOutputStream(newImage);        // file maybe too large to fit in memory for encryption        // Get length of file in MB        AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(uri,"r");        long fileSize = afd.getLength();        afd.close();        long fileSizeInMB = fileSize / (1024*1024);        if (fileSizeInMB > 100){            // slower but less memory cost            CipherInputStream cis = new CipherInputStream(imageStream, AES.getEncryptionCipher());            byte[] buffer = new byte[64*1024];            int len = 0;            while ((len=cis.read(buffer)) != -1) {                imageOut.write(buffer, 0, len);            }            cis.close();        }else {            // faster but more memory cost            imageOut.write(Util.encryptToByte(getBytes(imageStream)));        }        imageStream.close();        imageOut.close();        // file checksum        FileInputStream in = new FileInputStream(newImage);        String imageHash = Util.md5(in);        in.close();        // update saveFile        saveFile.setId(id);        saveFile.setAlbum(albumId);        saveFile.setName("IMG" + id);        saveFile.setThumbnail(thumbHash);        saveFile.setFileType(fileTypeExtension);        saveFile.setContent(imageHash);        db.updatePhoto(saveFile);        // for photoAdapter.notifyDataSetChanged();        allImages.add(saveFile);        //--tempFile.delete();    }    /**     *     * @param uri video file     * @return result[0]=file type, result[1]=video play time     */    public String[] getVideoInfo(Uri uri){        String[] result = new String[]{"N/A","N/A"};        try {            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();            metaRetriever.setDataSource(getApplicationContext(),uri);            result[0] = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);            String time = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);            long timeInMillisecond = Long.parseLong(time);            result[1] = String.format(Locale.US, "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeInMillisecond),                    TimeUnit.MILLISECONDS.toMinutes(timeInMillisecond) % TimeUnit.HOURS.toMinutes(1),                    TimeUnit.MILLISECONDS.toSeconds(timeInMillisecond) % TimeUnit.MINUTES.toSeconds(1));        }catch (Exception e){            e.printStackTrace();        }        return result;    }    public void copyFileToInternal(File file, Uri uri) throws IOException {        InputStream is = getApplicationContext().getContentResolver().openInputStream(uri);        OutputStream os = new FileOutputStream(file);        byte[] buffer = new byte[64 * 1024];        int len;        while ((len=is.read(buffer)) > 0){            os.write(buffer, 0, len);        }        os.close();    }    public byte[] getBytes(InputStream inputStream) throws IOException {        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();        byte[] buffer = new byte[64 * 1024];        int len = 0;        while ((len=inputStream.read(buffer)) != -1) {            byteBuffer.write(buffer, 0, len);        }        return byteBuffer.toByteArray();    }    public static String getMimeType(Context context, Uri uri) {        String type = context.getContentResolver().getType(uri);        // sometimes getType returns null        if(type == null){            type = uri.getPath();        }        // split by /        String[] arr = type.split("/");        // look for file type key words        for (String tmp : arr){            switch (tmp) {                case "images":                case "image":                    type = "image";                    break;                case "video":                    type = "video";                    break;            }        }        return type;    }    public Bitmap putOverlay(Bitmap bmp1, String timer) {        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), Bitmap.Config.ARGB_8888);        Canvas canvas = new Canvas(bmOverlay);        Paint paint = new Paint();        paint.setColor(Color.WHITE); // Text Color        paint.setTextSize(12); // Text Size        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern        // some more settings...        canvas.drawBitmap(bmp1, 0, 0, paint);        canvas.drawText(timer, 10, 10, paint);        return bmOverlay;    }    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {        Drawable drawable = ContextCompat.getDrawable(context, drawableId);        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {            drawable = (DrawableCompat.wrap(drawable)).mutate();        }        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);        Canvas canvas = new Canvas(bitmap);        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());        drawable.draw(canvas);        return bitmap;    }    public void setupButtons(){        backButton = findViewById(R.id.browseBack);        backButton.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                finish();            }        });        undoButton = findViewById(R.id.undo_select);        undoButton.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                undoButtonFunction();            }        });        deleteButton = findViewById(R.id.browse_delete_btn);        deleteButton.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                // check if any are selected                List<Integer> positions = new ArrayList<>();                for (int i=0; i<allImages.size(); i++){                    if (allImages.get(i).getCheckBox()){                        positions.add(i);                    }                }                if (!positions.isEmpty()){                    dialogDeleteSelectedImages(ImageBrowseActivity.this, allImages, positions);                }            }        });        moveButton = findViewById(R.id.browse_move_btn);        moveButton.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                // move selected images to different album            }        });        exportButton = findViewById(R.id.browse_export_btn);        exportButton.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                // export selected images to folder            }        });        menuButton = findViewById(R.id.browseMenu);        menuButton.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                PopupMenu popup = new PopupMenu(getApplicationContext(), v);                MenuInflater inflater = popup.getMenuInflater();                inflater.inflate(R.menu.image_browser_popup, popup.getMenu());                popup.setForceShowIcon(true);                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {                    @Override                    public boolean onMenuItemClick(MenuItem item) {                        final int delete = R.id.browser_action_delete;                        final int move = R.id.browser_action_move;                        final int export = R.id.browser_action_export;                        switch (item.getItemId()){                            case delete:                                // delete button clicked                                menuButton.setVisibility(View.GONE);                                undoButton.setVisibility(View.VISIBLE);                                deleteButton.setVisibility(View.VISIBLE);                                selecting = true;                                for (PhotoModel photoModel : allImages){                                    photoModel.setCheckBoxVisibility(true);                                }                                photoAdapter.notifyDataSetChanged();                                break;                            case move:                                // edit button clicked                                menuButton.setVisibility(View.GONE);                                undoButton.setVisibility(View.VISIBLE);                                moveButton.setVisibility(View.VISIBLE);                                selecting = true;                                break;                            case export:                                // export                                menuButton.setVisibility(View.GONE);                                undoButton.setVisibility(View.VISIBLE);                                exportButton.setVisibility(View.VISIBLE);                                selecting = true;                                break;                        }                        return true;                    }                });                popup.show();            }        });    }    public void dialogDeleteSelectedImages(Context context, final List<PhotoModel> photoModels, final List<Integer> positions){        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {            @Override            public void onClick(DialogInterface dialog, int which) {                switch (which){                    // clicked yes                    case DialogInterface.BUTTON_POSITIVE:                        // delete photo                        DataBaseHelper db = new DataBaseHelper(getApplicationContext());                        List<PhotoModel> selectedPhotos = new ArrayList<>();                        for (Integer i : positions){                            selectedPhotos.add(photoModels.get(i));                        }                        boolean success = db.deletePhotos(selectedPhotos);                        if(success){                            // remove photos from allImage array                            // delete file on disk                            String filePath = getApplicationContext().getExternalFilesDir("media/").getAbsolutePath();                            String thumbPath = getApplicationContext().getExternalFilesDir("media/t").getAbsolutePath();                            for (PhotoModel photoModel : selectedPhotos){                                allImages.remove(photoModel);                                String name = String.valueOf(photoModel.getId());                                File deleteFile = new File(filePath, name);                                File deleteThumb = new File(thumbPath, name);                                deleteFile.delete();                                deleteThumb.delete();                            }                            undoButtonFunction();                        }                        break;                    case DialogInterface.BUTTON_NEGATIVE:                        break;                }            }        };        AlertDialog.Builder builder = new AlertDialog.Builder(context);        builder.setMessage("Are you sure? This will be permanently delete " +positions.size()+ " items.")                .setPositiveButton("Yes", dialogClickListener)                .setNegativeButton("No", dialogClickListener).show();    }    public void undoButtonFunction(){        undoButton.setVisibility(View.GONE);        deleteButton.setVisibility(View.GONE);        moveButton.setVisibility(View.GONE);        exportButton.setVisibility(View.GONE);        menuButton.setVisibility(View.VISIBLE);        for (PhotoModel photoModel : allImages){            photoModel.setCheckBoxVisibility(false);            photoModel.setCheckBox(false);        }        selecting = false;        photoAdapter.notifyDataSetChanged();    }    @Override    public void onResume() {        super.onResume();        if(photoAdapter != null){            photoAdapter.notifyDataSetChanged();        }        if(allImages.isEmpty()){            empty.setVisibility(View.VISIBLE);        }    }    @Override    public void onPause() {        super.onPause();        Glide.get(this).clearMemory();    }}