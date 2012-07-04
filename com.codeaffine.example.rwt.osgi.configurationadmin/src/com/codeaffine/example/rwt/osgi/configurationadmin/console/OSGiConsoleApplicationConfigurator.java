package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.branding.AbstractBranding;


public class OSGiConsoleApplicationConfigurator implements ApplicationConfigurator {

  public void configure( ApplicationConfiguration configuration ) {
    configuration.addEntryPoint( "default", OSGiConsoleEntryPoint.class );
    configuration.addBranding( new AbstractBranding() {
      @Override
      public String getServletName() {
        return "console";
      }
    } );
  }
}
