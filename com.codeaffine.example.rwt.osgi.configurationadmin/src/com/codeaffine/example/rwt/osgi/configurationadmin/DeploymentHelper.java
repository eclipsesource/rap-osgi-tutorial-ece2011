/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
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
import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.HttpService;


public class DeploymentHelper {

  private final BundleContext bundleContext;

  public DeploymentHelper() {
    bundleContext = getBundleContext();
  }

  public static BundleContext getBundleContext() {
    return FrameworkUtil.getBundle( DeploymentHelper.class ).getBundleContext();
  }

  public void deployApplication( String configurator, String port, String contextName ) {
    String key = createApplicationKey( configurator, port, contextName );
    try {
      Configuration configuration = createConfiguration( configurator );
      configuration.update( createApplicationSettings( key, port, contextName ) );
    } catch( IOException ioe ) {
      throw new RuntimeException( "Unable to update configuration for " + key, ioe );
    }
  }

  public void undeployApplication( String configurator, String port, String contextName ) {
    deleteConfiguration( createApplicationFilter( configurator, port, contextName ) );
  }

  public void deployUIContribution( String contributor,
                             String configurator,
                             String port,
                             String contextName )
  {
    try {
      Configuration configuration = createConfiguration( contributor );
      configuration.update( createSettings( contributor, configurator, port, contextName ) );
    } catch( IOException ioe ) {
      throw new RuntimeException( "Unable to update configuration for " + contributor, ioe );
    }
  }

  public void undeployUIContribution( String contributor,
                                      String configurator,
                                      String port,
                                      String contextName )
  {
    String filter = createUIContributorFilter( contributor, configurator, port, contextName );
    deleteConfiguration( filter );
  }

  private Configuration createConfiguration( String configurationName )
    throws IOException
  {
    Configuration result;
    ServiceReference<ConfigurationAdmin> reference = getConfigurationAdmin();
    ConfigurationAdmin configurationAdmin = bundleContext.getService( reference );
    try {
      result = configurationAdmin.createFactoryConfiguration( configurationName );
    } finally {
      bundleContext.ungetService( reference );
    }
    return result;
  }

  private void deleteConfiguration( String filter ) {
    try {
      ServiceReference<ConfigurationAdmin> reference = getConfigurationAdmin();
      ConfigurationAdmin configurationAdmin = bundleContext.getService( reference );
      Configuration[] configurations;
      try {
        configurations = configurationAdmin.listConfigurations( filter );
      } finally {
        bundleContext.ungetService( reference );
      }
      if( configurations != null && configurations.length >= 1 ) {
        configurations[ 0 ].delete();
      }
    } catch( Exception exception ) {
      throw new RuntimeException( "Unable to delete configuration for " + filter, exception );
    }
  }

  private ServiceReference<ConfigurationAdmin> getConfigurationAdmin() {
    return bundleContext.getServiceReference( ConfigurationAdmin.class );
  }

  private static Dictionary<String, Object> createApplicationSettings( String key,
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

  private static Dictionary<String, String> createSettings( String contributor,
                                                            String configurator,
                                                            String port,
                                                            String contextName )
  {
    String key = createUIContributorKey( contributor, configurator, port, contextName );
    String value = createApplicationKey( configurator, port, contextName );
    Hashtable<String, String> result = new Hashtable<String, String>();
    result.put( key, value );
    result.put( getConfiguratorKey(), value );
    return result;
  }

  private static String createUIContributorKey( String contributor,
                                                String configurator,
                                                String port,
                                                String contextName )
  {
    return contributor + "_" + createApplicationKey( configurator, port, contextName );
  }

  private static String createTargetKey( Class<?> targetType ) {
    StringBuilder result = new StringBuilder();
    result.append( targetType.getSimpleName().substring( 0, 1 ).toLowerCase() );
    result.append( targetType.getSimpleName().substring( 1 ) );
    result.append( ".target" );
    return result.toString();
  }

  private static String getConfiguratorKey() {
    return ApplicationConfiguration.class.getSimpleName();
  }

  private static String createPortFilter( String port ) {
    return "(" + JettyConstants.HTTP_PORT + "=" + port + ")";
  }

  private static String createUIContributorFilter( String contributor,
                                                   String configurator,
                                                   String port,
                                                   String contextName )
  {
    String key = createUIContributorKey( contributor, configurator, port, contextName );
    String value = createApplicationKey( configurator, port, contextName );
    return "(" + key + "=" + value + ")";
  }

  private static String createApplicationFilter( String configurator, String port, String contextName ) {
    String key = createApplicationKey( configurator, port, contextName );
    return "(" + key + "=" + port + ")";
  }

  private static String createApplicationKey( String configurator, String port, String contextName ) {
    return port + "_" + configurator + "_" + contextName;
  }

}
