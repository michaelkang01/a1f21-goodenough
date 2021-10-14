package ca.utoronto.utm.mcs;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = ServerModule.class)
public interface ServerComponent {
    /**
     * Builder component for ReqHandler handled by Dagger2.
     */
	public Server buildServer();
}
