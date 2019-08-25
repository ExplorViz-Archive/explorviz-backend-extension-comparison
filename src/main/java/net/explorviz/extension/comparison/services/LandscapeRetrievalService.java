package net.explorviz.extension.comparison.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;

import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.security.model.User;

public class LandscapeRetrievalService {
	private String urlPath;
	private URL authUrl;
	//private URL refreshUrl;

	private LandscapeSerializationHelper landscapeSerializationHelper;
	private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeRetrievalService.class);
	private final ResourceConverter jsonApiConverter;

	private String token;

	private final String username;
	private final String password;
	

	@Inject
	public LandscapeRetrievalService(@Config("exchange.historyService.host") String landscapeHost,
			@Config("exchange.historyService.port") int landscapePort,
			@Config("exchange.historyService.path") String landscapePath,
			@Config("exchange.authService.host") String authHost, @Config("exchange.authService.port") int authPort,
			@Config("exchange.authService.path") String authPath,
			@Config("exchange.authService.username") String username,
			@Config("exchange.authService.password") String password,
			LandscapeSerializationHelper landscapeSerializationHelper, ResourceConverter jsonApiConverter)
			throws MalformedURLException {

		this.urlPath = "http://" + landscapeHost + ":" + landscapePort + landscapePath;
		this.authUrl = new URL("http", authHost, authPort, authPath);
		//this.refreshUrl = new URL(authUrl.toString() + "/refresh");

		this.username = username;
		this.password = password;

		this.landscapeSerializationHelper = landscapeSerializationHelper;
		this.jsonApiConverter = jsonApiConverter;

		try {
			getToken();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Landscape retrieveLandscapeByTimestamp(long timestamp) throws IOException, DocumentSerializationException {
		final HttpURLConnection landscapeConnection = (HttpURLConnection) new URL(urlPath + "?timestamp=" + timestamp)
				.openConnection();

		landscapeConnection.setDoInput(true);
		landscapeConnection.setRequestMethod("GET");
		landscapeConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		landscapeConnection.setRequestProperty("Authorization", "Bearer " + token);

		final InputStream inputStream = landscapeConnection.getInputStream();
		String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		inputStream.close();

		return landscapeSerializationHelper.deserialize(jsonString);

	}

	private void getToken() throws IOException {
		final HttpURLConnection authConnection = (HttpURLConnection) authUrl.openConnection();
		String query = "{ \"username\": \"" + this.username + "\", \"password\": \"" + this.password + "\" }";

		authConnection.setDoInput(true);
		authConnection.setDoOutput(true);
		authConnection.setRequestMethod("POST");
		authConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		authConnection.setRequestProperty("Content-Length", Integer.toString(query.length()));

		OutputStreamWriter writer = new OutputStreamWriter(authConnection.getOutputStream());
		writer.write(query);
		writer.flush();
		writer.close();

		final InputStream inputStream = authConnection.getInputStream();
		String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		inputStream.close();

		final JSONAPIDocument<User> document = jsonApiConverter.readDocument(jsonString.getBytes(), User.class);

		this.token = document.get().getToken();

	}
	
	/*
	private void refreshToken() throws IOException {
		final HttpURLConnection authConnection = (HttpURLConnection) refreshUrl.openConnection();
		
		authConnection.setDoInput(true);
		authConnection.setRequestMethod("POST");
		authConnection.setRequestProperty("Accept", "application/json");
		authConnection.setRequestProperty("Authorization", "Bearer " + token);
		
		final InputStream inputStream = authConnection.getInputStream();
		String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		inputStream.close();
		
		final JSONAPIDocument<Token> document = jsonApiConverter.readDocument(jsonString.getBytes(), User.class);
	}
	*/
}
