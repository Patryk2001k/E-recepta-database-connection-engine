package Utils;

import java.util.HashMap;

public class Message {
    private final HashMap<String, String> errorHashMap = new HashMap<>();
    private final String status;
    private final String exception;
    private final String userFriendlyError;
    private final String errorMessage;
    private final String defaultStatusResponse;
    private final String defaultExceptionResponse;
    private final String defaultUserFriendlyErrorResponse;
    private final String defaultErrorMessageResponse;

    public Message() {
        this.status = "status";
        this.exception = "exception";
        this.userFriendlyError = "userFriendlyError";
        this.errorMessage = "errorMessage";
        this.defaultStatusResponse = "Success";
        this.defaultExceptionResponse = "There is no exception";
        this.defaultUserFriendlyErrorResponse = "There is no error";
        this.defaultErrorMessageResponse = "There is no error message";

        errorHashMap.put(this.status, this.defaultStatusResponse);
        errorHashMap.put(this.exception, this.defaultExceptionResponse);
        errorHashMap.put(this.userFriendlyError, this.defaultUserFriendlyErrorResponse);
        errorHashMap.put(this.errorMessage, this.defaultErrorMessageResponse);

    }

    public String getHashIdStatus() {
        return status;
    }

    public String getHashIdException() {
        return exception;
    }

    public String getHashIdUserFriendlyError() {
        return userFriendlyError;
    }

    public String getHashIdErrorMessage() {
        return errorMessage;
    }

    public String getDefaultStatusResponse(){
        return defaultStatusResponse;
    }

    public String getDefaultExceptionResponse(){
        return defaultExceptionResponse;
    }

    public String getDefaultUserFriendlyErrorResponse(){
        return defaultUserFriendlyErrorResponse;
    }

    public String getDefaultErrorMessageResponse(){
        return defaultErrorMessageResponse;
    }

    public HashMap<String, String> getDefaultErrorMessageAsHashMap(){
        return this.errorHashMap;
    }

}
