/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.example.ece2011.ui.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.eclipse.equinox.http.jetty.JettyConstants;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import com.codeaffine.example.rwt.osgi.configurationadmin.DeploymentHelper;


public class UiComponents {

  public static boolean isApplication( Component component ) {
    return implementsService( component, "org.eclipse.rwt.application.ApplicationConfigurator" );
  }

  public static boolean isUiContribution( Component component ) {
    return implementsService( component, "com.codeaffine.example.rwt.osgi.ui.platform.UIContributorFactory" );
  }

  public static void deploy( Component component, int port ) {
    DeploymentHelper deploymentHelper = new DeploymentHelper();
    deploymentHelper.deployApplication( component.getName(), Integer.toString( port ), null );
  }

  public static void undeploy( Component component, int port ) {
    DeploymentHelper deploymentHelper = new DeploymentHelper();
    deploymentHelper.undeployApplication( component.getName(), Integer.toString( port ), null );
  }

  public static List<Component> getActiveComponents( String port ) {
    List<Long> ids = new ArrayList<Long>();
    BundleContext bundleContext = DeploymentHelper.getBundleContext();
    Collection<ServiceReference<ApplicationConfigurator>> serviceReferences;
    try {
      serviceReferences = bundleContext.getServiceReferences( ApplicationConfigurator.class, null );
    } catch( InvalidSyntaxException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
    for( ServiceReference<ApplicationConfigurator> serviceReference : serviceReferences ) {
      Object portProperty = serviceReference.getProperty( "httpService.target" );
      String expected = "(http.port=" + port + ")";
      if( expected.equals( portProperty ) ) {
        ids.add( ( Long )serviceReference.getProperty( "component.id" ) );
      }
    }
    ArrayList<Component> result = new ArrayList<Component>();
    List<Component> allComponents = getAllComponents();
    for( Component component : allComponents ) {
      if( ids.contains( Long.valueOf( component.getId() ) ) ) {
        result.add( component );
      }
    }
    return new ArrayList<Component>( result );
  }

  public static List<Component> getAllComponents() {
    Component[] components = null;
    BundleContext context = DeploymentHelper.getBundleContext();
    ServiceReference<?> reference = context.getServiceReference( "org.apache.felix.scr.ScrService" );
    if( reference != null ) {
      ScrService scrService = ( ScrService )context.getService( reference );
      try {
        components = scrService.getComponents();
      } finally {
        context.ungetService( reference );
      }
    }
    return new ArrayList<Component>( Arrays.asList( components ) );
  }

  public static boolean implementsService( Component component, String string ) {
    boolean result = false;
    String[] services = component.getServices();
    if( services != null ) {
      for( String service : services ) {
        if( string.equals( service ) ) {
          result = true;
        }
      }
    }
    return result;
  }

  public static List<String> getAvailablePorts() {
    List<String> result = new ArrayList<String>();
    BundleContext bundleContext = DeploymentHelper.getBundleContext();
    try {
      Collection<ServiceReference<HttpService>> httpServices
        = bundleContext.getServiceReferences( HttpService.class, null );
      for( ServiceReference< HttpService > service : httpServices ) {
        result.add( ( String )service.getProperty( JettyConstants.HTTP_PORT ) );
      }
    } catch( InvalidSyntaxException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
    return Collections.unmodifiableList( result );
  }

  public static final class UIComponentComparator implements Comparator<Component> {
    
    public int compare( Component component1, Component component2 ) {
      int result;
      boolean isApplication1 = UiComponents.isApplication( component1 );
      boolean isApplication2 = UiComponents.isApplication( component2 );
      if( isApplication1 && !isApplication2 ) {
        result = -1;
      } else if( !isApplication1 && isApplication2 ) {
        result = 1;
      } else {
        String name1 = component1.getName().toLowerCase( Locale.ENGLISH );
        String name2 = component2.getName().toLowerCase( Locale.ENGLISH );
        result = name1.compareTo( name2 );
      }
      return result;
    }
  }

}
