package com.limmil.real_calculator.glidemodelloader;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.limmil.real_calculator.encryption.AES;
import com.limmil.real_calculator.encryption.EncryptedFileObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.crypto.CipherInputStream;

public class EncryptionDataFetcher implements DataFetcher<ByteBuffer> {

    private final EncryptedFileObject file;

    public EncryptionDataFetcher(EncryptedFileObject file) {
        this.file = file;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super ByteBuffer> callback) {
        // open file
        byte[] data = new byte[0];
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);

        try {
            AES decrypt = new AES(file.getIv());
            data = decrypt.iDecrypt(getBytes(new FileInputStream(file.getEncryptedFile())));
            byteBuffer = ByteBuffer.wrap(data);
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (OutOfMemoryError e){
            // slower decryption method less ram
            byteBuffer = decryptFile(file);
        }


        callback.onDataReady(byteBuffer);
    }



    public byte[] getBytes(InputStream inputStream) throws IOException {
        MyByteArrayOutputStream byteBuffer = new MyByteArrayOutputStream((int)file.getEncryptedFile().length());
        int bufferSize = 64 * 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        inputStream.close();
        return byteBuffer.getBuf();
    }

    public static ByteBuffer decryptFile(EncryptedFileObject f){
        FileInputStream fis;
        CipherInputStream cis;
        AES cipher = new AES(f.getIv());
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[0]);
        try {
            fis = new FileInputStream(f.getEncryptedFile());
            cis = new CipherInputStream(fis, cipher.iDecryptCipher());
            MyByteArrayOutputStream bos = new MyByteArrayOutputStream((int)f.getEncryptedFile().length());
            byte[] buffer = new byte[1024*1024];

            int len = 0;
            while ((len=cis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byteBuffer = ByteBuffer.wrap(bos.getBuf());
            fis.close();
            cis.close();
            bos.close();
        } catch (IOException | OutOfMemoryError ex) {
            //ex.printStackTrace();
        } catch (Exception e){
            //e.printStackTrace();
        }
        return byteBuffer;
    }

    @Override
    public void cleanup() {
        // Intentionally empty only because we're not opening an InputStream or another I/O resource!
    }

    @Override
    public void cancel() {
        // Intentionally empty.
    }

    @NonNull
    @Override
    public Class<ByteBuffer> getDataClass() {
        return ByteBuffer.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }

    public static class MyByteArrayOutputStream extends ByteArrayOutputStream {
        public MyByteArrayOutputStream() {
        }

        public MyByteArrayOutputStream(int size) {
            super(size);
        }

        public int getCount() {
            return count;
        }

        public byte[] getBuf() {
            return buf;
        }
    }
}
