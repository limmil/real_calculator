package com.project.real_calculator.glidemodelloader;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.File;
import java.nio.ByteBuffer;

public class EncryptionModelLoaderFactory implements ModelLoaderFactory<File, ByteBuffer> {
    @NonNull
    @Override
    public ModelLoader<File, ByteBuffer> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new EncryptionModelLoader();
    }

    @Override
    public void teardown() {
        // Do nothing
    }
}
