package com.codeaffine.example.rwt.osgi.ui.platform;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


public class ConfiguratorTracker
  extends ServiceTracker<ApplicationConfigurator, ApplicationConfigurator>
{
  
  private static final String CONFIGURATOR_REFERENCE = "ApplicationReference";

  private final ApplicationConfiguration configuration;
  private final ApplicationConfigurator configurator;

  public ConfiguratorTracker( ApplicationConfigurator configurator,
                              ApplicationConfiguration configuration )
  {
    super( getBundleContext(), ApplicationConfigurator.class, null );
    this.configurator = configurator;
    this.configuration = configuration;
  }

  @Override
  public ApplicationConfigurator addingService( ServiceReference<ApplicationConfigurator> ref ) {
    ApplicationConfigurator result = super.addingService( ref );
    if( isTrackedConfigurator( result ) ) {
      storeConfiguratorServiceReference( ref );
      close();
    }
    return result;
  }
  
  public static boolean matches( ServiceReference<UIContributor> contributorReference ) {
    try {
      String expression = createFilterExpression();
      Filter filter = FrameworkUtil.createFilter( expression );
      return filter.match( contributorReference );
    } catch( InvalidSyntaxException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  private static String createFilterExpression() {
    @SuppressWarnings( "rawtypes" )
    ServiceReference serviceReference = getConfiguratorReference();
    String value = ( String )serviceReference.getProperty( getConfiguratorKey() );
    return "(" + getConfiguratorKey() + "=" + value + ")";
  }

  @SuppressWarnings( "rawtypes" )
  private static ServiceReference getConfiguratorReference() {
    return ( ServiceReference )RWT.getApplicationStore().getAttribute( CONFIGURATOR_REFERENCE );
  }

  private static String getConfiguratorKey() {
    return ApplicationConfigurator.class.getSimpleName();
  }

  private void storeConfiguratorServiceReference( ServiceReference<ApplicationConfigurator> ref ) {
    configuration.setAttribute( ConfiguratorTracker.CONFIGURATOR_REFERENCE, ref );
  }

  private boolean isTrackedConfigurator( ApplicationConfigurator result ) {
    return result == configurator;
  }
  
  private static BundleContext getBundleContext() {
    return FrameworkUtil.getBundle( ConfiguratorTracker.class ).getBundleContext();
  }
}