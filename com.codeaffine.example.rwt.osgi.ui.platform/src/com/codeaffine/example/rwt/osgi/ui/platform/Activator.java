package com.codeaffine.example.rwt.osgi.ui.platform;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class Activator implements BundleActivator {

  private ServiceRegistration<ServiceProvider> registration;

  @Override
  public void start( BundleContext context ) throws Exception {
    SessionAwareServiceProvider serviceProvider = new SessionAwareServiceProvider();
    registration = context.registerService( ServiceProvider.class, serviceProvider, null );
  }

  @Override
  public void stop( BundleContext context ) throws Exception {
    registration.unregister();
  }
}
