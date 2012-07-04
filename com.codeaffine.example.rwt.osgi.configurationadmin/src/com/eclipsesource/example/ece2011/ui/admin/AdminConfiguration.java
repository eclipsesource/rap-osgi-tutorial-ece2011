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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.client.WebClient;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;


public class AdminConfiguration implements ApplicationConfiguration {

  public void configure( Application application ) {
    IEntryPointFactory entryPointFactory = new AdminEntryPointFactory();
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.PAGE_TITLE, "RAP Admin UI" );
    application.addEntryPoint( "/admin", entryPointFactory, properties );
    application.addStyleSheet( "org.eclipse.rap.rwt.theme.Default", "resources/addon.css" );
  }

  private final class AdminEntryPointFactory implements IEntryPointFactory {

    public IEntryPoint create() {
      return new AdminUI();
    }
  }

}
