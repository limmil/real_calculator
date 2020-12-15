package com.project.real_calculator.encryption;

import java.io.File;

public class EncryptedFileObject {

    private File encryptedFile;
    private byte[] iv;

    public EncryptedFileObject(File encryptedFile, byte[] iv){
        this.encryptedFile = encryptedFile;
        this.iv = iv;
    }

    public File getEncryptedFile() {
        return encryptedFile;
    }

    public void setEncryptedFile(File encryptedFile) {
        this.encryptedFile = encryptedFile;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
