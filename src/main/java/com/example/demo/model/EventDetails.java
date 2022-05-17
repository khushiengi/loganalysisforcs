package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name="EVENT_DETAILS")
public class EventDetails {
	
	@Id
	private String id;

	private long duration;

	private String type;

	private String host;

	private boolean alert;
	
	public EventDetails(LogDetails log, long duration) {
		this.id = log.getId();
		this.duration = duration;
		this.alert = duration > 4;

		if (log.getType() != null && log.getType().equals("APPLICATION_LOG")) {
			this.type = log.getType();
			this.host = log.getHost();
		}
	}

}
