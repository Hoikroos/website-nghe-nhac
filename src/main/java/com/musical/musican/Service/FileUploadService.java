package com.musical.musican.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {

    /**
     * Upload file ảnh vào thư mục `static/uploads/[subFolder]/`
     * 
     * @param file      ảnh người dùng upload
     * @param subFolder thư mục con (vd: avatars, products)
     * @return đường dẫn tương đối để hiển thị trên web (vd:
     *         /uploads/avatars/123.png)
     */
    String uploadFile(MultipartFile file, String subFolder) throws IOException;

    /**
     * Xóa file từ thư mục uploads
     * 
     * @param filePath đường dẫn từ `/uploads/...`
     */
    boolean deleteFile(String filePath);
}
