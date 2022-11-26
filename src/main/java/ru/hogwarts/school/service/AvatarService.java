package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.AvatarRecord;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {
	private Logger logger = LoggerFactory.getLogger(AvatarService.class);
    private AvatarRepository avatarRepository;
    private StudentRepository studentRepository;

    @Value("${dir.avatar}")
    private String dirAvatar;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public void upload(Long studentId, MultipartFile file) throws IOException {
		logger.info("Method 'upload()' was invoked");
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
			logger.error("Студента с идентификатором '{}' не существует", studentId);
            throw new IOException("Студента с таким идентификатором не существует");
        }

        String fileName = file.getOriginalFilename();
        int beginIndex = fileName.lastIndexOf(".");
        if (beginIndex == -1) {
            logger.error("В файле '" + fileName + "' отсутствует расширение");
			throw new IOException("В файле '" + fileName + "' отсутствует расширение");
        }
        Path fullPath = Path.of(dirAvatar, student.getId().toString() + fileName.substring(beginIndex));

        Files.createDirectories(fullPath.getParent());
        Files.deleteIfExists(fullPath);

        try(InputStream is = file.getInputStream();
            OutputStream os = Files.newOutputStream(fullPath, CREATE_NEW);
            BufferedInputStream bis = new BufferedInputStream(is, 512);
            BufferedOutputStream bos = new BufferedOutputStream(os, 512);) {

            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(student.getId()).orElse(new Avatar());
        avatar.setFilePath(fullPath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());
        avatar.setStudent(student);
        avatarRepository.save(avatar);
    }

    public Avatar getAvatar(Long id) {
		logger.info("Method 'getAvatar()' was invoked");
        return avatarRepository.findByStudentId(id).orElse(null);
    }
	
	public List<AvatarRecord> getAll(int pageNumber, int sizeNumber) {
		logger.info("Method 'getAll()' was invoked");
		PageRequest pageRequest = PageRequest.of(pageNumber, sizeNumber);
        List<AvatarRecord> content = avatarRepository.findAllBy(pageRequest).getContent();
        return content;
	}

}
