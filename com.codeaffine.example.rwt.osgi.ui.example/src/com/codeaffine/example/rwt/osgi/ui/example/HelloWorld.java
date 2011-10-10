package com.codeaffine.example.rwt.osgi.ui.example;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;


public class HelloWorld implements UIContributor {

  @Override
  public String getId() {
    return "Hello";
  }

  @Override
  public Control contribute( Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Hello World" );
    return label;
  }
}
