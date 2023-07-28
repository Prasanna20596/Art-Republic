package com.jwt.videostreaming.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jwt.videostreaming.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Integer> {
	
	

}
