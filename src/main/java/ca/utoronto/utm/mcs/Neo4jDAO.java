package ca.utoronto.utm.mcs;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
// All your database transactions or queries should 
// go in this class
public class Neo4jDAO {
    // TODO Complete This Class
    
    private final Driver driver;
    Dotenv dotenv = Dotenv.load();
    String addr = dotenv.get("NEO4J_ADDR");
    private final String uriDb = "neo4j://"+addr+":7687";
    private final String username = "neo4j";
    private final String password = "123456";

    public Neo4jDAO() {
        this.driver = GraphDatabase.driver(this.uriDb, AuthTokens.basic(this.username, this.password));
    }

    public void close() throws Exception {
        this.driver.close();
    }

    public void addActor(String name, String actorID) {
        try (Session session = this.driver.session() ) {
            session.writeTransaction(tx -> tx.run(
                "MERGE (n:actor {Name: $name, id: $actorID})",
                parameters("name",name, "actorID", actorID)));
        }
        return;
    }

    public void addMovie(String name, String movieID) {
        try (Session session = this.driver.session() ) {
            session.writeTransaction(tx -> tx.run(
                "MERGE (n:movie {Name: $name, id: $movieID})",
                parameters("name",name, "movieID", movieID)));
        }
        return;
    }

    public void addRelationship(String actorID, String movieID) {
        try (Session session = this.driver.session() ) {
            session.writeTransaction(tx -> tx.run(
                "MATCH (a:actor), (m:movie) WHERE a.id = $actorID AND m.id = $movieID MERGE ((a)-[r:ACTED_IN]-(m))",
                parameters("actorID",actorID, "movieID", movieID)));
        }
        return;
    }

    public String getActor(String actorID) throws JSONException {
        JSONObject toRet = new JSONObject();
        try (Session session = this.driver.session() ) {
            String query = "MATCH (a:actor) WHERE a.id = \"%s\" RETURN a.id, a.Name";
            query = String.format(query, actorID);
            Result result = session.run(query);
            String query2 = "MATCH (actor {id: \"%s\"})--(m:movie) RETURN m.id";
            query2 = String.format(query2, actorID);
            Result result2 = session.run(query2);
            Record actRec = result.next();
            ArrayList<String> movielist = new ArrayList<String>();
            while (result2.hasNext()) {
                Record rec = result2.next();
                movielist.add(rec.get("m.id").asString());
            }
            toRet.put("actorId", actRec.get("a.id").asString());
            toRet.put("name", actRec.get("a.Name").asString());
            toRet.put("movies", new JSONArray(movielist));
        }
        return toRet.toString();
    }

    public String getMovie(String movieID) throws JSONException{
        JSONObject toRet = new JSONObject();
        try (Session session = this.driver.session() ) {
            String query = "MATCH (m:movie) WHERE m.id = \"%s\" RETURN m.id, m.Name";
            query = String.format(query, movieID);
            Result result = session.run(query);
            String query2 = "MATCH (movie {id: \"%s\"})--(a:actor) RETURN a.id";
            query2 = String.format(query2, movieID);
            Result result2 = session.run(query2);

            ArrayList<String> actorlist = new ArrayList<String>();
            while (result2.hasNext()) {
                Record rec = result2.next();
                actorlist.add(rec.get("a.id").asString());
            }
            Record actRec = result.next();
            toRet.put("movieId", actRec.get("m.id").asString());
            toRet.put("name", actRec.get("m.Name").asString());
            toRet.put("actors", new JSONArray(actorlist));
        }
        return toRet.toString();
    }

    public String hasRelationship(String actorID, String movieID) throws JSONException{
        JSONObject toRet = new JSONObject();
        try (Session session = this.driver.session() ) {
            String query = "MATCH (actor {id: \"%s\"})--(movie {id: \"%s\"}) return actor, movie";
            query = String.format(query, actorID, movieID);
            Result result = session.run(query);
            if (result.hasNext()) {
                toRet.put("actorId", actorID);
                toRet.put("movieId", movieID);
                toRet.put("hasRelationship", true);
            }
        }
        return toRet.toString();
    }

    public String computeBaconNumber(String actorID) throws JSONException{
        JSONObject toRet = new JSONObject();
        try (Session session = this.driver.session() ) {
            int pathCount = -1;
        }
        return toRet.toString();
    }

    public String computeBaconPath(String actorID) throws JSONException{
        JSONObject toRet = new JSONObject();
        try (Session session = this.driver.session() ) {
            ArrayList<String> path = new ArrayList<String>();
            }
        return toRet.toString();
    }
}
