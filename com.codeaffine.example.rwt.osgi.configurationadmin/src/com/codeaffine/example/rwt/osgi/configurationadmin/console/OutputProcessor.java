package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class OutputProcessor implements Runnable {
  private final File consoleOut;
  private final LineWriter lineWriter;
  private volatile boolean shutdown;

  public OutputProcessor( File consoleOut, LineWriter lineWriter ) {
    this.consoleOut = consoleOut;
    this.lineWriter = lineWriter;
  }

  public void run() {
    BufferedReader reader = openReader();
    try {
      while( !shutdown ) {
        processOutput( reader );
      }
    } finally {
      closeReader( reader );
    }
  }

  private void processOutput( BufferedReader reader ) {
    try {
      while( !reader.ready() ) {
        Thread.sleep( 100 );
      }
      lineWriter.writeLine( reader.readLine() );
    } catch( InterruptedException ie ) {
      // shutdown started
    } catch( Exception shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  private void closeReader( BufferedReader reader ) {
    try {
      reader.close();
    } catch( IOException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  private BufferedReader openReader() {
    try {
      return new BufferedReader( new FileReader( consoleOut ) );
    } catch( FileNotFoundException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  public synchronized void shutdown() {
    shutdown = true;
  }
}
