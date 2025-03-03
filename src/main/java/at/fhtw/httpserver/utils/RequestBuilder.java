package at.fhtw.httpserver.utils;

import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.server.Request;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;

public class RequestBuilder {
    public Request buildRequest(BufferedReader reader) throws IOException {
        Request request = new Request();
        String line = reader.readLine();
        if (line != null) {
            String[] firstLineParts = line.split(" ");
            request.setMethod(getMethod(firstLineParts[0]));
            setPathname(request, firstLineParts[1]);
            line = reader.readLine();
            while (!line.isEmpty()) {
                request.getHeaderMap().ingest(line);
                line = reader.readLine();
            }
            if (request.getHeaderMap().getContentLength() > 0) {
                char[] contentBuffer = new char[request.getHeaderMap().getContentLength()];
                reader.read(contentBuffer, 0, request.getHeaderMap().getContentLength());
                request.setBody(new String(contentBuffer));
            }
        }
        return request;
    }

    private HttpMethod getMethod(String methodStr) {
        return HttpMethod.valueOf(methodStr.toLowerCase(Locale.ROOT).substring(0, 1).toUpperCase() +
                methodStr.substring(1).toLowerCase());
    }

    private void setPathname(Request request, String path) {
        if (path.contains("?")) {
            String[] parts = path.split("\\?", 2);
            request.setPathname(parts[0]);
            request.setParams(parts[1]);
        } else {
            request.setPathname(path);
            request.setParams(null);
        }
    }
}
