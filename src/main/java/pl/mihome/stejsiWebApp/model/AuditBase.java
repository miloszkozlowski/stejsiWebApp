package pl.mihome.stejsiWebApp.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@MappedSuperclass
public abstract class AuditBase {

		@JsonSerialize(using = LocalDateTimeSerializer.class)
		private LocalDateTime whenCreated;
		
		@JsonIgnore
		@Column(name = "usuniety")
		private boolean removed;
		
		public AuditBase() { 
		}

		public LocalDateTime getWhenCreated() {
			return whenCreated;
		}

		public void setWhenCreated(LocalDateTime whenCreated) {
			this.whenCreated = whenCreated;
		}

		public boolean isRemoved() {
			return removed;
		}

		public void setRemoved(boolean done) {
			this.removed = done;
		}
		
		@PrePersist
		public void initEntity() {
			this.whenCreated = LocalDateTime.now();
			this.removed = false;
		}
}
