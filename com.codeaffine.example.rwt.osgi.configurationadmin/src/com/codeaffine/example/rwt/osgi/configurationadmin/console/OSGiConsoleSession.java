package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.osgi.framework.console.ConsoleSession;

class OSGiConsoleSession extends ConsoleSession {

  private final File consoleIn;
  private final File consoleOut;

  OSGiConsoleSession( File consoleIn, File consoleOut ) {
    this.consoleIn = consoleIn;
    this.consoleOut = consoleOut;
  }

  @Override
  protected void doClose() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public InputStream getInput() {
    try {
      return new FileInputStream( consoleIn );
    } catch( FileNotFoundException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  @Override
  public OutputStream getOutput() {
    try {
      return new FileOutputStream( consoleOut );
    } catch( FileNotFoundException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }
}