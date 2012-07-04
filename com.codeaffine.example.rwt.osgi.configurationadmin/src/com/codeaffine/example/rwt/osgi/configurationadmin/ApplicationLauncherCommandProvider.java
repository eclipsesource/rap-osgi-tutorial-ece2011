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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;


public class ApplicationLauncherCommandProvider implements CommandProvider {

  private static final String CUSTOMIZER_CLASS
    = JettyConstants.class.getPackage().getName() + "." + JettyConstants.CUSTOMIZER_CLASS;
  private static final String HTTP_SERVER_MANAGER_ID = "org.eclipse.equinox.http.jetty.config";

  private BundleContext bundleContext;

  public void _sh( CommandInterpreter commandInterpreter ) {
    _startHttpService( commandInterpreter );
  }

  public void _startHttpService( CommandInterpreter commandInterpreter ) {
    String port = getPort( commandInterpreter );
    boolean jettyCustomizerFlag = getJettyCustomizerFlag( commandInterpreter );
    if( null != port ) {
      startHttpService( commandInterpreter, port, jettyCustomizerFlag );
    }
  }

  public void _hh( CommandInterpreter commandInterpreter ) {
    _stopHttpService( commandInterpreter );
  }

  public void _stopHttpService( CommandInterpreter commandInterpreter ) {
    String port = getPort( commandInterpreter );
    if( null != port ) {
      stopHttpService( commandInterpreter, port );
    }
  }

  public void _da( CommandInterpreter commandInterpreter ) {
    _deployApplication( commandInterpreter );
  }

  public void _deployApplication( CommandInterpreter commandInterpreter ) {
    String configurator = getApplicationConfiguration( commandInterpreter );
    String port = getPort( commandInterpreter );
    String contextName = commandInterpreter.nextArgument();
    if( port != null && configurator != null ) {
      new DeploymentHelper().deployApplication( configurator, port, contextName );
    }
  }

  public void _ua( CommandInterpreter commandInterpreter ) {
    _undeployApplication( commandInterpreter );
  }

  public void _undeployApplication( CommandInterpreter commandInterpreter ) {
    String configurator = getApplicationConfiguration( commandInterpreter );
    String port = getPort( commandInterpreter );
    String contextName = commandInterpreter.nextArgument();
    if( port != null && configurator != null ) {
      undeployApplication( configurator, port, contextName );
    }
  }

  public void _du( CommandInterpreter commandInterpreter ) {
    _deployUIContribution( commandInterpreter );
  }

  public void _deployUIContribution( CommandInterpreter commandInterpreter ) {
    String contributor = getUIContributor( commandInterpreter );
    String configurator = getApplicationConfiguration( commandInterpreter );
    String port = getPort( commandInterpreter );
    String contextName = commandInterpreter.nextArgument();
    if( contributor != null && port != null && configurator != null ) {
      deployUIContribution( commandInterpreter, contributor, configurator, port, contextName );
    }
  }

  public void _uu( CommandInterpreter commandInterpreter ) {
    _undeployUIContribution( commandInterpreter );
  }

  public void _undeployUIContribution( CommandInterpreter commandInterpreter ) {
    String contributor = getUIContributor( commandInterpreter );
    String configurator = getApplicationConfiguration( commandInterpreter );
    String port = getPort( commandInterpreter );
    String contextName = commandInterpreter.nextArgument();
    if( contributor != null && port != null && configurator != null ) {
      undeployUIContribution( commandInterpreter, contributor, configurator, port, contextName );
    }
  }

  public String getHelp() {
    return   "---Configuration of ApplicationLauncher---\n"
           + "\tstartHttpService (sh) <port>\n"
           + "\tstopHttpService (hh) <port>\n"
           + "\tdeployApplication (da) <configurator name>|<port>|<context name(optional)>\n"
           + "\tundeployApplication (ua) <configurator name>|<port>|<context name(optional)>\n"
           + "\tdeployUIContribution (du) <contributor name>|<configurator name>|<port>|<context name(optional)>\n"
           + "\tundeployUIContribution (uu) <contributor name>|<configurator name>|<port>|<context name(optional)>\n";
  }

  public void activate( BundleContext bundleContext ) {
    this.bundleContext = bundleContext;
  }

  public void deactivate( BundleContext bundleContext ) {
    this.bundleContext = null;
  }

  private boolean getJettyCustomizerFlag( CommandInterpreter commandInterpreter ) {
    String jettyCustomizerFlag = getArgument( commandInterpreter, null );
    return "jcf".equals( jettyCustomizerFlag );
  }


  private String getPort( CommandInterpreter commandInterpreter ) {
    return getArgument( commandInterpreter, "Parameter port must not be null" );
  }

  private String getApplicationConfiguration( CommandInterpreter commandInterpreter ) {
    return getArgument( commandInterpreter, "Parameter configurator must not be null" );
  }

  private String getUIContributor( CommandInterpreter commandInterpreter ) {
    return getArgument( commandInterpreter, "Parameter contributor must not be null" );
  }

  private String getArgument( CommandInterpreter commandInterpreter, String message ) {
    String result = commandInterpreter.nextArgument();
    if( result == null && message != null ) {
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
    DeploymentHelper deploymentHelper = new DeploymentHelper();
    deploymentHelper.deployUIContribution( contributor, configurator, port, contextName );
  }

  private void undeployUIContribution( CommandInterpreter commandInterpreter,
                                       String contributor,
                                       String configurator,
                                       String port,
                                       String contextName )
  {
    DeploymentHelper deploymentHelper = new DeploymentHelper();
    deploymentHelper.undeployUIContribution( contributor, configurator, port, contextName );
  }


  private void undeployApplication( String configurator, String port, String contextName ) {
    new DeploymentHelper().undeployApplication( configurator, port, contextName );
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

  private Dictionary<String, Object> createHttpServiceSettings( String port,
                                                                boolean jettyCustomizerFlag )
  {
    Dictionary<String,Object> result = new Hashtable<String, Object>();
    result.put( JettyConstants.HTTP_PORT, Integer.valueOf( port ) );
    if( useJettyCustomizer() && !jettyCustomizerFlag ) {
      result.put( JettyConstants.CUSTOMIZER_CLASS, System.getProperty( CUSTOMIZER_CLASS ) );
    }
    return result;
  }

  private boolean useJettyCustomizer() {
    return System.getProperty( CUSTOMIZER_CLASS ) != null;
  }

  private void startHttpService( CommandInterpreter commandInterpreter,
                                 String port,
                                 boolean jettyCustomizerFlag )
  {
    try {
      Configuration configuration = createHttpServiceConfiguration();
      configuration.update( createHttpServiceSettings( port, jettyCustomizerFlag ) );
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
