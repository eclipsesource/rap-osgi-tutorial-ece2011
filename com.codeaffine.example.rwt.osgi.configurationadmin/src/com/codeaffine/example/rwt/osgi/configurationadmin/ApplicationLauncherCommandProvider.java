/*******************************************************************************
 * Copyright (c) 2011 Frank Appel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.codeaffine.example.rwt.osgi.configurationadmin;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.equinox.http.jetty.JettyConstants;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.HttpService;


public class ApplicationLauncherCommandProvider implements CommandProvider {
  
  private static final String HTTP_SERVER_MANAGER_ID = "org.eclipse.equinox.http.jetty.config";

  private BundleContext bundleContext;

  public void _startHttpService( CommandInterpreter commandInterpreter ) {
    String port = getPort( commandInterpreter );
    if( null != port ) {
      startHttpService( commandInterpreter, port );
    }
  }

  public void _stopHttpService( CommandInterpreter commandInterpreter ) {
    String port = getPort( commandInterpreter );
    if( null != port ) {
      stopHttpService( commandInterpreter, port );
    }
  }

  public void _deployApplication( CommandInterpreter commandInterpreter ) {
    String configurator = getApplicationConfigurator( commandInterpreter );
    String port = getPort( commandInterpreter );
    String contextName = commandInterpreter.nextArgument();
    if( port != null && configurator != null ) {
      deployApplication( commandInterpreter, configurator, port, contextName );
    }
  }

  public void _undeployApplication( CommandInterpreter commandInterpreter ) {
    String configurator = getApplicationConfigurator( commandInterpreter );
    String port = getPort( commandInterpreter );
    String contextName = commandInterpreter.nextArgument();
    if( port != null && configurator != null ) {
      undeployApplication( commandInterpreter, configurator, port, contextName );
    }
  }
  
  public void _deployUIContribution( CommandInterpreter commandInterpreter ) {
    String contributor = getUIContributor( commandInterpreter );
    String configurator = getApplicationConfigurator( commandInterpreter );
    String port = getPort( commandInterpreter );
    String contextName = commandInterpreter.nextArgument();
    if( contributor != null && port != null && configurator != null ) {
      deployUIContribution( commandInterpreter, contributor, configurator, port, contextName );
    }
  }

  public void _undeployUIContribution( CommandInterpreter commandInterpreter ) {
    String contributor = getUIContributor( commandInterpreter );
    String configurator = getApplicationConfigurator( commandInterpreter );
    String port = getPort( commandInterpreter );
    String contextName = commandInterpreter.nextArgument();
    if( contributor != null && port != null && configurator != null ) {
      undeployUIContribution( commandInterpreter, contributor, configurator, port, contextName );
    }
  }

  public String getHelp() {
    return   "---Configuration of ApplicationLauncher---\n"
           + "\tstartHttpService <port>\n"
           + "\tstopHttpService <port>\n"
           + "\tdeployApplication <configurator name>|<port>|<context name(optional)>\n"
           + "\tundeployApplication <configurator name>|<port>|<context name(optional)>\n"
           + "\tdeployUIContribution <contributor name>|<configurator name>|<port>|<context name(optional)>\n"
           + "\tundeployUIContribution <contributor name>|<configurator name>|<port>|<context name(optional)>\n";
  }
  
  public void activate( BundleContext bundleContext ) {
    this.bundleContext = bundleContext;
  }
  
  public void deactivate( BundleContext bundleContext ) {
    this.bundleContext = null;
  }
  
  private String getPort( CommandInterpreter commandInterpreter ) {
    return getArgument( commandInterpreter, "Parameter port must not be null" );
  }

  private String getApplicationConfigurator( CommandInterpreter commandInterpreter ) {
    return getArgument( commandInterpreter, "Parameter configurator must not be null" );
  }
  
  private String getUIContributor( CommandInterpreter commandInterpreter ) {
    return getArgument( commandInterpreter, "Parameter contributor must not be null" );
  }
  
  private String getArgument( CommandInterpreter commandInterpreter, String message ) {
    String result = commandInterpreter.nextArgument();
    if( result == null ) {
      commandInterpreter.println( message );
    }
    return result;
  }
  
  private void deployUIContribution( CommandInterpreter commandInterpreter,
                                     String contributor,
                                     String configurator,
                                     String port,
                                     String contextName )
  {
    try {
      Configuration configuration = createUIContributionConfiguration( contributor );
      configuration.update( createSettings( contributor, configurator, port, contextName ) );
    } catch( IOException ioe ) {
      commandInterpreter.println( "Unable to update configuration for " + contributor );
      commandInterpreter.println( ioe.getMessage() );
    }
  }

  private void undeployUIContribution( CommandInterpreter commandInterpreter,
                                       String contributor,
                                       String configurator,
                                       String port,
                                       String contextName )
  {
    String filter = createUIContributorFilter( contributor, configurator, port, contextName );
    deleteConfiguration( commandInterpreter, filter );
  }

  private String createUIContributorFilter( String contributor,
                                            String configurator,
                                            String port,
                                            String contextName )
  {
    String key = createUIContributorKey( contributor, configurator, port, contextName );
    String value = createApplicationKey( configurator, port, contextName );
    return "(" + key + "=" + value + ")";
  }

  @SuppressWarnings( {
    "rawtypes", "unchecked"
  } )
  private Dictionary createSettings( String contributor,
                                     String configurator,
                                     String port,
                                     String contextName )
  {
    String key = createUIContributorKey( contributor, configurator, port, contextName );
    String value = createApplicationKey( configurator, port, contextName );
    Hashtable result = new Hashtable();
    result.put( key, value );
    result.put( getConfiguratorKey(), value );
    return result;
  }

  private String createUIContributorKey( String contributor,
                                         String configurator,
                                         String port,
                                         String contextName )
  {
    return contributor + "_" + createApplicationKey( configurator, port, contextName );
  }
  
  private Configuration createUIContributionConfiguration( String contributor ) throws IOException {
    ConfigurationAdmin configurationAdmin = getConfigurationAdmin();
    return configurationAdmin.createFactoryConfiguration( contributor );
  }

  private void deployApplication( CommandInterpreter ci,
                                  String configurator,
                                  String port,
                                  String contextName )
  {
    String key = createApplicationKey( configurator, port, contextName );
    try {
      Configuration configuration = createApplicationConfiguration( configurator );
      configuration.update( createApplicationSettings( key, port, contextName ) );
    } catch( IOException ioe ) {
      ci.println( "Unable to update configuration for " + key );
      ci.println( ioe.getMessage() );
    }
  }
  
  private void undeployApplication( CommandInterpreter ci,
                                    String configurator,
                                    String port,
                                    String contextName ) {
    deleteConfiguration( ci, createApplicationFilter( configurator, port, contextName ) );
  }

  private void deleteConfiguration( CommandInterpreter ci, String filter ) {
    try {
      ConfigurationAdmin configurationAdmin = getConfigurationAdmin();
      Configuration[] configurations = configurationAdmin.listConfigurations( filter );
      if( configurations.length >= 1 ) {
        configurations[ 0 ].delete();
      }
    } catch( Exception exception ) {
      ci.println( "Unable to delete configuration for " + filter );
      ci.println( exception.getMessage() );
    }
  }

  private String createApplicationFilter( String configurator, String port, String contextName ) {
    String key = createApplicationKey( configurator, port, contextName );
    return "(" + key + "=" + port + ")";
  }
  
  private String createApplicationKey( String configurator, String port, String contextName ) {
    return port + "_" + configurator + "_" + contextName;
  }

  private Configuration createApplicationConfiguration( String configurationName )
    throws IOException
  {
    ConfigurationAdmin configurationAdmin = getConfigurationAdmin();
    return configurationAdmin.createFactoryConfiguration( configurationName );
  }

  private Dictionary<String, Object> createApplicationSettings( String key,
                                                                String port,
                                                                String contextName )
  {
    Dictionary<String,Object> result = new Hashtable<String, Object>();
    result.put( createTargetKey( HttpService.class ), createPortFilter( port ) );
    result.put( getConfiguratorKey(), key );
    result.put( key, port );
    if( contextName != null ) {
      result.put( ApplicationLauncher.PROPERTY_CONTEXT_NAME, contextName );
    }
    return result;
  }

  private String getConfiguratorKey() {
    return ApplicationConfigurator.class.getSimpleName();
  }

  private String createPortFilter( String port ) {
    return "(" + JettyConstants.HTTP_PORT + "=" + port + ")";
  }
  
  static String createTargetKey( Class<?> targetType ) {
    StringBuilder result = new StringBuilder(); 
    result.append( targetType.getSimpleName().substring( 0, 1 ).toLowerCase() );
    result.append( targetType.getSimpleName().substring( 1 ) );
    result.append( ".target" );
    return result.toString();
  }

  private ConfigurationAdmin getConfigurationAdmin() {
    Class<ConfigurationAdmin> type = ConfigurationAdmin.class;
    ServiceReference<ConfigurationAdmin> reference = bundleContext.getServiceReference( type );
    return bundleContext.getService( reference );
  }

  private Dictionary<String, Object> createHttpServiceSettings( String port ) {
    Dictionary<String,Object> result = new Hashtable<String, Object>();
    result.put( JettyConstants.HTTP_PORT, Integer.valueOf( port ) );
    if( System.getProperty( JettyConstants.CUSTOMIZER_CLASS ) != null ) {
      result.put( JettyConstants.CUSTOMIZER_CLASS,
                  System.getProperty( JettyConstants.CUSTOMIZER_CLASS ) );
    }
    return result;
  }

  private void startHttpService( CommandInterpreter commandInterpreter, String port ) {
    try {
      Configuration configuration = createHttpServiceConfiguration();
      configuration.update( createHttpServiceSettings( port ) );
    } catch( IOException ioe ) {
      commandInterpreter.println( "Unable to start HttpService at port: " + port );
      commandInterpreter.println( ioe.getMessage() );
    }
  }

  private void stopHttpService( CommandInterpreter commandInterpreter, String port ) {
    try {
      ConfigurationAdmin configurationAdmin = getConfigurationAdmin();
      String filter = createPortFilter( port );
      Configuration[] configurations = configurationAdmin.listConfigurations( filter );
      if( configurations.length >= 1  ) {
        configurations[ 0 ].delete();
      }
    } catch( Exception exception ) {
      commandInterpreter.println( "Unable to stop HttpService at port: " + port );
      commandInterpreter.println( exception.getMessage() );
    }
  }

  private Configuration createHttpServiceConfiguration() throws IOException {
    ConfigurationAdmin configurationAdmin = getConfigurationAdmin();
    String location = findHttpServiceManagerLocation();
    return configurationAdmin.createFactoryConfiguration( HTTP_SERVER_MANAGER_ID, location );
  }

  private String findHttpServiceManagerLocation() {
    Bundle[] bundles = bundleContext.getBundles();
    String result = null;
    for( Bundle bundle : bundles ) {
      if( bundle.getSymbolicName().equals( "org.eclipse.equinox.http.jetty" ) ) {
        result = bundle.getLocation();
      }
    }
    return result;
  }
}