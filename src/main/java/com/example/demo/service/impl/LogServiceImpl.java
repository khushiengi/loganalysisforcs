package com.example.demo.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.EventDetails;
import com.example.demo.model.LogDetails;
import com.example.demo.repository.EventDetailsRepository;
import com.example.demo.service.LogService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LogServiceImpl implements LogService {
	
	@Autowired
	EventDetailsRepository repo;

	@Override
	public Set<EventDetails> readFromFileAndprocessLogEvents(String fileLocation) throws Exception {
		// Check for null fileLocation or empty string in fileLocation
		if (fileLocation == null)
			throw new NullPointerException();
		else if (fileLocation.equals("") || fileLocation.length() == 0) {
			throw new Exception("Please provide file location");
		}

		Map<String, LogDetails> eventsMap = new ConcurrentHashMap<String, LogDetails>();

		Set<EventDetails> eventSet = new HashSet<EventDetails>();

		try (Scanner myReader = new Scanner(new File(fileLocation))) {

			ObjectMapper logObjectMapper = new ObjectMapper();

			while (myReader.hasNextLine()) {
				String jsonData = myReader.nextLine();

				LogDetails currentLog = logObjectMapper.readValue(jsonData, LogDetails.class);

				if (!eventsMap.containsKey(currentLog.getId())) {
					eventsMap.put(currentLog.getId(), currentLog);
				} else {
					LogDetails previousLog = eventsMap.get(currentLog.getId());

					long duration = Math.abs(currentLog.getTimestamp() - previousLog.getTimestamp());

					EventDetails event = new EventDetails(currentLog, duration);
					eventSet.add(event);

					eventsMap.remove(currentLog.getId());
				}
			} 
		} catch (FileNotFoundException e) {
			throw e;
		} catch (JsonParseException e) {
			throw e;
		} catch (JsonMappingException e) {
			throw e;
		}

		return eventSet;

	}

	@Override
	public void saveEventInDB(EventDetails event) {
		repo.save(event);

	}

	@Override
	public void saveAllEventsInDB(Set<EventDetails> eventSet) {
		repo.saveAll(eventSet);

	}

	@Override
	public List<EventDetails> getAllEventsFromDB() {
		return repo.findAll();
	}

}
