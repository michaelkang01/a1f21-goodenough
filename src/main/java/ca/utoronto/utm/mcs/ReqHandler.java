package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javax.inject.Inject;
import org.json.*;

public class ReqHandler implements HttpHandler {

    // TODO Complete This Class
    public Neo4jDAO neo4j;

    @Inject
    public ReqHandler(Neo4jDAO neo4j) {
        this.neo4j = neo4j;
    }

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
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleGet(HttpExchange exchange) throws IOException, JSONException {
        String body = Utils.convert(exchange.getRequestBody());
        JSONObject des = new JSONObject(body);
        String uri = exchange.getRequestURI().getPath();
        String movieId, actorId;
        String res = "";
        switch (uri) {
            case "/api/v1/getActor":
                if (des.length() == 1 && des.has("actorId")) {
                    actorId = des.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    break;
                }
                try {
                    res = this.neo4j.getActor(actorId);
                    if (res.equals("{}") || res.equals("-1")) {
                        exchange.sendResponseHeaders(404, -1);
                        break;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    break;
                }
                break;
            case "/api/v1/getMovie":
                if (des.length() == 1 && des.has("movieId")) {
                    movieId = des.getString("movieId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    break;
                }
                try {
                    res = this.neo4j.getMovie(movieId);
                    if (res.equals("{}") || res.equals("-1")) {
                        exchange.sendResponseHeaders(404, -1);
                        break;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    break;
                }
                break;
            case "/api/v1/hasRelationship":
                if (des.length() == 2 && des.has("movieId") && des.has("actorId")) {
                    movieId = des.getString("movieId");
                    actorId = des.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    break;
                }
                try {
                    res = this.neo4j.hasRelationship(actorId, movieId);
                    if (res.equals("{}") || res.equals("-1")) {
                        exchange.sendResponseHeaders(404, -1);
                        break;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    break;
                }
                break;
            case "/api/v1/computeBaconNumber":
                if (des.length() == 1 && des.has("actorId")) {
                    actorId = des.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    break;
                }
                try {
                    res = this.neo4j.computeBaconNumber(actorId);
                    if (res.equals("{}") || res.equals("-1")) {
                        exchange.sendResponseHeaders(404, -1);
                        break;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    break;
                }
                break;
            case "/api/v1/computeBaconPath":
                if (des.length() == 1 && des.has("actorId")) {
                    actorId = des.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    break;
                }
                try {
                    res = this.neo4j.computeBaconNumber(actorId);
                    if (res.equals("{}") || res.equals("-1")) {
                        exchange.sendResponseHeaders(404, -1);
                        break;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    break;
                }
                break;
            default:
                exchange.sendResponseHeaders(404, -1);
                break;
        }
        exchange.sendResponseHeaders(200, res.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(res.getBytes());
        os.close();
        return;
    }

    public void handlePut(HttpExchange exchange) throws IOException, JSONException{
        String body = Utils.convert(exchange.getRequestBody());
        JSONObject des = new JSONObject(body);
        String uri = exchange.getRequestURI().getPath();
        String name, movieId, actorId;
        int res;
        switch (uri) {
            case "/api/v1/addActor":
                if (des.length() == 2 && des.has("name") && des.has("actorId")) {
                    name = des.getString("name");
                    actorId = des.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    break;
                }
                try {
                    res = this.neo4j.addActor(name, actorId);
                    if (res == -1) {
                        exchange.sendResponseHeaders(400, -1);
                        break;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    break;
                }
                break;
            case "/api/v1/addMovie":
                if (des.length() == 2 && des.has("name") && des.has("movieId")) {
                    name = des.getString("name");
                    movieId = des.getString("movieId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    break;
                }
                try {
                    res = this.neo4j.addMovie(name, movieId);
                    if (res == -1) {
                        exchange.sendResponseHeaders(400, -1);
                        break;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    break;
                }
                break;
            case "/api/v1/addRelationship":
                if (des.length() == 2 && des.has("actorId") && des.has("movieId")) {
                    actorId = des.getString("actorId");
                    movieId = des.getString("movieId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    break;
                }
                try {
                    res = this.neo4j.addRelationship(actorId, movieId);
                    if (res == -1) {
                        exchange.sendResponseHeaders(400, -1);
                        break;
                    }
                    else if (res == -2) {
                        exchange.sendResponseHeaders(404, -1);
                        break;
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    break;
                }
                break;
            default:
                exchange.sendResponseHeaders(404, -1);
                break;
        }
        exchange.sendResponseHeaders(200, -1);
        return;
    }
}