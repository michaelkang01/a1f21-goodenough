package ca.utoronto.utm.mcs;
import dagger.Module;
import dagger.Provides;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

@Module
public class ServerModule {
    // TODO Complete This Module
    @Provides
    public HttpServer provideHttpServer() {
        try {
            return HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);
        } catch (Exception e) {
            return null;
        }
    }

}
