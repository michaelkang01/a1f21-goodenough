package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.HttpURLConnection;
import java.net.URI;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

// TODO Please Write Your Tests For CI/CD In This Class. You will see
// these tests pass/fail on github under github actions.
public class AppTest {
    final static String API_URL = "http://localhost:8080";

    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + endpoint))
                                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeAll
    public static void init() {
        ServerComponent servComp = DaggerServerComponent.create();
        Server se = servComp.buildServer();
        ReqHandlerComponent reqComp = DaggerReqHandlerComponent.create();
        ReqHandler rc = reqComp.buildHandler();
        
        se.hts.createContext("/api/v1/", rc);
        se.hts.start();
    }

    @AfterEach
    public void after() {
        System.out.printf("Tested\n");
    }

    @Test
    public void addActorPass() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("name", "Kevin Bacon")
                            .put("actorId", "nm0000102");
        HttpResponse<String> res = sendRequest("/api/v1/addActor", "PUT", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode(), "addActorPass not 200");
    }

    @Test
    public void addActorFail() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("name", "Kevin Bacon");
        HttpResponse<String> res = sendRequest("/api/v1/addActor", "PUT", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode(), "addActorFail not 400.");
    }

    @Test
    public void addMoviePass() throws JSONException, IOException, InterruptedException  {
        JSONObject reqBody = new JSONObject()
                            .put("name", "The Baconator Supreme")
                            .put("movieId", "mm0001337");
        HttpResponse<String> res = sendRequest("/api/v1/addMovie", "PUT", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode(), "addMoviePass not 200");
    }

    @Test
    public void addMovieFail() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("name", "BACONATOR")
                            .put("actorId", "nm0000102");
        HttpResponse<String> res = sendRequest("/api/v1/addMovie", "PUT", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, res.statusCode(), "addMovieFail not 400");
    }

    @Test
    public void addRelationshipPass() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("movieId", "mm0001337")
                            .put("actorId", "nm0000102");
        HttpResponse<String> res = sendRequest("/api/v1/addRelationship", "PUT", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode(), "addRelationshipPass not 200");
    }

    @Test
    public void addRelationshipFail() throws JSONException,  IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("movieId", "DNE")
                            .put("actorId", "nm0000102");
        HttpResponse<String> res = sendRequest("/api/v1/addMovie", "PUT", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, res.statusCode(), "addRelationshipFail not 404");
    }

    @Test
    public void getActorPass() throws JSONException, IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void getActorFail() throws JSONException, IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void getMoviePass() throws JSONException, IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void getMovieFail() throws JSONException, IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void hasRelationshipPass() throws JSONException, IOException, InterruptedException {
        assertTrue(true);
    }

    
    @Test
    public void hasRelationshipFail() throws JSONException, IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void computeBaconNumberPass() throws JSONException, IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void computeBaconNumberFail() throws JSONException, IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void computeBaconPathPass() throws JSONException, IOException, InterruptedException {
        assertTrue(true);
    }

    @Test
    public void computeBaconPathFail() throws JSONException, IOException, InterruptedException {
        assertTrue(true);
    }
}
