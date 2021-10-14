package ca.utoronto.utm.mcs;

import dagger.Module;
import dagger.Provides;

@Module
public class ReqHandlerModule {
    // TODO Complete This Module
    @Provides
    public Neo4jDAO provideNeo4j() {
        return new Neo4jDAO();
    }
}
