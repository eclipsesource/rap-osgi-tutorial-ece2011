package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.osgi.framework.console.ConsoleSession;

class OSGiConsoleSession extends ConsoleSession {

  private final File consoleIn;
  private final File consoleOut;
  private InputStream inputStream;
  private OutputStream outputStream;

  OSGiConsoleSession( File consoleIn, File consoleOut ) {
    this.consoleIn = consoleIn;
    this.consoleOut = consoleOut;
  }

  @Override
  protected void doClose() {
    if( outputStream != null ) {
      try {
        outputStream.close();
      } catch( IOException e ) {
        // do nothing
      }
    }
    if( inputStream != null ) {
      try {
        inputStream.close();
      } catch( IOException e ) {
        // do nothing
      }
    }
  }

  @Override
  public InputStream getInput() {
    try {
      inputStream = new FileInputStream( consoleIn );
      return inputStream;
    } catch( FileNotFoundException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  @Override
  public OutputStream getOutput() {
    try {
      outputStream = new FileOutputStream( consoleOut );
      return outputStream;
    } catch( FileNotFoundException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }
}
