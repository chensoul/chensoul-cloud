package com.chensoul.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
public class ResourceUtils {

	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	public static boolean resourceExists(Object classLoaderSource, String filePath) {
		return resourceExists(classLoaderSource.getClass().getClassLoader(), filePath);
	}

	public static boolean resourceExists(ClassLoader classLoader, String filePath) {
		boolean classPathResource = false;
		String path = filePath;
		if (path.startsWith(CLASSPATH_URL_PREFIX)) {
			path = path.substring(CLASSPATH_URL_PREFIX.length());
			classPathResource = true;
		}
		if (!classPathResource) {
			File resourceFile = new File(path);
			if (resourceFile.exists()) {
				return true;
			}
		}
		InputStream classPathStream = classLoader.getResourceAsStream(path);
		if (classPathStream != null) {
			return true;
		}
		else {
			try {
				URL url = getResource(path);
				if (url != null) {
					return true;
				}
			}
			catch (IllegalArgumentException e) {
			}
		}
		return false;
	}

	public static InputStream getInputStream(Object classLoaderSource, String filePath) {
		return getInputStream(classLoaderSource.getClass().getClassLoader(), filePath);
	}

	public static InputStream getInputStream(ClassLoader classLoader, String filePath) {
		boolean classPathResource = false;
		String path = filePath;
		if (path.startsWith(CLASSPATH_URL_PREFIX)) {
			path = path.substring(CLASSPATH_URL_PREFIX.length());
			classPathResource = true;
		}
		try {
			if (!classPathResource) {
				File resourceFile = new File(path);
				if (resourceFile.exists()) {
					log.info("Reading resource data from file {}", filePath);
					return new FileInputStream(resourceFile);
				}
			}
			InputStream classPathStream = classLoader.getResourceAsStream(path);
			if (classPathStream != null) {
				log.info("Reading resource data from class path {}", filePath);
				return classPathStream;
			}
			else {
				URL url = getResource(path);
				if (url != null) {
					URI uri = url.toURI();
					log.info("Reading resource data from URI {}", filePath);
					return new FileInputStream(new File(uri));
				}
			}
		}
		catch (Exception e) {
			if (e instanceof NullPointerException) {
				log.warn("Unable to find resource: " + filePath);
			}
			else {
				log.warn("Unable to find resource: " + filePath, e);
			}
		}
		throw new RuntimeException("Unable to find resource: " + filePath);
	}

	public static String getUri(Object classLoaderSource, String filePath) {
		return getUri(classLoaderSource.getClass().getClassLoader(), filePath);
	}

	public static String getUri(ClassLoader classLoader, String filePath) {
		try {
			File resourceFile = new File(filePath);
			if (resourceFile.exists()) {
				log.info("Reading resource data from file {}", filePath);
				return resourceFile.getAbsolutePath();
			}
			else {
				URL url = classLoader.getResource(filePath);
				return url.toURI().toString();
			}
		}
		catch (Exception e) {
			if (e instanceof NullPointerException) {
				log.warn("Unable to find resource: " + filePath);
			}
			else {
				log.warn("Unable to find resource: " + filePath, e);
			}
			throw new RuntimeException("Unable to find resource: " + filePath);
		}
	}

	public static URL getResource(String resourceName) {
		ClassLoader loader = (ClassLoader) ObjectUtils.firstNonNull(Thread.currentThread().getContextClassLoader(),
				ResourceUtils.class.getClassLoader());
		URL url = loader.getResource(resourceName);
		Assert.notNull(url, String.format("resource %s not found.", resourceName));
		return url;
	}

}
