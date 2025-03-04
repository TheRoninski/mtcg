package at.fhtw.httpserver.server;

import java.util.HashMap;
import java.util.Map;

public class HeaderMap {
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private final Map<String, String> headers = new HashMap<>();

    public void ingest(String headerLine) {
        String[] split = headerLine.split(":", 2);
        if (split.length == 2) {
            headers.put(split[0].trim(), split[1].trim());
        }
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public int getContentLength() {
        String header = headers.get(CONTENT_LENGTH_HEADER);
        if (header == null) {
            return 0;
        }
        return Integer.parseInt(header);
    }

    @Override
    public String toString() {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        return headers.toString();
    }
}
