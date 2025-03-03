package at.fhtw.httpserver.server;

import at.fhtw.httpserver.http.HttpMethod;
import java.util.ArrayList;
import java.util.List;

public class Request {
    private HttpMethod method;
    private String urlContent;
    private String pathname;
    private List<String> pathParts;
    private String params;
    private final HeaderMap headerMap = new HeaderMap();
    private String body;

    public String getServiceRoute() {
        if (pathParts == null || pathParts.isEmpty()) {
            return null;
        }
        return "/" + pathParts.get(0);
    }

    public String getUrlContent() {
        return urlContent;
    }

    public void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
        if (urlContent.contains("?")) {
            String[] parts = urlContent.split("\\?", 2);
            setPathname(parts[0]);
            setParams(parts[1]);
        } else {
            setPathname(urlContent);
            setParams(null);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
        String[] parts = pathname.split("/");
        pathParts = new ArrayList<>();
        for (String part : parts) {
            if (part != null && !part.isEmpty()) {
                pathParts.add(part);
            }
        }
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public HeaderMap getHeaderMap() {
        return headerMap;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getPathParts() {
        return pathParts;
    }
}
