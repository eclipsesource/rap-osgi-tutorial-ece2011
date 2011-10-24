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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


@SuppressWarnings( "serial" )
public class UiComponentDialog {

  private final Shell parent;
  private final Shell shell;
  private final Component component;
  private Button enablementButton;
  private boolean isInEnableMode;

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
    GridLayout mainLayout = new GridLayout();
    mainLayout.marginWidth = 20;
    mainLayout.marginTop = 15;
    mainLayout.marginBottom = 10;
    shell.setLayout( mainLayout );
    Label headLabel = new Label( shell, SWT.NONE );
    headLabel.setText( UiComponents.isApplication( component ) ? "Application" : "UIContribution" );
    headLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    Label label = new Label( shell, SWT.NONE );
    label.setText( "Name: " + component.getName() );
    Label bundleLabel = new Label( shell, SWT.NONE );
    bundleLabel.setText( "Contributing bundle: " + component.getBundle().getSymbolicName() );
    isInEnableMode = component.getState() != Component.STATE_ACTIVE;
    Control buttons = createButtons( shell );
    buttons.setLayoutData( new GridData( SWT.RIGHT, SWT.BOTTOM, false, false ) );
    shell.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent e ) {
        shell.pack();
      }
    } );
  }

  private Control createButtons( Composite parent ) {
    Composite buttonBar = new Composite( parent, SWT.NONE );
    RowLayout layout = new RowLayout( SWT.HORIZONTAL );
    layout.marginTop = 20;
    layout.marginRight = 0;
    layout.marginBottom = 0;
    buttonBar.setLayout( layout );
    enablementButton = new Button( buttonBar, SWT.PUSH );
    enablementButton.setText( isInEnableMode ? "enable" : "disable" );
    enablementButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        if( isInEnableMode ) {
          // TODO fixed port
          UiComponents.deploy( component, 9090 );
        } else {
          // TODO fixed port
          UiComponents.undeploy( component, 9090 );
        }
        shell.close();
      }
    } );
    shell.setDefaultButton( enablementButton );
    return buttonBar;
  }

  private Point getScreenCenter() {
    Rectangle screen = parent.getDisplay().getPrimaryMonitor().getClientArea();
    return new Point( screen.x + screen.width / 2, screen.y + screen.height / 2 );
  }

}
