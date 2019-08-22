package net.explorviz.extension.comparison.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;

import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.config.annotations.ConfigValues;
import net.explorviz.shared.landscape.model.landscape.Landscape;

public class LandscapeRetrievalService {
	private URL url;
	private LandscapeSerializationHelper landscapeSerializationHelper;

	@Inject
	public LandscapeRetrievalService(@Config("exchange.historyService.host") String host,
			@Config("exchange.historyService.port") int port, @Config("exchange.historyService.path") String path,
			LandscapeSerializationHelper landscapeSerializationHelper) throws MalformedURLException {
		url = new URL("http", host, port, path);
		this.landscapeSerializationHelper = landscapeSerializationHelper;
	}

	public Landscape retrieveLandscapeByTimestamp(long timestamp) throws IOException, DocumentSerializationException {
		final URLConnection urlConnection = url.openConnection();
		urlConnection.setDoInput(true);
		urlConnection.setRequestProperty("content-type", "application/json; charset=utf-8");
		urlConnection.connect();

		final InputStream inputStream = urlConnection.getInputStream();
		String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		inputStream.close();
		
		return landscapeSerializationHelper.deserialize(jsonString);

	}
}
