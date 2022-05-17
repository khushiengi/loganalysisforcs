package com.example.demo;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.model.EventDetails;
import com.example.demo.service.LogService;

@SpringBootApplication
public class DemoLogApplication implements CommandLineRunner {
	
	static Logger logger = LogManager.getLogger(DemoLogApplication.class);
	
	@Autowired
	public LogService logService;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DemoLogApplication.class);
        app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		try {
			// Reading from file to get logs and convert those logs to events
			logger.info("Reading from file and processing to create events");
			Set<EventDetails> eventSet = logService.readFromFileAndprocessLogEvents(args[0]);

			// Saving the created events in database
			logger.info("Saving events to HSQLDB");
			logService.saveAllEventsInDB(eventSet);

			// Reading the saved events from database
			logger.info("Getting saved events from HSQLDB");
			logService.getAllEventsFromDB().stream().forEach(x -> logger.info(x));

		} catch (Exception e) {
			logger.error("Exception occured in run: " + e);
		}

	}

}
