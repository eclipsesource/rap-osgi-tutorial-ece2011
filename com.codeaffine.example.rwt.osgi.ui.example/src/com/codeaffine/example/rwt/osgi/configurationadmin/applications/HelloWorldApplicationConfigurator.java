/*******************************************************************************
 * Copyright (c) 2011 Frank Appel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.codeaffine.example.rwt.osgi.configurationadmin.applications;

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.branding.AbstractBranding;


public class HelloWorldApplicationConfigurator implements ApplicationConfigurator {

  public void configure( ApplicationConfiguration configuration ) {
    configuration.addEntryPoint( "default", HelloWorld.class );
    configuration.addBranding( new AbstractBranding() {
      @Override
      public String getServletName() {
        return "helloworld";
      }
    } );
  }
}
