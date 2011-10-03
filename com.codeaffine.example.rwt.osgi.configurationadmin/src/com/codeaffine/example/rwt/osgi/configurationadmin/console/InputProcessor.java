package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

class InputProcessor {
  
  private final PrintWriter printWriter;
  private final StringBuilder command;

  InputProcessor( File consoleIn ) {
    this.command = new StringBuilder();
    try {
      this.printWriter = new PrintWriter( consoleIn );
    } catch( FileNotFoundException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  void processCommand() {
    printWriter.println( command.toString() );
    printWriter.flush();
    command.setLength( 0 );
  }
  
  void removeLastCommandCharacter() {
    if( command.length() > 0 ) {
      command.deleteCharAt( command.length() - 1 );
    }
  }

  public void appendCommandCharacter( String character ) {
    command.append( character );      
  }
}