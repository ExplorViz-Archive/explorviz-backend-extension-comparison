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

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;

import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.security.model.User;

/**
 * Provides a service to retrieve landscapes from the HistoryService
 *
 */
public class LandscapeRetrievalService {
	private String urlPath;
	private URL authUrl;

	private LandscapeSerializationHelper landscapeSerializationHelper;
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
	
	/**
	 * Retrieves a single landscape.
	 * 
	 * @param timestamp the timestamp of the landscape
	 * @return the retrieved landscape
	 * @throws IOException
	 * @throws DocumentSerializationException
	 */
	public Landscape retrieveLandscapeByTimestamp(long timestamp) throws IOException, DocumentSerializationException {
		final HttpURLConnection landscapeConnection = (HttpURLConnection) new URL(urlPath + "?timestamp=" + timestamp)
				.openConnection();

		// set connection options
		landscapeConnection.setDoInput(true);
		landscapeConnection.setRequestMethod("GET");
		landscapeConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		landscapeConnection.setRequestProperty("Authorization", "Bearer " + token);
		
		// get a new token if the the access was denied
		if(landscapeConnection.getResponseCode() == 403) {
			getToken();
			return retrieveLandscapeByTimestamp(timestamp);
		}

		// retrieve the answer
		final InputStream inputStream = landscapeConnection.getInputStream();
		String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		inputStream.close();

		return landscapeSerializationHelper.deserialize(jsonString);

	}

	/**
	 * Gets a new token from the user service.
	 * 
	 * @throws IOException
	 */
	private void getToken() throws IOException {
		final HttpURLConnection authConnection = (HttpURLConnection) authUrl.openConnection();
		String query = "{ \"username\": \"" + this.username + "\", \"password\": \"" + this.password + "\" }";

		// set connection options
		authConnection.setDoInput(true);
		authConnection.setDoOutput(true);
		authConnection.setRequestMethod("POST");
		authConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		authConnection.setRequestProperty("Content-Length", Integer.toString(query.length()));

		// send data
		OutputStreamWriter writer = new OutputStreamWriter(authConnection.getOutputStream());
		writer.write(query);
		writer.flush();
		writer.close();

		// retrieve the answer and store it
		final InputStream inputStream = authConnection.getInputStream();
		String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		inputStream.close();

		final JSONAPIDocument<User> document = jsonApiConverter.readDocument(jsonString.getBytes(), User.class);

		this.token = document.get().getToken();

	}
}