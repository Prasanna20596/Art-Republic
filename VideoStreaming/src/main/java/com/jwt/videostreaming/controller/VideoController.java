package com.jwt.videostreaming.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.jwt.videostreaming.entity.Video;
import com.jwt.videostreaming.service.VideoService;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class VideoController {

	@Autowired
	private VideoService videoService;
	
	@PostMapping("/savevideo")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("title") String title,
                                             @RequestParam("description") String description) throws IOException{
		return videoService.uploadVideo(file, title, description);
	}		
	
	@GetMapping("/videos")
	public ResponseEntity<List<Video>> getAllVideos(){
		return videoService.getAllVideos();
	}
	
	@DeleteMapping("/video/{id}")
	public ResponseEntity<String> deleteVideo(@PathVariable int id){
		return videoService.deleteVideo(id);
	}
	
	@GetMapping("/video/{filename}")
	public ResponseEntity<StreamingResponseBody> streamVideo(@PathVariable String filename) {
		return videoService.streamVideo(filename);
	}
	    
}
