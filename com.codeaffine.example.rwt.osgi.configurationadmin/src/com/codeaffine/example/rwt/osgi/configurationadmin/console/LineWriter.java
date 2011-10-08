package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

class LineWriter {

  final Text consoleWidget;
  final Display display;

  LineWriter( Text consoleWidget ) {
    this.consoleWidget = consoleWidget;
    this.display = consoleWidget.getDisplay();
  }

  void writeLine( final String line ) {
    display.asyncExec( new Runnable() {
      public void run() {
        consoleWidget.append( "\r\n" + line );
        consoleWidget.pack();
        consoleWidget.setSelection( consoleWidget.getText().length() );
        consoleWidget.getParent().layout( true, true );
      }
    } );
  }
}