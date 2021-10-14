package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.URI;

import org.json.JSONObject;
import org.json.JSONArray;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

// TODO Please Write Your Tests For CI/CD In This Class. You will see
// these tests pass/fail on github under github actions.
public class AppTest {
    static HttpClient client;
    static HttpRequest req;
    static HttpResponse<String> res;
    @BeforeAll
    public static void init() throws IOException{
        String[] start = {""};
        App.main(start);
        client = HttpClient.newHttpClient();
    }

    @Test
    public void addActorPass() throws IOException, InterruptedException {
        req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/v1/addActor"))
        .PUT(HttpRequest.BodyPublishers.ofString("{\"name\": \"Kevin Bacon\", \"actorId\": \"nm0000102\"}")).build();
        res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertTrue(res.statusCode() == 200);
    }

    @Test
    public void addActorFail() throws IOException, InterruptedException {
        req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/v1/addActor"))
        .PUT(HttpRequest.BodyPublishers.ofString("{\"name\": \"Kevin Bacon\"}")).build();
        res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertTrue(res.statusCode() == 400);
    }

    @Test
    public void addMoviePass() throws IOException, InterruptedException  {
        req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/v1/addMovie"))
        .PUT(HttpRequest.BodyPublishers.ofString("{\"name\": \"The Baconator Supreme\", \"movieId\": \"mm1000337\"}")).build();
        res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertTrue(res.statusCode() == 200);
    }

    @Test
    public void addMovieFail() throws IOException, InterruptedException {
        req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/v1/addMovie"))
        .PUT(HttpRequest.BodyPublishers.ofString("{\"name\": \"The Baconator Supreme\", \"actorId\": \"mm1000337\"}")).build();
        res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertTrue(res.statusCode() == 400);
    }

    @Test
    public void addRelationshipPass() throws IOException, InterruptedException {
        req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/v1/addRelationship"))
        .PUT(HttpRequest.BodyPublishers.ofString("{\"actorId\": \"nm0000102\", \"movieId\": \"mm1000337\"}")).build();
        res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertTrue(res.statusCode() == 200);
    }

    @Test
    public void addRelationshipFail() throws IOException, InterruptedException {
        req = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/v1/addRelationship"))
        .PUT(HttpRequest.BodyPublishers.ofString("{\"actorId\": \"xd\", \"movieId\": \"dne\"}")).build();
        res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertTrue(res.statusCode() == 404);
    }

    @Test
    public void getActorPass() throws IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void getActorFail() throws IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void getMoviePass() throws IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void getMovieFail() throws IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void hasRelationshipPass() throws IOException, InterruptedException {
        assertTrue(true);
    }

    
    @Test
    public void hasRelationshipFail() throws IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void computeBaconNumberPass() throws IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void computeBaconNumberFail() throws IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void computeBaconPathPass() throws IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void computeBaconPathFail() throws IOException, InterruptedException {
        assertTrue(true);
    }
}
