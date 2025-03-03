package at.fhtw.httpserver.server;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Response {
    private final int status;
    private final String message;
    private final String contentType;
    private final String content;

    public Response(HttpStatus httpStatus, ContentType contentType, String content) {
        this.status = httpStatus.getCode();
        this.message = httpStatus.toString();
        this.contentType = contentType.type();
        this.content = content;
    }

    public String get() {
        String localDatetime = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("UTC")));
        return "HTTP/1.1 " + status + " " + message + "\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "Connection: close\r\n" +
                "Date: " + localDatetime + "\r\n" +
                "Expires: " + localDatetime + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + (content != null ? content.length() : 0) + "\r\n" +
                "\r\n" +
                (content != null ? content : "");
    }
}
