package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;


class CommandParser implements KeyListener {

  private static final long serialVersionUID = 1L;

  private final InputProcessor inputProcessor;
  private final Text consoleWidget;

  CommandParser( InputProcessor inputProcessor, Text consoleWidget ) {
    this.inputProcessor = inputProcessor;
    this.consoleWidget = consoleWidget;
  }

  public void keyReleased( KeyEvent event ) {
    if( enterPressed( event ) ) {
      inputProcessor.processCommand();
    }
    consoleWidget.getParent().layout( true, true );
  }

  public void keyPressed( KeyEvent event ) {
    String value = consoleWidget.getText();
    String newValue = value;
    if( backSpacePressed( event ) ) {
      newValue = removeLastCommandCharacter( value );
    } else if( anyKeyPressed( event ) ) {
      newValue = appendLastCommandCharacter( event, value );
    }
    if( !enterPressed( event ) ) {
      event.doit = false;
    }
    consoleWidget.setText( newValue );
    consoleWidget.setSelection( consoleWidget.getText().length() );
    consoleWidget.getParent().layout( true, true );
  }

  private String appendLastCommandCharacter( KeyEvent event, String value ) {
    String filtered = filterWhiteSpacesExceptSpace( event );
    inputProcessor.appendCommandCharacter( filtered );
    return value + filtered;
  }

  private String removeLastCommandCharacter( String value ) {
    inputProcessor.removeLastCommandCharacter();
    return value.substring( 0, value.length() - 1 );
  }

  private String filterWhiteSpacesExceptSpace( KeyEvent event ) {
    String result = new StringBuilder().append( event.character ).toString();
    if( event.character != ' ' ) {
      result = result.trim();
    }
    return result;
  }

  private boolean anyKeyPressed( KeyEvent event ) {
    return event.character != 13;
  }

  private boolean backSpacePressed( KeyEvent event ) {
    return event.character == 8;
  }

  private boolean enterPressed( KeyEvent event ) {
    return event.character == 13;
  }
}
