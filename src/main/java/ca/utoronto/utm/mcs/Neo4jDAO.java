package ca.utoronto.utm.mcs;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONObject;
import org.json.JSONArray;
// All your database transactions or queries should 
// go in this class
public class Neo4jDAO {
    // TODO Complete This Class
    
    private final Driver driver;
    Dotenv dotenv = Dotenv.load();
    String addr = dotenv.get("NEO4J_ADDR");
    private final String uriDb = "bolt://"+addr+":7687";
    private final String username = "neo4j";
    private final String password = "123456";

    public Neo4jDAO() {
        this.driver = GraphDatabase.driver(this.uriDb, AuthTokens.basic(this.username, this.password));
    }

    public void close() throws Exception {
        this.driver.close();
    }
    
    //1 means found, -1 beans not found
    private int checkActId(String id) {
        try (Session session = this.driver.session() ) {
            String query = "MATCH (a:actor) WHERE a.id = \"%s\" RETURN a.Name";
            query = String.format(query, id);
            Result result = session.run(query);
            if (result.hasNext()) {
                return 1;
            }
        }
        return -1;
    }
    
    //1 means found, -1 beans not found
    private int checkMovId(String id) {
        try (Session session = this.driver.session() ) {
            String query = "MATCH (m:movie) WHERE m.id = \"%s\" RETURN m.Name";
            query = String.format(query, id);
            Result result = session.run(query);
            if (result.hasNext()) {
                return 1;
            }
        }
        return -1;
    }

    private int checkRelationship(String movieID, String actorID) {
        try (Session session = this.driver.session() ) {
            String query = "MATCH  (a:actor {id: \"%s\"}), (m:movie {id: \"%s\"}) RETURN exists((a)-[:ACTED_IN]-(m))";
            query = String.format(query, actorID, movieID);
            Result result = session.run(query);
            if (result.hasNext()) {
                Record rec = result.next();
                if (rec.get("exists((a)-[:ACTED_IN]-(m))").asBoolean() == true) {
                    return 1;
                }
                else {
                    return -1;
                }
            }
        }
        return -1;
    }

    public int addActor(String name, String actorID) {
        if (checkActId(actorID) == 1) {
            //ActorID is already there, -> 400
            return -1;
        }
        try (Session session = this.driver.session() ) {
            session.writeTransaction(tx -> tx.run(
                "MERGE (n:actor {Name: $name, id: $actorID})",
                parameters("name",name, "actorID", actorID)));
        }
        return 1;
    }

    public int addMovie(String name, String movieID) {
        if (checkMovId(movieID) == 1) {
            //MovieID is already there, -> 400
            return -1;
        }
        try (Session session = this.driver.session() ) {
            session.writeTransaction(tx -> tx.run(
                "MERGE (n:movie {Name: $name, id: $movieID})",
                parameters("name",name, "movieID", movieID)));
        }
        return 1;
    }

    public int addRelationship(String actorID, String movieID) {
        if (checkMovId(movieID) == -1) {
            //MovierID is already there, -> 404
            return -2;
        }
        if (checkActId(actorID) == -1) {
            //ActorID is already there, -> 404
            return -2;
        }
        if (checkRelationship(movieID, actorID) == 1) {
            //Relationship exists.
            return -1;
        }
        try (Session session = this.driver.session() ) {
            session.writeTransaction(tx -> tx.run(
                "MATCH (a:actor), (m:movie) WHERE a.id = $actorID AND m.id = $movieID MERGE ((a)-[r:ACTED_IN]-(m))",
                parameters("actorID",actorID, "movieID", movieID)));
        }
        return 1;
    }

    public String getActor(String actorID) {
        JSONObject toRet = new JSONObject();
        String retStr = "";
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
            try {
                toRet.put("actorId", actRec.get("a.id").asString());
                toRet.put("name", actRec.get("a.Name").asString());
                toRet.put("movies", new JSONArray(movielist));
                retStr = toRet.toString(1);
            } catch (Exception e) {
                System.out.printf("JSON error", e);
            }
        }
        return retStr;
    }

    public String getMovie(String movieID){
        JSONObject toRet = new JSONObject();
        String retStr = "";
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
            try {
                toRet.put("movieId", actRec.get("m.id").asString());
                toRet.put("name", actRec.get("m.Name").asString());
                toRet.put("actors", new JSONArray(actorlist));
                retStr = toRet.toString(1);
            } catch (Exception e) {
                System.out.printf("JSON error", e);
            }
        }
        return retStr;
    }

    public String hasRelationship(String actorID, String movieID) {
        JSONObject toRet = new JSONObject();
        String retStr = "";
        try (Session session = this.driver.session() ) {
            String query = "MATCH (actor {id: \"%s\"})--(movie {id: \"%s\"}) return actor, movie";
            query = String.format(query, actorID, movieID);
            Result result = session.run(query);
            if (result.hasNext()) {
                try {
                    toRet.put("actorId", actorID);
                    toRet.put("movieId", movieID);
                    toRet.put("hasRelationship", true);
                    retStr = toRet.toString(1);
                } catch (Exception e) {
                    System.out.printf("JSON error", e);
                }
            }
        }
        return retStr;
    }
    
    private String getBaconID() {
        String toRet = "";
        try (Session session = this.driver.session() ) {
            String query = "MATCH (a:actor) WHERE a.Name = \"Kevin Bacon\" RETURN a.id";
            Result result = session.run(query);
            if (result.hasNext()) {
                Record rec = result.next();
                toRet = rec.get("a.id").asString();
            }
        }
        return toRet;
    }

    public String computeBaconNumber(String actorID) {
        JSONObject toRet = new JSONObject();
        String retStr = "";
        String baconID = getBaconID();
        if (baconID.equals("")) {
            return "-1";
        }
        try (Session session = this.driver.session() ) {
            int pathCount = -1;
            String query = "MATCH path = shortestPath((b:actor {id:\"%s\"})-[*]-(a:actor {id:\"%s\"})) RETURN length(path)";
            query = String.format(query, actorID, baconID);
            Result result = session.run(query);
            if (result.hasNext()) {
                Record rec = result.next();
                pathCount = rec.get("length(path)").asInt();
            }
            if (pathCount <= 0) {
                return "-1";
            }
            try {
                toRet.put("baconNumber", pathCount / 2);
                retStr = toRet.toString(1);
            } catch (Exception e) {
                System.out.printf("JSON error", e);
            }
        }
        return retStr;
    }

    public String computeBaconPath(String actorID){
        JSONObject toRet = new JSONObject();
        String retStr = "";
        String baconID = getBaconID();
        if (baconID.equals("")) {
            return "-1";
        }
        try (Session session = this.driver.session() ) {
            ArrayList<Object> path = new ArrayList<Object>();
            String query = "MATCH path = shortestPath((b:actor {id:\"%s\"})-[*]-(a:actor {id:\"%s\"})) RETURN [node in nodes(path) | node.id]";
            query = String.format(query, actorID, baconID);
            Result result = session.run(query);
            if (result.hasNext()) {
                Record rec = result.next();
                path = new ArrayList<Object>(rec.get("[node in nodes(path) | node.id]").asList());
            }
            try {
                toRet.put("baconPath", new JSONArray(path));
                retStr = toRet.toString(1);
            } catch (Exception e) {
                System.out.printf("JSON error", e);
            }
        }
        return retStr;
    }
}
