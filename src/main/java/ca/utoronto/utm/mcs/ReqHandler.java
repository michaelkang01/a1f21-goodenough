package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javax.inject.Inject;
import org.json.*;

public class ReqHandler implements HttpHandler {

    public Neo4jDAO neo4j;
    /**
     * ReqHandler contructor, depedency injection handled by Dagger2.
     * @param neo4j Neo4jDAO object used to operate on the neo4j database.
     */
    @Inject
    public ReqHandler(Neo4jDAO neo4j) {
        this.neo4j = neo4j;
    }
    /**
     * Checks if the request is a GET, PUT, or otherwise and routes accordingly.
     * Sends Http response header 404 if not GET or PUT request, 500 is error occurs.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    this.handleGet(exchange);
                    break;
                case "PUT":
                    this.handlePut(exchange);
                    break;
                default:
                    exchange.sendResponseHeaders(404, -1);
                    return;
            }
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
            e.printStackTrace();
            return;
        }
    }

    /**
     * If GET request is sent, will retrieve the proper data from neo4j according to request.
     * Will send back responseHeaders, 400 on bad input, 404 on not found, 200 on success accompanied by a responseBody, 500 on server error.
     * @param exchange HttpExchange object that contains request information.
     */
    public void handleGet(HttpExchange exchange) throws IOException, JSONException {
        String body;
        JSONObject des;
        try {
            body = Utils.convert(exchange.getRequestBody());
            des = new JSONObject(body);
        } catch (Exception e) {
            exchange.sendResponseHeaders(400,-1);
            e.printStackTrace();
            return;
        }
        String uri = exchange.getRequestURI().getPath();
        String movieId, actorId;
        String res = "";
        switch (uri) {
            //getActor
            case "/api/v1/getActor":
                if (des.has("actorId")) {
                    actorId = des.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                try {
                    res = this.neo4j.getActor(actorId);
                    if (res.equals("{}") || res.equals("-1")) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                break;
            //getMovie
            case "/api/v1/getMovie":
                if (des.has("movieId")) {
                    movieId = des.getString("movieId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                try {
                    res = this.neo4j.getMovie(movieId);
                    if (res.equals("{}") || res.equals("-1")) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                break;
            //hasRelationship
            case "/api/v1/hasRelationship":
                if (des.has("movieId") && des.has("actorId")) {
                    movieId = des.getString("movieId");
                    actorId = des.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                try {
                    res = this.neo4j.hasRelationship(actorId, movieId);
                    if (res.equals("{}") || res.equals("-1")) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                break;
            //computeBaconNumber
            case "/api/v1/computeBaconNumber":
                if (des.has("actorId")) {
                    actorId = des.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                try {
                    res = this.neo4j.computeBaconNumber(actorId);
                    if (res.equals("{}") || res.equals("-1")) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                break;
            //computeBaconPath
            case "/api/v1/computeBaconPath":
                if (des.has("actorId")) {
                    actorId = des.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                try {
                    res = this.neo4j.computeBaconPath(actorId);
                    if (res.equals("{}") || res.equals("-1")) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                break;
            default:
                exchange.sendResponseHeaders(404, -1);
                return;
        }
        exchange.sendResponseHeaders(200, res.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(res.getBytes());
        os.close();
        return;
    }

    /**
     * If PUT request is sent, will send the proper data from neo4j according to request.
     * Will send back responseHeaders, 400 on bad input, 404 on not found, 200 on successful addition to database, 500 on server error.
     * @param exchange HttpExchange object that contains request information.
     */
    public void handlePut(HttpExchange exchange) throws IOException, JSONException{
        String body;
        JSONObject des;
        try {
            body = Utils.convert(exchange.getRequestBody());
            des = new JSONObject(body);
        } catch (Exception e) {
            exchange.sendResponseHeaders(400,0);
            return;
        }
        String uri = exchange.getRequestURI().getPath();
        String name, movieId, actorId;
        int res;
        switch (uri) {
            //addActor
            case "/api/v1/addActor":
                if (des.has("name") && des.has("actorId")) {
                    name = des.getString("name");
                    actorId = des.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                try {
                    res = this.neo4j.addActor(name, actorId);
                    if (res == -1) {
                        exchange.sendResponseHeaders(400, -1);
                        return;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                break;
            //addMovie
            case "/api/v1/addMovie":
                if (des.has("name") && des.has("movieId")) {
                    name = des.getString("name");
                    movieId = des.getString("movieId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                try {
                    res = this.neo4j.addMovie(name, movieId);
                    if (res == -1) {
                        exchange.sendResponseHeaders(400, -1);
                        return;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                break;
            //addRelationship
            case "/api/v1/addRelationship":
                if (des.has("actorId") && des.has("movieId")) {
                    actorId = des.getString("actorId");
                    movieId = des.getString("movieId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }
                try {
                    res = this.neo4j.addRelationship(actorId, movieId);
                    if (res == -1) {
                        exchange.sendResponseHeaders(400, -1);
                        return;
                    }
                    else if (res == -2) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                break;
            default:
                exchange.sendResponseHeaders(404, -1);
                return;
        }
        exchange.sendResponseHeaders(200, -1);
        return;
    }
}