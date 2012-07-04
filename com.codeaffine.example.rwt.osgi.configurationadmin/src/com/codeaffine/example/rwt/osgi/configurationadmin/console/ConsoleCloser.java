package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import org.eclipse.rwt.service.SessionStoreEvent;
import org.eclipse.rwt.service.SessionStoreListener;

class ConsoleCloser implements SessionStoreListener {

  private static final long serialVersionUID = 1L;

  private final OSGiConsole console;

  ConsoleCloser( OSGiConsole console ) {
    this.console = console;
  }

  public void beforeDestroy( SessionStoreEvent event ) {
    console.dispose();
  }
}
