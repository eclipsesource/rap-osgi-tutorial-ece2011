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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.client.WebClient;

import com.codeaffine.example.rwt.osgi.ui.platform.ConfiguratorTracker;


public class PresentationConfigurator implements ApplicationConfiguration {

  @Override
  public void configure( Application configuration ) {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.THEME_ID, "slides.theme" );
    properties.put( WebClient.PAGE_TITLE, "Slides" );
    configuration.addEntryPoint( "/slides", EntryPoint.class, properties  );
    configuration.addStyleSheet( "slides.theme", "themes/slides/theme.css" );
    new ConfiguratorTracker( this, configuration ).open();
  }
}
