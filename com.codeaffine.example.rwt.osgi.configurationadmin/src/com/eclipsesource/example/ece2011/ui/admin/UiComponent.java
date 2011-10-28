package com.eclipsesource.example.ece2011.ui.admin;

import org.apache.felix.scr.Component;


public class UiComponent {

  private final Component component;

  public UiComponent( Component component ) {
    this.component = component;
  }

  public boolean isApplication() {
    return implementsService( "org.eclipse.rwt.application.ApplicationConfigurator" );
  }

  public boolean isUiContribution() {
    return implementsService( "com.codeaffine.example.rwt.osgi.ui.platform.UIContributorFactory" );
  }

  public boolean implementsService( String string ) {
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

  public String getName() {
    return component.getName();
  }

  public String getBundleName() {
    return component.getBundle().getSymbolicName();
  }

}
