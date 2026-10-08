package com.campus.marketplace.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempUploadDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempUploadDir.toString());
        fileStorageService.init();
    }

    @Test
    void testTwoDifferentImagesGetUniqueFilenamesAndDoNotOverwrite() throws IOException {
        // Prepare two distinct image files
        byte[] imageAContent = "IMAGE_A_CONTENT_BYTES_TEST_12345".getBytes();
        byte[] imageBContent = "IMAGE_B_CONTENT_BYTES_DIFFERENT_67890".getBytes();

        MockMultipartFile fileA = new MockMultipartFile(
                "file", "product_a.png", "image/png", imageAContent);
        MockMultipartFile fileB = new MockMultipartFile(
                "file", "product_b.jpg", "image/jpeg", imageBContent);

        // Upload Product A image
        String urlA = fileStorageService.storeFile(fileA);
        // Upload Product B image
        String urlB = fileStorageService.storeFile(fileB);

        // 1. Verify URLs are not null and non-empty
        assertNotNull(urlA);
        assertNotNull(urlB);

        // 2. Verify URLs are completely unique
        assertNotEquals(urlA, urlB, "Product A and Product B must receive different URLs");

        // 3. Verify correct file extensions are preserved
        assertTrue(urlA.endsWith(".png"), "Image A should preserve .png extension");
        assertTrue(urlB.endsWith(".jpg"), "Image B should preserve .jpg extension");

        // 4. Verify both files exist on disk
        String filenameA = urlA.replace("/uploads/", "");
        String filenameB = urlB.replace("/uploads/", "");

        Path pathA = tempUploadDir.resolve(filenameA);
        Path pathB = tempUploadDir.resolve(filenameB);

        assertTrue(Files.exists(pathA), "File A must exist on disk");
        assertTrue(Files.exists(pathB), "File B must exist on disk");

        // 5. Verify contents were not overwritten and match original bytes
        byte[] readBytesA = Files.readAllBytes(pathA);
        byte[] readBytesB = Files.readAllBytes(pathB);

        assertArrayEquals(imageAContent, readBytesA, "File A content must match Image A");
        assertArrayEquals(imageBContent, readBytesB, "File B content must match Image B");
    }

    @Test
    void testUploadWithSameOriginalFilenameGetsDifferentUniqueStorageNames() {
        // Uploading two files with identical user-side names like "photo.jpg"
        MockMultipartFile file1 = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "DATA1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "DATA2".getBytes());

        String url1 = fileStorageService.storeFile(file1);
        String url2 = fileStorageService.storeFile(file2);

        assertNotEquals(url1, url2, "Two uploads with identical original filenames must still get different stored filenames");
    }
}
