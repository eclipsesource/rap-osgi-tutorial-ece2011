package com.codeaffine.example.rwt.osgi.configurationadmin.console;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class OSGiConsoleEntryPoint implements IEntryPoint {

  public int createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.TITLE | SWT.MAX | SWT.RESIZE );
    shell.setBounds( new Rectangle( 30, 30, 700, 500 ) );
    shell.setLayout( new FillLayout() );

    OSGiConsole console = new OSGiConsole();
    console.create( shell );
    console.getControl().setFocus();

    shell.open();
    return 0;
  }
}
