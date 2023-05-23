package com.mobiquity.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mobiquity.packer.Packer;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api") 
@Slf4j
public class PackageController {
	
	
	@PostMapping(value = "/packageFile", produces = APPLICATION_JSON_VALUE)
	public String packageCollecctor(@RequestParam MultipartFile file) throws IOException {
		log.info("file name!!! {}", file.getOriginalFilename());
		return Packer.pack(file.getBytes());
	}
	
	@PostMapping(value = "/packageFilePath", produces = APPLICATION_JSON_VALUE)
	public String packageCollecctorFromPath(@RequestParam(name = "filePath") String filePath) throws IOException {
		log.info("filepath!!! {}", filePath);
		return Packer.pack(filePath);
	}

}
