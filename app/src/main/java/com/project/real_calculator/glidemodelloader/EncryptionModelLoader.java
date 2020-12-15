package com.project.real_calculator.glidemodelloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;
import com.project.real_calculator.encryption.EncryptedFileObject;

import java.io.File;
import java.nio.ByteBuffer;

public final class EncryptionModelLoader implements ModelLoader<EncryptedFileObject, ByteBuffer> {

    @Nullable
    @Override
    public ModelLoader.LoadData<ByteBuffer> buildLoadData(EncryptedFileObject file, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(file), new EncryptionDataFetcher(file));
    }

    @Override
    public boolean handles(@NonNull EncryptedFileObject file) {
        return file instanceof EncryptedFileObject;
    }
}
