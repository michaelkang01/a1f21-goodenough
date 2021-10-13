package ca.utoronto.utm.mcs;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;

public class App
{
    static int port = 8080;

    public static void main(String[] args) throws IOException
    {
        // TODO Create Your Server Context Here, There Should Only Be One Context
        Neo4jDAO test = new Neo4jDAO();
        test.addActor("Matt", "1000");
        test.addActor("Aaron", "1001");
        test.addActor("Kevin Bacon", "nm0000102");
        test.addMovie("Matt's Great Adventure I", "1000");
        test.addMovie("Matt's Great Adventure II", "1001");
        test.addMovie("Aaaron's Great Adventure I", "1002");
        test.addMovie("Matt and Aaron's Adventure", "1003");
        test.addRelationship("1000", "1000");
        test.addRelationship("1000", "1001");
        test.addRelationship("1001", "1002");
        test.addRelationship("1000", "1003");
        test.addRelationship("1001", "1003");
        test.addRelationship("nm0000102", "1002");
        System.out.printf("Server started on port %d\n", port);

        // This code is used to get the neo4j address, you must use this so that we can mark :)
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
        System.out.println(addr);
        System.out.printf(test.getActor("1000"));
        System.out.printf(test.getMovie("1002"));
        System.out.printf(test.hasRelationship("1000", "1003"));
        System.out.printf(test.computeBaconNumber("1000"));
        System.out.printf(test.computeBaconPath("1000"));
    }
}
