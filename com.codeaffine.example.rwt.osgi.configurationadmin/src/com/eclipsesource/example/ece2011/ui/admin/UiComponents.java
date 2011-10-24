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

import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

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

  public static Component[] getAllComponents() {
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
    return components;
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

}
