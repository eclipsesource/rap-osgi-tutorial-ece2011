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
import java.util.Collections;
import java.util.List;

import org.apache.felix.scr.Component;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.eclipsesource.example.ece2011.ui.admin.UiComponents.UIComponentComparator;


@SuppressWarnings( "serial" )
public class AdminUI implements IEntryPoint {

  private Shell shell;
  private TabFolder portsTabFolder;
  private Table contributionsTable;
  private Image applicationImage;
  private Image contributionImage;

  public int createUI() {
    Display display = new Display();
    shell = new Shell( display, SWT.NO_TRIM );
    shell.setMaximized( true );
    createImages( display );
    createContent( shell );
    update();
    shell.layout();
    shell.open();
    return 0;
  }

  private void createImages( Display display ) {
    applicationImage = createImage( display, "resources/status-active-16.png" );
    contributionImage = createImage( display, "resources/status-inactive-16.png" );
  }

  private void createContent( Composite parent ) {
    parent.setLayout( createMainLayout() );
    SashForm sashForm = new SashForm( parent, SWT.VERTICAL );
    sashForm.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    Composite upperFrame = new Composite( sashForm, SWT.NONE );
    Composite lowerFrame = new Composite( sashForm, SWT.NONE );
    createUpperPart( upperFrame );
    createLowerPart( lowerFrame );
    createUpdateButton( parent );
  }

  private void createUpdateButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "update" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        update();
      }
    } );
  }

  private void createUpperPart( Composite parent ) {
    parent.setLayout( createGridLayout() );
    Label headerLabel = new Label( parent, SWT.NONE );
    headerLabel.setText( "Deployed" );
    headerLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    portsTabFolder = new TabFolder( parent, SWT.TOP );
    portsTabFolder.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, true ) );
    createPortsTabItems();
  }

  private void createLowerPart( Composite parent ) {
    parent.setLayout( createGridLayout() );
    Label headerLabel = new Label( parent, SWT.NONE );
    headerLabel.setText( "Available Contributions" );
    headerLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    contributionsTable = createTable( parent );
    contributionsTable.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
  }

  private void createPortTabItem( String port ) {
    TabItem tabItem = new TabItem( portsTabFolder, SWT.NONE );
    tabItem.setText( "Port " + port );
    Table table = createTable( portsTabFolder );
    table.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    tabItem.setControl( table );
    createComponents( table, port );
  }

  private Table createTable( Composite parent ) {
    Table table = new Table( parent, SWT.SINGLE );
    table.setLinesVisible( true );
    table.setHeaderVisible( true );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setWidth( 32 );
    column1.setText( "Type" );
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
    renderAvailableComponents();
    shell.layout( true );
  }

  private void renderAvailableComponents() {
    List<Component> components = UiComponents.getAllComponents();
    List<Component> contributions = new ArrayList<Component>();
    for( Component component : components ) {
      if( "require".equals( component.getConfigurationPolicy() ) ) {
        contributions.add( component );
      }
    }
    Collections.sort( contributions, new UIComponentComparator() ); 
    clearTableItems( contributionsTable );
    for( Component component : contributions ) {
      createContributionItem( contributionsTable, component );
    }
  }

  private void createComponents( Table table, String port ) {
    clearTableItems( table );
    List<Component> contributions = UiComponents.getActiveComponents( port );
    Collections.sort( contributions, new UIComponentComparator() ); 
    for( Component component : contributions ) {
      createContributionItem( table, component );
    }
  }

  private void createPortsTabItems() {
    List<String> ports = UiComponents.getAvailablePorts();
    TabItem[] items = portsTabFolder.getItems();
    for( TabItem item : items ) {
      item.dispose();
    }
    for( String port : ports ) {
      createPortTabItem( port );
    }
  }

  private static void clearTableItems( Table table ) {
    TableItem[] items = table.getItems();
    for( TableItem item : items ) {
      item.dispose();
    }
  }

  private void createContributionItem( Table table, Component component ) {
    TableItem item = new TableItem( table, SWT.NONE );
    item.setData( component );
    item.setImage( 0, getTypeImage( component ) );
    item.setText( 1, component.getName() );
    item.setText( 2, component.getBundle().getSymbolicName() );
  }

  private Image getTypeImage( Component component ) {
    Image result;
    if( UiComponents.isApplication( component ) ) {
      result = applicationImage;
    } else {
      result = contributionImage;
    }
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

  private static GridLayout createGridLayout() {
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    return layout;
  }

  private static GridLayout createMainLayout() {
    GridLayout layout = new GridLayout();
    layout.marginTop = 15;
    layout.marginWidth = 40;
    layout.marginBottom = 10;
    return layout;
  }

}
