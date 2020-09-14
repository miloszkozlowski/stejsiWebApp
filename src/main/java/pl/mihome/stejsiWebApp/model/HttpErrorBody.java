package pl.mihome.stejsiWebApp.model;

import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

public class HttpErrorBody {
    private final Date dateStamp;
    private final HttpStatus errorCode;
    private final String errorMsg;
    private final List<String> errorDesc;

    public HttpErrorBody(HttpStatus errorCode, String errorMsg, List<String> errorDesc) {
        this.dateStamp = new Date();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.errorDesc = errorDesc;
    }

    public HttpErrorBody(HttpStatus errorCode, String errorMsg) {
        this(errorCode, errorMsg, List.of("no error description avaliable"));
    }

    public Date getDateStamp() {
        return dateStamp;
    }

    public HttpStatus getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public List<String> getErrorDesc() {
        return errorDesc;
    }

}
