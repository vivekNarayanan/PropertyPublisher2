package com.sf.xslcreator.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.sf.xslcreator.serviceimpl.StorageException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
	void init();

	void store(MultipartFile file) throws StorageException;

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename);

	void deleteAll();

	String readFile(MultipartFile file) throws StorageException;
}
