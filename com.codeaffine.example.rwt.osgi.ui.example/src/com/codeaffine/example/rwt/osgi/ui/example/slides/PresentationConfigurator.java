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
package com.codeaffine.example.rwt.osgi.ui.example.slides;

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;

import com.codeaffine.example.rwt.osgi.ui.platform.ConfiguratorTracker;


public class PresentationConfigurator implements ApplicationConfigurator {
  static final String SLIDES = "slides";

  @Override
  public void configure( ApplicationConfiguration configuration ) {
    configuration.addEntryPoint( "default", EntryPoint.class );
    configuration.addStyleSheet( SLIDES, "themes/slides/theme.css" );
    configuration.addBranding( new Branding() );
    new ConfiguratorTracker( this, configuration ).open();
  }
}
