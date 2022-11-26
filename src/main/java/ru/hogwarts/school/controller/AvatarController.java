package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.AvatarRecord;
import ru.hogwarts.school.service.AvatarService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.List;

@RestController
@RequestMapping("avatar")
public class AvatarController {
    private AvatarService avatarService;

    //public AvatarController() {}
    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "{studentId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long studentId, @RequestParam MultipartFile avatar) throws IOException{
        avatarService.upload(studentId, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "{studentId}/get-from-bd")
    public ResponseEntity<byte[]> getAvatarFromDatabase(@PathVariable Long studentId) throws IOException {
        Avatar avatar = avatarService.getAvatar(studentId);
        if (avatar == null) {
            String message = "У студента с id = " + studentId + " нет аватара!";
            throw new IOException(message);
        }
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "{studentId}/get-from-file")
    public void getAvatarFromFile(@PathVariable Long studentId, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.getAvatar(studentId);
        if (avatar == null) {
            String message = "У студента с id = " + studentId + " нет аватара!";
            throw new IOException(message);
        }
        Path path = Path.of(avatar.getFilePath());

        try(InputStream is = Files.newInputStream(path);
            OutputStream os = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is, 512);
            BufferedOutputStream bos = new BufferedOutputStream(os, 512);) {

            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            bis.transferTo(bos);
        }
    }

    @GetMapping
    public ResponseEntity<List<AvatarRecord>> getAll(@RequestParam("page") Integer pageNumber, @RequestParam("size") Integer sizeNumber) {
        List<AvatarRecord> result = avatarService.getAll(pageNumber, sizeNumber);
        return ResponseEntity.ok().body(result);
    }
}
