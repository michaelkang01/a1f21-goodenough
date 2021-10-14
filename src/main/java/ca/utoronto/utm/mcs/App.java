package ca.utoronto.utm.mcs;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
public class App
{
    /**
     * Runs the Server and Request handler for the api.
     * @param args String[] object containing configuration.
     */
    public static void main(String[] args) throws IOException
    {
        //Setup Server and Request Handler
        ServerComponent servComp = DaggerServerComponent.create();
        Server se = servComp.buildServer();
        ReqHandlerComponent reqComp = DaggerReqHandlerComponent.create();
        ReqHandler rc = reqComp.buildHandler();
        //Setup context
        se.hts.createContext("/api/v1/", rc);
        se.hts.start();
        
        // This code is used to get the neo4j address, you must use this so that we can mark :)
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
        System.out.println(addr);
    }
}
