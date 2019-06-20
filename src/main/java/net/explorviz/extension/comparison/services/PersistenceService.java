package net.explorviz.extension.comparison.services;

import javax.inject.Inject;

import org.bson.Document;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;

import net.explorviz.shared.config.annotations.Config;
import net.explorviz.shared.config.annotations.ConfigValues;
import net.explorviz.shared.landscape.model.landscape.Landscape;

public class PersistenceService {

	private final MongoClient client;
	
	@Inject
	private final LandscapeSerializationHelper serializationHelper;

	@ConfigValues({@Config("mongo.host"), @Config("mongo.port")})
	@Inject
	public PersistenceService(String host, String port, LandscapeSerializationHelper serializationHelper) {
		client = new MongoClient(new MongoClientURI("mongodb://" + host + ":" + port));
		this.serializationHelper = serializationHelper;
	}
	
	public void save(long timestamp, String landscapeJSONString) {
		MongoCollection<Document> landscapeCollection = client.getDatabase("explorviz").getCollection("LandscapeCollection");
		final Document landscapeDocument = new Document();
		
		landscapeDocument.append("timestamp", timestamp);
		landscapeDocument.append("landscape", landscapeJSONString);
		
		landscapeCollection.insertOne(landscapeDocument);
	}
	
	public Landscape retrieveLandscapeByTimestamp(final long timestamp) {
		MongoCollection<Document> landscapeCollection = client.getDatabase("explorviz").getCollection("LandscapeCollection");
		final Document landscapeDocument = new Document();
		
		landscapeDocument.append("timestamp", timestamp);
		
		String landscapJSONString = landscapeCollection.find(landscapeDocument).first().getString("landscape");
		
		Landscape landscape = null;
		
		try {
			landscape = serializationHelper.deserialize(landscapJSONString);
		} catch (DocumentSerializationException e) {
			
		}
		
		return landscape;
	}
}