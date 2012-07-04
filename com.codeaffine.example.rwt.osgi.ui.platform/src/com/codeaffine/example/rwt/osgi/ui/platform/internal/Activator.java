package com.codeaffine.example.rwt.osgi.ui.platform.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.codeaffine.example.rwt.osgi.ui.platform.ServiceProvider;


public class Activator implements BundleActivator {

  private ServiceRegistration<ServiceProvider> serviceProviderRegistration;
  private ServiceRegistration<UIContributorTrackerService> trackerServiceRegistration;
  private static BundleContext context;

  @Override
  public void start( BundleContext context ) throws Exception {
    Activator.context = context;
    registerServiceProvider( context );
    registerTrackerService( context );
  }

  private void registerTrackerService( BundleContext context ) {
    UIContributorTrackerService trackerService = new UIContributorTrackerService( context );
    Class<UIContributorTrackerService> type = UIContributorTrackerService.class;
    trackerServiceRegistration = context.registerService( type, trackerService, null );
  }

  private void registerServiceProvider( BundleContext context ) {
    SessionAwareServiceProvider serviceProvider = new SessionAwareServiceProvider();
    Class<ServiceProvider> type = ServiceProvider.class;
    serviceProviderRegistration = context.registerService( type, serviceProvider, null );
  }

  @Override
  public void stop( BundleContext context ) throws Exception {
    Activator.context = null;
    serviceProviderRegistration.unregister();
    trackerServiceRegistration.unregister();
  }

  public static BundleContext getBundleContext() {
    return context;
  }
}
