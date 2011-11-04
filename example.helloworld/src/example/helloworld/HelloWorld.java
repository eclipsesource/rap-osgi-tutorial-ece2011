package example.helloworld;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class HelloWorld implements IEntryPoint {

  @Override
  public int createUI() {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new GridLayout( 1, false ) );

    Button button = new Button( shell, SWT.PUSH );
    button.setText( "Hello world" );

    shell.pack();
    shell.open();
    return 0;
  }

}
