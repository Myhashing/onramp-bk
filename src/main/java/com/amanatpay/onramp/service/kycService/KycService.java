package com.amanatpay.onramp.service.kycService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class KycService {

    public String saveKycImage(MultipartFile kycImage) throws IOException {        // Define the directory where the image will be saved
        String uploadDir = "/path/to/upload/directory";

        // Create the directory if it does not exist
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // Generate a unique file name
        String fileName = UUID.randomUUID().toString() + "_" + kycImage.getOriginalFilename();

        // Save the file to the directory
        File destFile = new File(uploadDirFile, fileName);
        kycImage.transferTo(destFile);

        // Return the path to the saved file
        return destFile.getAbsolutePath();
    }
}
