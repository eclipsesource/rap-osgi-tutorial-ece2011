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
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributorFactory;


public class UiComponents {

  public static List<UiComponent> getActiveComponents( String port ) {
    List<Long> ids = new ArrayList<Long>();
    BundleContext bundleContext = DeploymentHelper.getBundleContext();
    Collection<ServiceReference<?>> serviceReferences = new ArrayList<ServiceReference<?>>();
    try {
      serviceReferences.addAll( bundleContext.getServiceReferences( ApplicationConfigurator.class, null ) );
      serviceReferences.addAll( bundleContext.getServiceReferences( UIContributorFactory.class, null ) );
    } catch( InvalidSyntaxException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
    for( ServiceReference<?> serviceReference : serviceReferences ) {
      Object portProperty = serviceReference.getProperty( "httpService.target" );
      String expected = "(http.port=" + port + ")";
      if( expected.equals( portProperty ) ) {
        ids.add( ( Long )serviceReference.getProperty( "component.id" ) );
      }
      Object appConfProperty = serviceReference.getProperty( "ApplicationConfigurator" );
      if( appConfProperty != null && ((String)appConfProperty).contains( port + "_" ) ) {
        ids.add( ( Long )serviceReference.getProperty( "component.id" ) );
      }
    }
    ArrayList<UiComponent> result = new ArrayList<UiComponent>();
    Component[] allComponents = getAllComponents();
    for( Component component : allComponents ) {
      if( ids.contains( Long.valueOf( component.getId() ) ) ) {
        String property = ( String )component.getProperties().get( "ApplicationConfigurator" );
        String application = property.replaceFirst( ".*_(.*)_.*", "$1" );
        result.add( new UiComponent( component, application, port ) );
      }
    }
    return result;
  }

  public static List<UiComponent> getAvailableComponents() {
    Component[] components = getAllComponents();
    ArrayList<String> knownComponents = new ArrayList<String>();
    ArrayList<UiComponent> result = new ArrayList<UiComponent>();
    for( Component component : components ) {
      if( "require".equals( component.getConfigurationPolicy() ) ) {
        UiComponent uiComponent = new UiComponent( component, null, null );
        String key = uiComponent.getUniqueKey();
        if( !knownComponents.contains( key ) ) {
          result.add( uiComponent );
          knownComponents.add( key );
        }
      }
    }
    return result;
  }

  private static Component[] getAllComponents() {
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
    Arrays.sort( components, new Comparator<Component>() {
      public int compare( Component component1, Component component2 ) {
        String name1 = component1.getName().toLowerCase( Locale.ENGLISH );
        String name2 = component2.getName().toLowerCase( Locale.ENGLISH );
        return name1.compareTo( name2 );
      }
    } );
    return components;
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
    Collections.sort( result );
    return Collections.unmodifiableList( result );
  }

}
