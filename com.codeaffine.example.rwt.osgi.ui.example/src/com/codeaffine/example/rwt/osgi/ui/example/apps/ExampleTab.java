package com.codeaffine.example.rwt.osgi.ui.example.apps;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;


public class ExampleTab implements UIContributor {

  @Override
  public String getId() {
    return "Demo";
  }

  @Override
  public Control contribute( Composite parent ) {
    Composite result = new Composite( parent, SWT.NONE );
    result.setLayout( new RowLayout( SWT.VERTICAL ) );

    for( int i = 0; i < 20; i++ ) {
      Label label = new Label( result, SWT.NONE );
      label.setText( "This is row " + i +" of the demo page content." );
    }
    return result;
  }
}
