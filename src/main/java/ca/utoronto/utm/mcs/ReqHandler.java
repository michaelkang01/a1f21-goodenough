package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javax.inject.Inject;

public class ReqHandler implements HttpHandler {

    // TODO Complete This Class
    private Neo4jDAO neo4j;

    @Inject
    public ReqHandler(Neo4jDAO neo4j) {
        this.neo4j = neo4j;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
    }
}