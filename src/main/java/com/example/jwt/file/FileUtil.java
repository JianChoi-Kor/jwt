package com.example.jwt.file;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileUtil {

    // 폴더 만드는 메서드
    public void makeFolders(String path) {
        File folder = new File(path);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }


}
