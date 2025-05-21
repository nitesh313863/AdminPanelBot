package com.lincpay.chatbot.util;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public class MultipartFileResource extends InputStreamResource {

    private final String filename;

    public MultipartFileResource(MultipartFile multipartFile) throws IOException {
        super(multipartFile.getInputStream());
        this.filename = multipartFile.getOriginalFilename();
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() {
        return -1;
    }
}
