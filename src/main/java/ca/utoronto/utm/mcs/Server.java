package ca.utoronto.utm.mcs;
import com.sun.net.httpserver.HttpServer;
import javax.inject.Inject;

public class Server {
    //HttpServer for our server
    public HttpServer hts;

    /**
     * Server constructor, dependency injection handled by Dagger2
     */
    @Inject
    public Server(HttpServer hts) {
        this.hts = hts;
    }

}
