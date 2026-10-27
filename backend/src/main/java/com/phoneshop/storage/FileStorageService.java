package com.phoneshop.storage;

import com.phoneshop.common.ApiException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/webp");

	private final Path root;

	public FileStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
		this.root = Path.of(uploadDir).toAbsolutePath().normalize();
		Files.createDirectories(this.root);
	}

	public String storeProductImage(Long productId, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "File is required");
		}
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED.contains(contentType)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Only JPEG, PNG or WebP images are allowed");
		}
		String extension = switch (contentType) {
			case "image/png" -> "png";
			case "image/webp" -> "webp";
			default -> "jpg";
		};
		String relative = "products/" + productId + "-" + UUID.randomUUID() + "." + extension;
		Path target = root.resolve(relative).normalize();
		if (!target.startsWith(root)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid file path");
		}
		try {
			Files.createDirectories(target.getParent());
			file.transferTo(target);
			return relative.replace('\\', '/');
		} catch (IOException ex) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
		}
	}
}
