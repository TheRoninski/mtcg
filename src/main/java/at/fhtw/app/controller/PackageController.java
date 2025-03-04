package at.fhtw.app.controller;

import at.fhtw.app.service.IPackageService;
import at.fhtw.app.service.IUserService;
import at.fhtw.app.service.AuthenticationService;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpMethod;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

import java.util.Map;

public class PackageController extends AbstractController {

    private final IPackageService packageService;
    private final IUserService userService;
    private final AuthenticationService authenticationService;

    public PackageController(IPackageService packageService, IUserService userService, AuthenticationService authenticationService) {
        this.packageService = packageService;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Override
    public Response handleRequest(Request request) {
        String route = request.getServiceRoute();
        HttpMethod method = request.getMethod();
        String authToken = request.getHeaderMap().getHeader("Authorization");
        System.out.println("?????????????????????????????????????" + request.getHeaderMap());
        String content = request.getBody();
        if ("/packages".equals(route)) {
            // Admin endpoint: Create a new package.
            return createPackage(content, authToken);
        } else if ("/transactions/packages".equals(route)) {
            // User endpoint: Buy (assign) a package.
            return assignPackage(authToken);
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"Not Found\"}");
    }

    private Response createPackage(String content, String authToken) {
        // For simplicity, assume that package creation is allowed if the user is admin.
        authenticationService.authenticateUser(authToken, true);
        // Parse the content into a list of RawRequestCard.
        var rawCards = parseContent(content, java.util.List.class); // You may need a proper type reference.
        // In a real scenario, use mapper.convertValue(...)
        // Here, we assume rawCards is parsed correctly.
        var newCards = packageService.createNewPackage(rawCards);
        if (packageService.savePackage(newCards)) {
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\":\"Package and cards successfully created\"}");
        } else {
            return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"message\":\"At least one card in the package already exists\"}");
        }
    }

    private Response assignPackage(String authToken) {
        var user = authenticationService.authenticateUser(authToken, false);
        if (!packageService.isPackageAvailable()) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\":\"No card package available for buying\"}");
        }
        if (!userService.payPackage(user.id(), 5)) {
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "{\"message\":\"Not enough money for buying a card package\"}");
        }
        String result = packageService.assignUserToPackage(user.id());
        return new Response(HttpStatus.OK, ContentType.JSON, result);
    }
}
