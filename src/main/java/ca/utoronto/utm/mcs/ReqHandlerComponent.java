package ca.utoronto.utm.mcs;

import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = ReqHandlerModule.class)
public interface ReqHandlerComponent {
    /**
     * Builder component for ReqHandler handled by Dagger2.
     */
    public ReqHandler buildHandler();
}
