package ca.utoronto.utm.mcs;

import java.util.ArrayList;

import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONObject;
import org.json.JSONArray;

public class Neo4jDAO {

    //Neo4j variables.
    private final Driver driver;
    Dotenv dotenv = Dotenv.load();
    String addr = dotenv.get("NEO4J_ADDR");
    private final String uriDb = "bolt://"+addr+":7687";
    private final String username = "neo4j";
    private final String password = "123456";

    /**
     * Neo4jDAO contructor, sets up driver using built in authentication.
     */
    public Neo4jDAO() {
        this.driver = GraphDatabase.driver(this.uriDb, AuthTokens.basic(this.username, this.password));
    }

    /**
     * Method to close Neo4jDAO driver.
     */
    public void close() throws Exception {
        this.driver.close();
    }
    //Note, it is standard (to my knowledge, to not need to document private methods)
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
    //1 means relationship exists, -1 means does not.
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
    /**
     * Writes an actor node to the neo4j database.
     * @param name String object containing the name of the actor.
     * @param actorId String object containing identifier of the actor.
     * @return int -1 if the actor (by id) already exists, 1 if successfully written.
     */
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

    /**
     * Writes an movie node to the neo4j database.
     * @param name String object containing the name of the movie.
     * @param movieId String object containing identifier of the movie.
     * @return int -1 if the movie (by id) already exists, 1 if successfully written.
     */
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

    
    /**
     * Writes a relationship from an actor to a movie that they acted in..
     * @param actorId String object containing identifier of the actor.
     * @param movieId String object containing identifier of the movie.
     * @return int -1 if the relationship already exists, -2 if either the actor or movie do not exist, 1 if successfully written.
     */
    public int addRelationship(String actorID, String movieID) {
        if (checkMovId(movieID) == -1) {
            //MovierID does not exist -> 404
            return -2;
        }
        if (checkActId(actorID) == -1) {
            //ActorID does not exist -> 404
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

    /**
     * Retrieves id and name of actor, as well as movies an actor acted in.
     * @param actorId String object containing identifier of the actor.
     * @return String "-1" if actor does not exist. String in JSON-like form containing name, actorId, and movies array.
     */
    public String getActor(String actorID) {
        JSONObject toRet = new JSONObject();
        String retStr = "";
        try (Session session = this.driver.session() ) {
            String query = "MATCH (a:actor) WHERE a.id = \"%s\" RETURN a.id, a.Name";
            query = String.format(query, actorID);
            Result result = session.run(query);
            if (!result.hasNext()) {
                return "-1";
            }
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
                toRet.put("movies", new JSONArray(movielist.toString()));
                retStr = toRet.toString(1);
            } catch (Exception e) {
                System.out.printf("JSON error", e);
            }
        }
        return retStr;
    }

    
    /**
     * Retrieves id and name of movie, as well as actors that acted in the movie.
     * @param movieId String object containing identifier of the movie.
     * @return String "-1" if movie does not exist. String in JSON-like form containing name, movieId, and actors array.
     */
    public String getMovie(String movieID){
        JSONObject toRet = new JSONObject();
        String retStr = "-1";
        if (checkMovId(movieID) == -1) {
            return "-1";
        }
        try (Session session = this.driver.session() ) {
            String query = "MATCH (m:movie) WHERE m.id = \"%s\" RETURN m.id, m.Name";
            query = String.format(query, movieID);
            Result result = session.run(query);
            if (!result.hasNext()) {
                return "-1";
            }
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
                toRet.put("actors", new JSONArray(actorlist.toString()));
                retStr = toRet.toString(1);
            } catch (Exception e) {
                System.out.printf("JSON error", e);
            }
        }
        return retStr;
    }

    /**
     * Retrieves id of movie, actor, and if they have a relationship.
     * @param actorId String object containing identifier of the actor.
     * @param movieId String object containing identifier of the movie.
     * @return String "-1" if movie or actor do not exist. String in JSON-like form containing actorId, movieId, and hasRelationship boolean.
     */
    public String hasRelationship(String actorID, String movieID) {
        JSONObject toRet = new JSONObject();
        String retStr = "-1";
        if (checkActId(actorID) == -1 || checkMovId(movieID) == -1) {
            return "-1";
        }
        try (Session session = this.driver.session() ) {
            String query = "MATCH (actor {id: \"%s\"})--(movie {id: \"%s\"}) return actor, movie";
            query = String.format(query, actorID, movieID);
            Result result = session.run(query);
            if (!result.hasNext()) {
                try {
                    toRet.put("actorId", actorID);
                    toRet.put("movieId", movieID);
                    toRet.put("hasRelationship", false);
                    retStr = toRet.toString(1);
                } catch (Exception e) {
                    System.out.printf("JSON error", e);
                }
            }
            else {
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
    
    //Gets the id of Kevin Bacon, in case it changes.
    private String getBaconID() {
        String toRet = "-1";
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

    /**
     * Retrieves number of actors the actor in question is from Kevin Bacon
     * @param actorId String object containing identifier of the actor.
     * @return String "-1" if actor does not exist, Kevin Bacon does not exist, or no path to Kevin Bacon exists. String in JSON-like form containing baconNumber.
     */
    public String computeBaconNumber(String actorID) {
        JSONObject toRet = new JSONObject();
        String retStr = "-1";
        String baconID = getBaconID();
        if (baconID.equals("-1")) {
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

    /**
     * Retrieves shortest path of nodes by id from an actor to Kevin Bacon
     * @param actorId String object containing identifier of the actor.
     * @return String "-1" if actor does not exist, Kevin Bacon does not exist, or no path to Kevin Bacon exists. String in JSON-like form containing baconPath array.
     */
    public String computeBaconPath(String actorID){
        JSONObject toRet = new JSONObject();
        String retStr = "-1";
        String baconID = getBaconID();
        if (baconID.equals("-1")) {
            return "-1";
        }
        try (Session session = this.driver.session() ) {
            ArrayList<String> path = new ArrayList<String>();
            String query = "MATCH path = shortestPath((b:actor {id:\"%s\"})-[*]-(a:actor {id:\"%s\"})) UNWIND [node in nodes(path)] AS n RETURN n.id";
            query = String.format(query, actorID, baconID);
            Result result = session.run(query);
            if (!result.hasNext()) {
                return "-1";
            }
            while (result.hasNext()) {
                Record rec = result.next();
                try {
                    path.add(rec.get("n.id").asString());
                } catch (Exception e) {
                    System.out.printf("Conversion Error");
                }
            }
            try {
                toRet.put("baconPath", new JSONArray(path.toString()));
                retStr = toRet.toString(1);
            } catch (Exception e) {
                System.out.printf("JSON error", e);
            }
        }
        return retStr;
    }

    /**
     * Deletes all nodes in neo4j database.
     */
    public void delete_all_nodes() {
        try (Session session = this.driver.session() ) {
            String query = "MATCH (n) DETACH DELETE n";
            session.run(query);
        } catch (Exception e) {
            System.out.printf("Error", e);
        }
        return;
    }
}
