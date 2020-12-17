package com.limmil.real_calculator.glidemodelloader;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.limmil.real_calculator.encryption.EncryptedFileObject;

import java.nio.ByteBuffer;

@GlideModule
public class EncryptionGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, Registry registry) {
        registry.prepend(EncryptedFileObject.class, ByteBuffer.class, new EncryptionModelLoaderFactory());
    }
}
