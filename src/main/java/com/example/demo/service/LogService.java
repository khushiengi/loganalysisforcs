package com.example.demo.service;

import java.util.List;
import java.util.Set;

import com.example.demo.model.EventDetails;

public interface LogService {
	
	public Set<EventDetails> readFromFileAndprocessLogEvents(String fileLocation) throws Exception;

	public void saveEventInDB(EventDetails event);

	public void saveAllEventsInDB(Set<EventDetails> eventSet);

	public List<EventDetails> getAllEventsFromDB();

}
