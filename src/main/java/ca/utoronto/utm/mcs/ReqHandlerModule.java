package ca.utoronto.utm.mcs;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ReqHandlerModule {
    // TODO Complete This Module
    @Provides
    @Singleton
    public Neo4jDAO provideNeo4j() {
        return new Neo4jDAO();
    }
}
