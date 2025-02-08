package at.fhtw.sampleapp.service.weather;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Controller;

public class WeatherService implements Controller {
    private final WeatherController weatherController;

    public WeatherService() {
        this.weatherController = new WeatherController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == HttpMethod.GET &&
            request.getPathParts().size() > 1) {
            return this.weatherController.getWeather(request.getPathParts().get(1));
        }

        if (request.getMethod() == HttpMethod.GET) {
            return this.weatherController.getWeatherPerRepository();
            //return this.weatherController.getWeatherPerRepository();
        }

        if (request.getMethod() == HttpMethod.POST) {
            return this.weatherController.addWeather(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
