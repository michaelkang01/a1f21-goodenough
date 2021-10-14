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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.InterruptedException;

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
    public static void init() throws InterruptedException, IOException, JSONException{
        ServerComponent servComp = DaggerServerComponent.create();
        Server se = servComp.buildServer();
        ReqHandlerComponent reqComp = DaggerReqHandlerComponent.create();
        ReqHandler rc = reqComp.buildHandler();
        
        se.hts.createContext("/api/v1/", rc);
        se.hts.start();
        
        rc.neo4j.delete_all_nodes();

        JSONObject reqBody = new JSONObject()
                            .put("name", "Michael Kang")
                            .put("actorId", "1000");
        sendRequest("/api/v1/addActor", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                            .put("name", "Michael's Adventure")
                            .put("movieId", "m1000");
        sendRequest("/api/v1/addMovie", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                            .put("name", "The Baconator")
                            .put("movieId", "m1001");
        sendRequest("/api/v1/addMovie", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                            .put("name", "Kevin Bacon")
                            .put("actorId", "nm0000102");
        sendRequest("/api/v1/addActor", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                            .put("movieId", "m1000")
                            .put("actorId", "nm0000102");
        sendRequest("/api/v1/addRelationship", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                            .put("movieId", "m1000")
                            .put("actorId", "1000");
        sendRequest("/api/v1/addRelationship", "PUT", reqBody.toString());

        reqBody = new JSONObject()
                            .put("name", "Nigow King")
                            .put("actorId", "1003");
                    sendRequest("/api/v1/addActor", "PUT", reqBody.toString());
    }
    @Test
    public void addActorPass() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("name", "Aaron Simpson")
                            .put("actorId", "1001");
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
        assertTrue(true);
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
                            .put("movieId", "m1001")
                            .put("actorId", "nm0000102");
        HttpResponse<String> res = sendRequest("/api/v1/addRelationship", "PUT", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode(), "addRelationshipPass not 200");
    }

    @Test
    public void addRelationshipFail() throws JSONException,  IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("movieId", "DNE")
                            .put("actorId", "DNE");
        HttpResponse<String> res = sendRequest("/api/v1/addRelationship", "PUT", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, res.statusCode(), "addRelationshipFail not 404");
    }

    @Test
    public void getActorPass() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("actorId", "1000");
        JSONObject resExpect = new JSONObject()
                            .put("actorId", "1000")
                            .put("name", "Michael Kang")
                            .put("movies",new JSONArray("[m1000]"));
        HttpResponse<String> res = sendRequest("/api/v1/getActor", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode(), "getActorPass not 200");
        assertEquals(resExpect.toString(1), res.body(), "getActorPass body not expected.");
    }

    @Test
    public void getActorFail() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("actorId", "DNE");
        HttpResponse<String> res = sendRequest("/api/v1/getActor", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, res.statusCode(), "getActorFail not 404");
    }

    @Test
    public void getMoviePass() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("movieId", "m1000");
        JSONObject resExpect = new JSONObject()
                            .put("movieId", "m1000")
                            .put("name", "Michael's Adventure")
                            .put("actors",new JSONArray("[1000, nm0000102]"));
        JSONObject resExpect2 = new JSONObject()
                            .put("movieId", "m1000")
                            .put("name", "Michael's Adventure")
                            .put("actors",new JSONArray("[nm0000102, 1000]"));
        HttpResponse<String> res = sendRequest("/api/v1/getMovie", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode(), "getMoviePass not 200");
        assertTrue(resExpect.toString(1).equals(res.body()) || resExpect2.toString(1).equals(res.body()), "getMoviePass body not expected.");
    }

    @Test
    public void getMovieFail() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("movieId", "DNE");
        HttpResponse<String> res = sendRequest("/api/v1/getMovie", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, res.statusCode(), "getMovieFail not 404");
    }

    @Test
    public void hasRelationshipPass() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("movieId", "m1001")
                            .put("actorId", "1000");
        JSONObject resExpect = new JSONObject()
                            .put("movieId", "m1001")
                            .put("actorId", "1000")
                            .put("hasRelationship", false);
        HttpResponse<String> res = sendRequest("/api/v1/hasRelationship", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode(), "hasRelationshipPass not 200");
        assertEquals(resExpect.toString(1), res.body(), "hasRelationshipPass body not expected.");
    }

    @Test
    public void hasRelationshipFail() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                            .put("movieId", "m1001")
                            .put("actorId", "DNE");
        HttpResponse<String> res = sendRequest("/api/v1/hasRelationship", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, res.statusCode(), "hasRelationshipFail not 404");
    }

    @Test
    public void computeBaconNumberPass() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                .put("actorId", "1000");
        JSONObject resExpect = new JSONObject()
                .put("baconNumber", 1);
        HttpResponse<String> res = sendRequest("/api/v1/computeBaconNumber", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode(), "computeBaconNumberPass not 200");
        assertEquals(resExpect.toString(1), res.body(), "computeBaconNumberPass body not expected.");
    }

    @Test
    public void computeBaconNumberFail() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                .put("actorId", "1003");
        HttpResponse<String> res = sendRequest("/api/v1/computeBaconNumber", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, res.statusCode(), "computeBaconNumberFail not 404");
    }

    @Test
    public void computeBaconPathPass() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                .put("actorId", "1000");
        JSONObject resExpect = new JSONObject()
                .put("baconPath", new JSONArray("[1000, m1000, nm0000102]"));
        HttpResponse<String> res = sendRequest("/api/v1/computeBaconPath", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_OK, res.statusCode(), "computeBaconPathPass not 200");
        assertEquals(resExpect.toString(1), res.body(),"computeBaconPathPass body not expected.");
    }

    @Test
    public void computeBaconPathFail() throws JSONException, IOException, InterruptedException {
        JSONObject reqBody = new JSONObject()
                .put("actorId", "1003");
        HttpResponse<String> res = sendRequest("/api/v1/computeBaconPath", "GET", reqBody.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, res.statusCode(), "computeBaconPathPass not 404");
    }
}
