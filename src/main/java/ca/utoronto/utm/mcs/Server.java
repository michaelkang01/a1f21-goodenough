package ca.utoronto.utm.mcs;
import com.sun.net.httpserver.HttpServer;
import javax.inject.Inject;
import java.io.IOException;

public class Server {
    // TODO Complete This Class
    public HttpServer hts;

    @Inject
    public Server(HttpServer hts) throws IOException {
        this.hts = hts;
    }

}
