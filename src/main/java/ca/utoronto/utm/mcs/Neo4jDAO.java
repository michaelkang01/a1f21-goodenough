package ca.utoronto.utm.mcs;
import org.neo4j.driver.*;
import static org.neo4j.driver.Values.parameters;
import io.github.cdimascio.dotenv.Dotenv;
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
                parameters("name",name, "actorID", movieID)));
        }
        return;
    }

    public void addRelationship(String actorID, String movieID) {
        try (Session session = this.driver.session() ) {
            session.writeTransaction(tx -> tx.run(
                "MATCH ((a:actor {actorID: $actorID}), (m:movie {movieID: $movieID}) MERGE ((a)-[r:ACTED_IN]-(m))",
                parameters("actorID",actorID, "movieID", movieID)));
        }
        return;
    }

    public void getActor(String actorID) {
        return;
    }

    public void getMovie(String movieID) {
        return;
    }

    public void hasRelationship(String actorID, String movieID) {
        return;
    }

    public void computeBaconNumber(String actorID) {
        return;
    }

    public void computeBaconPath(String actorID) {
        return;
    }
}
