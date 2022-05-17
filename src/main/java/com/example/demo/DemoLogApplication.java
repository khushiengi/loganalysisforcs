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
		try {
			SpringApplication app = new SpringApplication(DemoLogApplication.class);

			if (args != null && args.length > 0) {
				app.run(args);
			} else {
				throw new Exception("Please enter the path for logfile.txt");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void run(String... args) throws Exception {
		
		try {
			logger.info("Fetching logs from file and processing them");
			Set<EventDetails> eventSet = logService.readFromFileAndprocessLogEvents(args[0]);

			logger.info("Saving events to HSQLDB");
			logService.saveAllEventsInDB(eventSet);

			logger.info("Getting saved events from HSQLDB");
			logService.getAllEventsFromDB().stream().forEach(x -> logger.debug(x));

		} catch (Exception e) {
			logger.error("Exception occured in run: " + e);
		}

	}

}
