package pl.mihome.stejsiWebApp.exeptions;

import java.time.LocalDateTime;

public class CallendarTimeConflictException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204452669936353251L;

	private String terminReadable;
	private LocalDateTime termin;
	
	public CallendarTimeConflictException(String terminReadable, LocalDateTime termin) {
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
