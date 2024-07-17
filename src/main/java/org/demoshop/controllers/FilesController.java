package org.demoshop.controllers;

import lombok.RequiredArgsConstructor;
import org.demoshop.dto.StandardResponseDto;
import org.demoshop.services.FilesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FilesController {

    private final FilesService filesService;

    @PostMapping("/api/users/files")
    public StandardResponseDto upload(@RequestParam("file") MultipartFile file) throws IOException {
        return filesService.upload(file);
    }
}
