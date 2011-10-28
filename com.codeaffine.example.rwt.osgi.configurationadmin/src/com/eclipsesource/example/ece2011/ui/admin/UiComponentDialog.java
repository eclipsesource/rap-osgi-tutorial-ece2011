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

import java.util.ArrayList;
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
import org.eclipse.swt.widgets.Display;
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
  private Combo parentCombo;
  private Images images;
  private boolean isApplication;

  public UiComponentDialog( Shell parent, Component component ) {
    this.parent = parent;
    this.component = component;
    isApplication = UiComponents.isApplication( component );
    shell = new Shell( parent, SWT.BORDER | SWT.APPLICATION_MODAL );
    createImages( shell.getDisplay() );
    createContents( shell );
    shell.pack();
    fixShellResize();
  }

  public void open() {
    Point size = shell.getSize();
    Point center = getScreenCenter();
    shell.setLocation( center.x - size.x / 2, center.y - size.y / 2 );
    shell.open();
  }

  private void createImages( Display display ) {
    images = new Images( display );
  }

  private void createContents( Composite parent ) {
    parent.setLayout( createMainLayout() );
    createHeader( shell );
    createLabels( shell );
    createPortCombo( shell );
    if( !isApplication ) {
      createParentApplicationCombo( shell );
    }
    Control buttonBar = createButtonBar( shell );
    buttonBar.setLayoutData( createButtonBarData() );
    updatePortCombo();
    updateParentCombo();
  }

  private void fixShellResize() {
    shell.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent e ) {
        shell.pack();
      }
    } );
  }

  private void createHeader( Composite parent ) {
    Composite header = new Composite( parent, SWT.NONE );
    RowLayout headerLayout = new RowLayout();
    headerLayout.marginLeft = 0;
    headerLayout.spacing = 8;
    headerLayout.center = true;
    header.setLayout( headerLayout );
    GridData layoutData = new GridData();
    layoutData.horizontalSpan = 2;
    header.setLayoutData( layoutData );
    Label iconLabel = new Label( header, SWT.CENTER );
    Label textLabel = new Label( header, SWT.CENTER );
    textLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    textLabel.setText( isApplication ? "Application" : "Contribution" );
    iconLabel.setImage( isApplication ? images.applicationImage : images.contributionImage );
  }

  private void createLabels( Composite parent ) {
    new Label( parent, SWT.NONE ).setText( "Name:" );
    Label nameLabel = new Label( parent, SWT.WRAP );
    nameLabel.setText( component.getName() );
    new Label( parent, SWT.NONE ).setText( "Bundle:" );
    Label bundleLabel = new Label( parent, SWT.WRAP );
    bundleLabel.setText( component.getBundle().getSymbolicName() );
  }

  private void createPortCombo( Composite parent ) {
    Label label = new Label( parent, SWT.WRAP );
    label.setText( "Port: " );
    portCombo = new Combo( parent, SWT.CHECK | SWT.READ_ONLY );
    portCombo.select( 0 );
    portCombo.setLayoutData( new GridData( 200, SWT.DEFAULT ) );
    portCombo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {        
        updateParentCombo();
      }
    } );
  }

  private void createParentApplicationCombo( Composite parent ) {
    Label label = new Label( parent, SWT.WRAP );
    label.setText( "Parent: " );
    parentCombo = new Combo( parent, SWT.CHECK | SWT.READ_ONLY );
    parentCombo.select( 0 );
    parentCombo.setLayoutData( new GridData( 200, SWT.DEFAULT ) );
  }

  private Control createButtonBar( Composite parent ) {
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

  private void updatePortCombo() {
    List<String> ports = UiComponents.getAvailablePorts();
    String oldText = portCombo.getText();
    portCombo.setItems( ports.toArray( new String[ ports.size() ] ) );
    portCombo.setText( oldText );
    if( portCombo.getSelectionIndex() < 0 && portCombo.getItemCount() > 0 ) {
      portCombo.select( 0 );
    }
  }

  private void updateParentCombo() {
    if( parentCombo != null ) {
      String port = portCombo.getText();
      List< Component > activeComponents = UiComponents.getActiveComponents( port );
      List< String > items = new ArrayList< String >();
      for( Component component : activeComponents ) {
        items.add( component.getName() );
      }
      String oldText = parentCombo.getText();
      parentCombo.setItems( items.toArray( new String[ items.size() ] ) );
      parentCombo.setText( oldText );
      if( parentCombo.getSelectionIndex() < 0 && parentCombo.getItemCount() > 0 ) {
        parentCombo.select( 0 );
      }
    }
  }

  private Point getScreenCenter() {
    Rectangle screen = parent.getDisplay().getPrimaryMonitor().getClientArea();
    return new Point( screen.x + screen.width / 2, screen.y + screen.height / 2 );
  }

  private static GridLayout createMainLayout() {
    GridLayout mainLayout = new GridLayout( 2, false );
    mainLayout.marginWidth = 20;
    mainLayout.marginTop = 15;
    mainLayout.marginBottom = 10;
    return mainLayout;
  }

  private static GridData createButtonBarData() {
    GridData buttonData = new GridData( SWT.RIGHT, SWT.BOTTOM, false, false );
    buttonData.horizontalSpan = 2;
    return buttonData;
  }

}
