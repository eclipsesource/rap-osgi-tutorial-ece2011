package com.eclipsesource.example.ece2011.ui.admin;

import org.apache.felix.scr.Component;


public class UiComponent {

  private final Component component;
  private final String application;
  private final String deployedOnPort;

  public UiComponent( Component component, String application, String deployedOnPort ) {
    this.component = component;
    this.application = application;
    this.deployedOnPort = deployedOnPort;
  }

  public String getName() {
    return component.getName();
  }

  public String getApplication() {
    return application;
  }

  public String getDeployedOnPort() {
    return deployedOnPort;
  }

  public String getBundleName() {
    return component.getBundle().getSymbolicName();
  }

  public boolean isApplication() {
    return implementsService( "org.eclipse.rwt.application.ApplicationConfiguration" );
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

  String getUniqueKey() {
    String type = isApplication() ? "application" : "uiContribution";
    return type + "_" + getName() + "_" + application + "_" + deployedOnPort;
  }

}
