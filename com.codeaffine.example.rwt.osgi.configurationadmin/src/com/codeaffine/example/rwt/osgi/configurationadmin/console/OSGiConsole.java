package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.osgi.framework.console.ConsoleSession;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;


public class OSGiConsole {
  
  private Composite composite;
  private OutputProcessor outputProcessor;
  private ExecutorService executorService;
  private ServiceRegistration<ConsoleSession> serviceRegistration;
  private Text consoleWidget;

  public void create( Composite parent ) {
    composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new FillLayout() );
    UICallBack.activate( String.valueOf( composite.hashCode() ) );
    consoleWidget = new Text( composite, SWT.MULTI );
    File consoleIn = createTempFile( "consoleIn" );
    File consoleOut = createTempFile( "consoleOut" );
    registerInputProcessor( consoleWidget, consoleIn );
    registerOutputProcessor( consoleWidget, consoleOut );
    registerConsoleSession( consoleIn, consoleOut );
  }
  
  public Control getControl() {
    return consoleWidget;
  }
  
  private void registerInputProcessor( Text consoleWidget, File consoleIn ) {
    InputProcessor inputProcessor = new InputProcessor( consoleIn );
    consoleWidget.addKeyListener( new CommandParser( inputProcessor, consoleWidget ) );
  }
  
  private void registerOutputProcessor( Text consoleWidget, File consoleOut ) {
    executorService = Executors.newSingleThreadExecutor();
    outputProcessor = new OutputProcessor( consoleOut, new LineWriter( consoleWidget ) );
    executorService.execute( outputProcessor );
  }
  
  private void registerConsoleSession( File consoleIn, File consoleOut ) {
    BundleContext context = FrameworkUtil.getBundle( getClass() ).getBundleContext();
    OSGiConsoleSession session = new OSGiConsoleSession( consoleIn, consoleOut );
    Hashtable<String,Boolean> properties = new Hashtable<String, Boolean>();
    properties.put( "console.systemInOut", Boolean.TRUE );
    serviceRegistration = context.registerService( ConsoleSession.class, session, properties );
  }
  
  private File createTempFile( String prefix ) {
    try {
      return File.createTempFile( prefix, "tmp" );
    } catch( IOException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }
  
  public void dispose() {
    if( composite != null ) {
      UICallBack.deactivate( String.valueOf( composite.hashCode() ) );
      outputProcessor.shutdown();
      executorService.shutdown();
      serviceRegistration.unregister();
      consoleWidget = null;
      if( !composite.isDisposed() ) {
        composite.dispose();
      }
      composite = null;
    }
  }
}