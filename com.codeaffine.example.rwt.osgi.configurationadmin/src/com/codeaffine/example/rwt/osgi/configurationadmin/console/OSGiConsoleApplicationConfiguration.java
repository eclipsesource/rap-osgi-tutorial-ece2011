package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.ApplicationConfiguration;


public class OSGiConsoleApplicationConfiguration implements ApplicationConfiguration {

  public void configure( Application configuration ) {
    configuration.addEntryPoint( "/console", OSGiConsoleEntryPoint.class, null );
  }
}
