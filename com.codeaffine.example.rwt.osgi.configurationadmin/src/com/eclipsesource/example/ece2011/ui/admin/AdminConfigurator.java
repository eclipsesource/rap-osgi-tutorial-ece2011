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

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;


public class AdminConfigurator implements ApplicationConfigurator {

  public void configure( ApplicationConfiguration configuration ) {
    configuration.addEntryPoint( "default", new IEntryPointFactory() {
      public IEntryPoint create() {
        return new AdminUI();
      }
    } );
    configuration.addBranding( new AbstractBranding() {
      @Override
      public String getServletName() {
        return "admin";
      }
      @Override
      public String getTitle() {
        return "RAP Admin UI";
      }
    } );
    configuration.addStyleSheet( "org.eclipse.rap.rwt.theme.Default", "resources/addon.css" );
  }

}
