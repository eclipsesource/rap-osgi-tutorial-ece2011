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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.equinox.http.jetty.JettyConfigurator;
import org.eclipse.equinox.http.jetty.JettyConstants;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.HttpService;


public class ApplicationLauncherComandProvider implements CommandProvider {
  
  private static final String PID_PREFIX = JettyConfigurator.class.getName() + ".id_";
  private static final String HTTP_SERVER_MANAGER_ID = "org.eclipse.equinox.http.jetty.config";

  private final Map<String, Configuration> httpServices;
  private final Map<String, Configuration> applications;
  private BundleContext bundleContext;

  public ApplicationLauncherComandProvider() {
    this.httpServices = new HashMap<String, Configuration>(); 
    this.applications = new HashMap<String, Configuration>(); 
  }
  
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
      deployRWTContext( commandInterpreter, configurator, port, contextName );
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

  public String getHelp() {
    return   "---Configuration of ApplicationLauncher---\n"
           + "\tstartHttpService <port>\n"
           + "\tstopHttpService <port>\n"
           + "\tdeployApplication <configurator name>|<port>|<contextName(optional)>\n"
           + "\tundeployApplication <configurator name>|<port>|<contextName(optional)>\n";
  }
  
  public void activate( BundleContext bundleContext ) {
    this.bundleContext = bundleContext;
  }
  
  public void deactivate( BundleContext bundleContext ) {
    this.bundleContext = null;
  }
  
  private String getPort( CommandInterpreter commandInterpreter ) {
    String result = commandInterpreter.nextArgument();
    if( result == null ) {
      commandInterpreter.println( "Parameter port must not be null" );
    }
    return result;
  }

  private String getApplicationConfigurator( CommandInterpreter commandInterpreter ) {
    String result = commandInterpreter.nextArgument();
    if( result == null ) {
      commandInterpreter.println( "Parameter configurator must not be null" );
    }
    return result;
  }
  
  private void deployRWTContext( CommandInterpreter ci,
                                 String configurator,
                                 String port,
                                 String contextName )
  {
    String key = createApplicationKey( configurator, port, contextName );
    try {
      Configuration configuration = createApplicationConfiguration( configurator );
      configuration.update( createApplicationSettings( port, contextName ) );
      applications.put( key, configuration );
    } catch( IOException ioe ) {
      ci.println( "Unable to update configuration for " + key );
      ci.println( ioe.getMessage() );
    }
  }
  
  private void undeployApplication( CommandInterpreter ci,
                                    String configurator,
                                    String port,
                                    String contextName ) {
    String key = createApplicationKey( configurator, port, contextName );
    try {
      Configuration configuration = applications.remove( key );
      configuration.delete();
    } catch( IOException ioe ) {
      ci.println( "Unable to delete configuration for " + key );
      ci.println( ioe.getMessage() );
    }
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

  private Dictionary<String, Object> createApplicationSettings( String port, String contextName ) {
    Dictionary<String,Object> result = new Hashtable<String, Object>();
    result.put( createTargetKey( HttpService.class ),
                "(" + JettyConstants.HTTP_PORT + "=" + port + ")" );
    if( contextName != null ) {
      result.put( ApplicationLauncher.PROPERTY_CONTEXT_NAME, contextName );
    }
    return result;
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
    result.put( Constants.SERVICE_PID, PID_PREFIX + Integer.valueOf( port ) );
    result.put( JettyConstants.HTTP_PORT, Integer.valueOf( port ) );
    result.put( JettyConstants.CUSTOMIZER_CLASS,
                "org.eclipse.rap.jettycustomizer.internal.SessionCookieCustomizer" );
    return result;
  }

  private void startHttpService( CommandInterpreter commandInterpreter, String port ) {
    try {
      Configuration configuration = createHttpServiceConfiguration();
      configuration.update( createHttpServiceSettings( port ) );
      httpServices.put( port, configuration );
    } catch( IOException ioe ) {
      commandInterpreter.println( "Unable to start HttpService at port: " + port );
      commandInterpreter.println( ioe.getMessage() );
    }
  }

  private void stopHttpService( CommandInterpreter commandInterpreter, String port ) {
    try {
      Configuration configuration = httpServices.remove( port );
      if( configuration != null ) {
        configuration.delete();
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