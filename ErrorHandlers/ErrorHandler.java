package ErrorHandlers;

import java.io.StringWriter;
import java.io.PrintWriter;

public class ErrorHandler {

    public String returnStackStraceAsString(Exception exception) {
        StringBuilder stackTraceBuilder = new StringBuilder();

        // Append exception message and class
        stackTraceBuilder.append("Exception: ").append(exception.getClass().getName());
        stackTraceBuilder.append(" - ").append(exception.getMessage()).append("\n");

        // Append stack trace elements
        for (StackTraceElement element : exception.getStackTrace()) {
            stackTraceBuilder.append("\tat ").append(element.toString()).append("\n");
        }

        return stackTraceBuilder.toString();
    }
}
