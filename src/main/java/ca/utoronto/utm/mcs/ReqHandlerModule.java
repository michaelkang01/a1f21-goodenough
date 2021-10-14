package ca.utoronto.utm.mcs;

import dagger.Module;
import dagger.Provides;

@Module
public class ReqHandlerModule {
     /**
     * Provides Neo4jDAO object for ReqHandler, handled by Dagger2
     * @return Neo4jDAO object.
     */
    @Provides
    public Neo4jDAO provideNeo4jDAO() {
        return new Neo4jDAO();
    }
}
