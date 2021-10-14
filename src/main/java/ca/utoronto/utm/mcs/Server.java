package ca.utoronto.utm.mcs;
import com.sun.net.httpserver.HttpServer;
import javax.inject.Inject;

public class Server {
    // TODO Complete This Class
    public HttpServer hts;

    @Inject
    public Server(HttpServer hts) {
        this.hts = hts;
    }

}
