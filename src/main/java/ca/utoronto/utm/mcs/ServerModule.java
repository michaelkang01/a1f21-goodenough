package ca.utoronto.utm.mcs;
import dagger.Module;
import dagger.Provides;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

@Module
public class ServerModule {
    /**
     * Provides HttpServer object for Server, handled by Dagger2
     * @return HttpServer object binded on localhost with port 8080.
     */
    @Provides
    public HttpServer provideHttpServer() {
        HttpServer hts;
        try {
            hts = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
        }
        catch (Exception e) {
            hts = null;
        }
        return hts;
    }

}
