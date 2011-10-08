package com.codeaffine.example.rwt.osgi.ui.platform;

import org.eclipse.rwt.SessionSingletonBase;


public class SessionAwareServiceProvider implements ServiceProvider {

  @Override
  public <T> T register( Class<T> service, T instance ) {
    return getProvider().register( service, instance );
  }

  @Override
  public <T> T get( Class<T> service ) {
    return getProvider().get( service );
  }
  
  private ServiceProvider getProvider() {
    return ( ServiceProvider )SessionSingletonBase.getInstance( ServiceProviderImpl.class );
  }
}
