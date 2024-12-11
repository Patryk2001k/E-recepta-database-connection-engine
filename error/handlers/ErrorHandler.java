package error.handlers;

import utils.Message;

import java.sql.SQLException;
import java.util.HashMap;

public class ErrorHandler {

    public String returnStackStraceAsString(Exception exception) {
        StringBuilder stackTraceBuilder = new StringBuilder();

        stackTraceBuilder.append("Exception: ").append(exception.getClass().getName());
        stackTraceBuilder.append(" - ").append(exception.getMessage()).append("\n");

        for (StackTraceElement element : exception.getStackTrace()) {
            stackTraceBuilder.append("\tat ").append(element.toString()).append("\n");
        }

        return stackTraceBuilder.toString();
    }

    public HashMap<String, String> handleSQLException(SQLException e, HashMap<String, String> staticInfo, Message message) {
        String errorMessage = this.returnStackStraceAsString(e);
        staticInfo.replace(message.getHashIdStatus(), "error");
        staticInfo.replace(message.getHashIdException(), e.getSQLState());
        staticInfo.replace(message.getHashIdUserFriendlyError(), SQLErrorTranslator.translate(e.getSQLState()));
        staticInfo.replace(message.getHashIdErrorMessage(), errorMessage);
        return staticInfo;
    }
}
