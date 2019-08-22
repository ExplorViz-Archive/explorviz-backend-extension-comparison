package net.explorviz.extension.comparison.services;

import javax.inject.Inject;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;

import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.landscape.model.landscape.Landscape;

public class PersistenceService {

	private MongoClient client;
	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceService.class);

	private final LandscapeSerializationHelper serializationHelper;

	@Inject
	public PersistenceService(@Config("mongo.host") final String host, @Config("mongo.port") final int port,
			LandscapeSerializationHelper serializationHelper) {
		client = new MongoClient(new ServerAddress(host, port));
		this.serializationHelper = serializationHelper;
	}

	public void save(long timestamp, String landscapeJSONString) {
		MongoCollection<Document> landscapeCollection = client.getDatabase("explorviz")
				.getCollection("LandscapeCollection");
		final Document landscapeDocument = new Document();

		landscapeDocument.append("timestamp", timestamp);
		landscapeDocument.append("landscape", landscapeJSONString);

		landscapeCollection.insertOne(landscapeDocument);
		//LOGGER.info("Saved landscape with timestamp: " + timestamp);
	}

	public Landscape retrieveLandscapeByTimestamp(final long timestamp) {
		MongoCollection<Document> landscapeCollection = client.getDatabase("explorviz")
				.getCollection("LandscapeCollection");
		final Document landscapeDocument = new Document();

		landscapeDocument.append("timestamp", timestamp);

		String landscapJSONString = landscapeCollection.find(landscapeDocument).first().getString("landscape");

		Landscape landscape = null;

		try {
			landscape = serializationHelper.deserialize(landscapJSONString);
		} catch (DocumentSerializationException e) {
			LOGGER.info("Deserailization failed.");
		}

		return landscape;
	}
}