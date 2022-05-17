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

		// Map to store log id with the Log object for that Log
		Map<String, LogDetails> eventsMap = new ConcurrentHashMap<String, LogDetails>();

		// Set to store event objects that have been created from Log objects
		Set<EventDetails> eventSet = new HashSet<EventDetails>();

		// Try with resource block
		try (Scanner myReader = new Scanner(new File(fileLocation))) {

			// Object mapper to map json properties with the class variables
			ObjectMapper logObjectMapper = new ObjectMapper();

			while (myReader.hasNextLine()) {
				String jsonData = myReader.nextLine();
				// Map JSON values with Log class to create Log object
				LogDetails currentLog = logObjectMapper.readValue(jsonData, LogDetails.class);

				// Check if the Log id is found in Map, if found, go to else block, else add the
				// object to map
				if (!eventsMap.containsKey(currentLog.getId())) {
					eventsMap.put(currentLog.getId(), currentLog);
				} else {
					LogDetails previousLog = eventsMap.get(currentLog.getId());

					// Calculate the absolute value of duration
					long duration = Math.abs(currentLog.getTimestamp() - previousLog.getTimestamp());

					// Create object of event using Log object and duration
					EventDetails event = new EventDetails(currentLog, duration);
					eventSet.add(event);

					// Remove the
					eventsMap.remove(currentLog.getId());
				}
			} // Exception Handling
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
