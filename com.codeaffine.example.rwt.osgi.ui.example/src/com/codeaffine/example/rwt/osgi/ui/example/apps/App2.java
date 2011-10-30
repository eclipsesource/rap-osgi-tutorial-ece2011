/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package com.codeaffine.example.rwt.osgi.ui.example.apps;

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;

import com.codeaffine.example.rwt.osgi.ui.platform.ConfiguratorTracker;
import com.codeaffine.example.rwt.osgi.ui.platform.ShellPositioner;


public class App2 implements ApplicationConfigurator {
  static final String EXAMPLE_UI = "example";
  
  @Override
  public void configure( ApplicationConfiguration configuration ) {
    configuration.addEntryPoint( "default", UIEntryPoint.class );
    configuration.addStyleSheet( EXAMPLE_UI, "themes/eclipsesource/theme.css" );
    configuration.addBranding( new UIBranding() );
    configuration.addPhaseListener( new ShellPositioner() );
    new ConfiguratorTracker( this, configuration ).open();
  }
}
