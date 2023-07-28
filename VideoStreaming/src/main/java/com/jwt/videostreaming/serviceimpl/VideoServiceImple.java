package com.jwt.videostreaming.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import javax.activation.MimetypesFileTypeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.jwt.videostreaming.entity.Video;
import com.jwt.videostreaming.repository.VideoRepository;
import com.jwt.videostreaming.service.VideoService;

@Service
public class VideoServiceImple implements VideoService{

	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@Autowired
	private VideoRepository videoRepository;

	public ResponseEntity<String> uploadVideo(MultipartFile file, String title, String description) throws IOException {
		try {
			String originalFilename=file.getOriginalFilename();
			String filePath = Paths.get(uploadDir, originalFilename).toString();
            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

            Video saveVideo = new Video();
            saveVideo.setFilename(originalFilename); // Store the unique identifier (filename) in the database
            saveVideo.setTitle(title);
            saveVideo.setDescription(description);

            videoRepository.save(saveVideo);

            return ResponseEntity.ok("File uploaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
		
	}

	public ResponseEntity<List<Video>> getAllVideos() {
		List<Video> videos = videoRepository.findAll();
		return ResponseEntity.ok(videos);
	}

	public ResponseEntity<String> deleteVideo(int id) {
		try {
			Optional<Video> optionalVideo=videoRepository.findById(id);
            Video video = optionalVideo.get();
			String filePath = Paths.get(uploadDir, video.getFilename()).toString();
			if (Files.exists(Paths.get(filePath))) {
				Files.delete(Paths.get(filePath));
			}
		    videoRepository.delete(video);
			return ResponseEntity.ok("Video deleted successfully!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Video deletion failed.");
		}
    }
		
	private String getContentType(String filename) {
	    File file = new File(filename);
	    String contentType = new MimetypesFileTypeMap().getContentType(file);
	    return contentType != null ? contentType : "application/octet-stream";
	}
	
	public ResponseEntity<StreamingResponseBody> streamVideo(String filename) {
		Path videoPath = Paths.get(uploadDir, filename);
		Resource videoResource = new FileSystemResource(videoPath.toFile());
		if (videoResource.exists() && videoResource.isReadable()) {
	           StreamingResponseBody responseBody = outputStream -> {
	                try (InputStream inputStream = videoResource.getInputStream()) {
	                    byte[] buffer = new byte[4096];
	                    int bytesRead;
	                    while ((bytesRead = inputStream.read(buffer)) != -1) {
	                        outputStream.write(buffer, 0, bytesRead);
	                    }
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            };
	            HttpHeaders headers=new HttpHeaders();
	            headers.setContentType(MediaType.parseMediaType(getContentType(filename)));
	            return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }

}
