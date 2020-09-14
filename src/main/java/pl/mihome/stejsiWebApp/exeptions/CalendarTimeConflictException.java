package pl.mihome.stejsiWebApp.exeptions;

import java.time.LocalDateTime;

public class CalendarTimeConflictException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204452669936353251L;

	private final String terminReadable;
	private final LocalDateTime termin;
	
	public CalendarTimeConflictException(String terminReadable, LocalDateTime termin) {
		this.terminReadable = terminReadable;
		this.termin = termin;
	}
	
	public String getReadableTermin() {
		return terminReadable;
	}

	public LocalDateTime getTermin() {
		return termin;
	}
	
	

}
