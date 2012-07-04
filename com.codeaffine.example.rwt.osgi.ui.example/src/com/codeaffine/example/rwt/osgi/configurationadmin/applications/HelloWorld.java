/*******************************************************************************
 * Copyright (c) 2011 Frank Appel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.codeaffine.example.rwt.osgi.configurationadmin.applications;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class HelloWorld implements IEntryPoint {

  public int createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.TITLE | SWT.MAX | SWT.RESIZE );
    shell.setBounds( new Rectangle( 30, 30, 300, 250 ) );
    shell.setLayout( new FillLayout() );

    Label label = new Label( shell, SWT.NONE );
    label.setText( "HelloWorld" );

    shell.open();
    return 0;
  }
}
