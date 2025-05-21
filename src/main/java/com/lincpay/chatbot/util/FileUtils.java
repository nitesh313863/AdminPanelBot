package com.lincpay.chatbot.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

public class FileUtils {
    public static String encodeFileToBase64(byte[] fileData) {
        if (fileData == null) return null;
        return Base64.getEncoder().encodeToString(fileData);
    }

    public static byte[] decodeBase64ToFile(String base64String) {
        if (base64String == null || base64String.isEmpty()) return null;
        return Base64.getDecoder().decode(base64String);
    }

    public static byte[] convertMultipartFileToBytes(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        return file.getBytes();
    }
}
