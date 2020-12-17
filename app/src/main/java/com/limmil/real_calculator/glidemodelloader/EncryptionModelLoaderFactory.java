package com.limmil.real_calculator.glidemodelloader;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.limmil.real_calculator.encryption.EncryptedFileObject;

import java.nio.ByteBuffer;

public class EncryptionModelLoaderFactory implements ModelLoaderFactory<EncryptedFileObject, ByteBuffer> {
    @NonNull
    @Override
    public ModelLoader<EncryptedFileObject, ByteBuffer> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new EncryptionModelLoader();
    }

    @Override
    public void teardown() {
        // Do nothing
    }
}
