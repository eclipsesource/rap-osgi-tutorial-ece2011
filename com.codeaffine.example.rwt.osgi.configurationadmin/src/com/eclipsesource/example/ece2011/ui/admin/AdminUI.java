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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.Component;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


@SuppressWarnings( "serial" )
public class AdminUI implements IEntryPoint {

  private Shell shell;
  private Table applicationsTable;
  private Table contributionsTable;
  private Image activeImage;
  private Image inactiveImage;

  public int createUI() {
    Display display = new Display();
    shell = new Shell( display, SWT.NO_TRIM );
    shell.setMaximized( true );
    GridLayout mainLayout = new GridLayout();
    mainLayout.marginTop = 15;
    mainLayout.marginWidth = 40;
    mainLayout.marginBottom = 10;
    shell.setLayout( mainLayout );
    createImages( display );
    createContent( shell );
    update();
    shell.layout();
    shell.open();
    return 0;
  }

  private void createImages( Display display ) {
    activeImage = createImage( display, "resources/status-active-16.png" );
    inactiveImage = createImage( display, "resources/status-inactive-16.png" );
  }

  private void createContent( Composite parent ) {
    Label applicationsLabel = new Label( parent, SWT.NONE );
    applicationsLabel.setText( "Applications" );
    applicationsLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    applicationsTable = createTable( parent );
    applicationsTable.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    Label contributionsLabel = new Label( parent, SWT.NONE );
    contributionsLabel.setText( "Contributions" );
    contributionsLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    contributionsTable = createTable( parent );
    contributionsTable.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "update" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        update();
      }
    } );
  }

  private Table createTable( Composite parent ) {
    Table table = new Table( parent, SWT.SINGLE );
    table.setLinesVisible( true );
    table.setHeaderVisible( true );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setWidth( 120 );
    column1.setText( "State" );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setWidth( 200 );
    column2.setText( "Name" );
    TableColumn column3 = new TableColumn( table, SWT.NONE );
    column3.setWidth( 500 );
    column3.setText( "Bundle" );
    table.addSelectionListener( new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        TableItem item = ( TableItem )e.item;
        if( item != null ) {
          Component component = ( Component )item.getData();
          if( component != null ) {
            handleSelection( component );
          }
        }
      }
    } );
    return table;
  }

  protected void handleSelection( final Component component ) {
    UiComponentDialog dialog = new UiComponentDialog( shell, component );
    dialog.open();
  }

  protected void update() {
    Component[] components = UiComponents.getAllComponents();
    renderComponents( components );
  }

  private void renderComponents( Component[] components ) {
    List<Component> applications = new ArrayList<Component>();
    List<Component> contributions = new ArrayList<Component>();
    if( components != null ) {
      for( Component component : components ) {
        if( UiComponents.isApplication( component ) ) {
          applications.add( component );
        }
        if( UiComponents.isUiContribution( component ) ) {
          contributions.add( component );
        }
      }
    }
    clearTableItems( applicationsTable );
    for( Component component : applications ) {
      createTableItem( applicationsTable, component );
    }
    clearTableItems( contributionsTable );
    for( Component component : contributions ) {
      createTableItem( contributionsTable, component );
    }
  }

  private void clearTableItems( Table table ) {
    TableItem[] items = table.getItems();
    for( TableItem item : items ) {
      item.dispose();
    }
  }

  private void createTableItem( Table table, Component component ) {
    TableItem item = new TableItem( table, SWT.NONE );
    item.setData( component );
    item.setImage( 0, getStatusImage( component ) );
    item.setText( 0, getStatusText( component ) );
    item.setText( 1, component.getName() );
    item.setText( 2, component.getBundle().getSymbolicName() );
  }

  private Image getStatusImage( Component component ) {
    Image result;
    int state = component.getState();
    if( state == Component.STATE_ACTIVE ) {
      result = activeImage;
    } else {
      result = inactiveImage;
    }
    return result;
  }

  private String getStatusText( Component component ) {
    String result;
    int state = component.getState();
    if( state == Component.STATE_ACTIVE ) {
      result = "active";
    } else {
      result = "inactive";
    }
    result += " (" + state + ")";
    return result;
  }

  private static Image createImage( Display display, String name ) {
    Image result;
    ClassLoader classLoader = AdminUI.class.getClassLoader();
    InputStream inputSteam = classLoader.getResourceAsStream( name  );
    if( inputSteam == null ) {
      throw new IllegalArgumentException( "Image not found" );
    }
    try {
      result = new Image( display, inputSteam );
    } finally {
      try {
        inputSteam.close();
      } catch( IOException e ) {
      }
    }
    return result;
  }

}
