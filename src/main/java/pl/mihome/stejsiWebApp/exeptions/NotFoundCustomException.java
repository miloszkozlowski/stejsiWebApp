package pl.mihome.stejsiWebApp.exeptions;

import org.bouncycastle.pqc.crypto.newhope.NHOtherInfoGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundCustomException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5759832432660724289L;
	private final String desc;

	public NotFoundCustomException(String msg, String desc) {
		super(msg);
		this.desc = desc;
	}

	public NotFoundCustomException(String msg) {
		this(msg, "No further description");
	}

	public NotFoundCustomException() {
		this("No message");
	}

	public String getDesc() {
		return desc;
	}
}
