/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.example.ece2011.ui.admin;

import java.util.List;

import org.apache.felix.scr.Component;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


@SuppressWarnings( "serial" )
public class UiComponentDialog {

  private final Shell parent;
  private final Shell shell;
  private final Component component;
  private Button deployButton;
  private Button cancelButton;
  private Combo portCombo;

  public UiComponentDialog( Shell parent, Component component ) {
    this.parent = parent;
    this.component = component;
    shell = new Shell( parent, SWT.BORDER | SWT.APPLICATION_MODAL );
    createContents();
    shell.pack();
  }

  public void open() {
    Point size = shell.getSize();
    Point center = getScreenCenter();
    shell.setLocation( center.x - size.x / 2, center.y - size.y / 2 );
    shell.open();
  }

  private void createContents() {
    GridLayout mainLayout = new GridLayout( 2, false );
    mainLayout.marginWidth = 20;
    mainLayout.marginTop = 15;
    mainLayout.marginBottom = 10;
    shell.setLayout( mainLayout );
    createLabels( shell );
    createCombo( shell );
    Control buttons = createButtons( shell );
    GridData buttonData = new GridData( SWT.RIGHT, SWT.BOTTOM, false, false );
    buttonData.horizontalSpan = 2;
    buttons.setLayoutData( buttonData );
    shell.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent e ) {
        shell.pack();
      }
    } );
  }

  private void createLabels( Composite parent ) {
    Label headLabel = new Label( parent, SWT.NONE );
    headLabel.setText( UiComponents.isApplication( component ) ? "Application" : "UIContribution" );
    headLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    GridData gridData = new GridData();
    gridData.horizontalSpan = 2;
    headLabel.setLayoutData( gridData );
    new Label( parent, SWT.NONE ).setText( "Name:" );
    Label nameLabel = new Label( parent, SWT.WRAP );
    nameLabel.setText( component.getName() );
    new Label( parent, SWT.NONE ).setText( "Bundle:" );
    Label bundleLabel = new Label( parent, SWT.WRAP );
    bundleLabel.setText( component.getBundle().getSymbolicName() );
  }

  private void createCombo( Composite parent ) {
    List<String> ports = UiComponents.getAvailablePorts();
    Label label = new Label( parent, SWT.WRAP );
    label.setText( "Port: " );
    portCombo = new Combo( parent, SWT.CHECK | SWT.READ_ONLY );
    portCombo.setItems( ports.toArray( new String[ ports.size() ] ) );
    portCombo.select( 0 );
    portCombo.setLayoutData( new GridData( 200, SWT.DEFAULT ) );
  }

  private Control createButtons( Composite parent ) {
    Composite buttonBar = new Composite( parent, SWT.NONE );
    RowLayout layout = new RowLayout( SWT.HORIZONTAL );
    layout.marginTop = 20;
    layout.marginRight = 0;
    layout.marginBottom = 0;
    layout.spacing = 6;
    buttonBar.setLayout( layout );
    deployButton = new Button( buttonBar, SWT.PUSH );
    deployButton.setText( "Deploy" );
    deployButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        int port = Integer.parseInt( portCombo.getText() );
        UiComponents.deploy( component, port );
        shell.close();
      }
    } );
    cancelButton = new Button( buttonBar, SWT.PUSH );
    cancelButton.setText( "Cancel" );
    cancelButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        shell.close();
      }
    } );
    shell.setDefaultButton( deployButton );
    return buttonBar;
  }

  private Point getScreenCenter() {
    Rectangle screen = parent.getDisplay().getPrimaryMonitor().getClientArea();
    return new Point( screen.x + screen.width / 2, screen.y + screen.height / 2 );
  }

}
