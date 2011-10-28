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
import java.util.Collections;
import java.util.List;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

  private static final String UICALLBACK_ID = AdminUI.class.getName();
  private Shell shell;
  private TabFolder portsTabFolder;
  private Table contributionsTable;
  private Images images;
  private ChangeTracker changeTracker;

  public int createUI() {
    final Display display = new Display();
    shell = new Shell( display, SWT.NO_TRIM );
    shell.setMaximized( true );
    createContent( shell );
    shell.layout();
    shell.open();
    changeTracker = createChangeTracker( display );
    UICallBack.activate( UICALLBACK_ID );
    changeTracker.start();
    shell.addDisposeListener( new DisposeListener() {
      
      public void widgetDisposed( DisposeEvent event ) {
        changeTracker.stop();
        UICallBack.deactivate( UICALLBACK_ID );
      }
    } );
    return 0;
  }

  public void createContent( Composite parent ) {
    createImages( parent.getDisplay() );
    parent.setLayout( createMainLayout() );
    Control upperPart = createUpperPart( parent );
    upperPart.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    Control lowerPart = createLowerPart( parent );
    lowerPart.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    update();
  }

  private void createImages( Display display ) {
    images = new Images( display );
  }

  private Control createUpperPart( Composite parent ) {
    Composite frame = new Composite( parent, SWT.NONE );
    frame.setLayout( createGridLayout() );
    Label headerLabel = new Label( frame, SWT.NONE );
    headerLabel.setText( "Deployed UI Contributions" );
    headerLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    portsTabFolder = new TabFolder( frame, SWT.TOP );
    portsTabFolder.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    createTabItemsForPorts();
    return frame;
  }

  private Control createLowerPart( Composite parent ) {
    Composite frame = new Composite( parent, SWT.NONE );
    frame.setLayout( createGridLayout() );
    Label headerLabel = new Label( frame, SWT.NONE );
    headerLabel.setText( "Available UI Contributions" );
    headerLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    contributionsTable = createTable( frame, SWT.BORDER );
    contributionsTable.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    return frame;
  }

  private Table createTable( Composite parent, int style ) {
    Table table = new Table( parent, style | SWT.SINGLE | SWT.HIDE_SELECTION );
    table.setLinesVisible( true );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    column1.setWidth( 32 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setWidth( 200 );
    TableColumn column3 = new TableColumn( table, SWT.NONE );
    column3.setWidth( 500 );
    table.addSelectionListener( new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        TableItem item = ( TableItem )e.item;
        if( item != null ) {
          UiComponent component = ( UiComponent )item.getData();
          if( component != null ) {
            handleSelection( component );
          }
        }
      }
    } );
    return table;
  }

  protected void handleSelection( final UiComponent component ) {
    UiComponentDialog dialog = new UiComponentDialog( shell, component );
    dialog.open();
  }

  protected void update() {
    createTabItemsForPorts();
    createItemsForAvailableComponents();
    shell.layout( true );
  }

  private void createItemsForAvailableComponents() {
    List<UiComponent> components = UiComponents.getAvailableComponents();
    List<UiComponent> contributions = new ArrayList<UiComponent>();
    for( UiComponent component : components ) {
      contributions.add( component );
    }
    Collections.sort( contributions, new UIComponentComparator() ); 
    clearTableItems( contributionsTable );
    for( UiComponent component : contributions ) {
      createContributionItem( contributionsTable, component );
    }
    fillEmptyItems( contributionsTable, 7 );
  }

  private void createItemsForActiveComponents( Table table, String port ) {
    clearTableItems( table );
    List<UiComponent> contributions = UiComponents.getActiveComponents( port );
    Collections.sort( contributions, new UIComponentComparator() ); 
    for( UiComponent component : contributions ) {
      createContributionItem( table, component );
    }
    fillEmptyItems( table, 4 );
  }

  private void createTabItemsForPorts() {
    List<String> ports = UiComponents.getAvailablePorts();
    String oldPort = null;
    int selected = portsTabFolder.getSelectionIndex();
    if( selected >= 0 ) {
      oldPort = ( String )portsTabFolder.getItem( selected ).getData();
    }
    TabItem[] items = portsTabFolder.getItems();
    for( TabItem item : items ) {
      item.dispose();
    }
    for( String port : ports ) {
      TabItem item = createPortTabItem( port );
      if( port.equals( oldPort ) ) {
        portsTabFolder.setSelection( item );
      }
    }
  }

  private TabItem createPortTabItem( String port ) {
    TabItem tabItem = new TabItem( portsTabFolder, SWT.NONE );
    tabItem.setText( "Port " + port );
    tabItem.setData( port );
    Table table = createTable( portsTabFolder, SWT.NONE );
    table.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    tabItem.setControl( table );
    createItemsForActiveComponents( table, port );
    return tabItem;
  }

  private static void clearTableItems( Table table ) {
    TableItem[] items = table.getItems();
    for( TableItem item : items ) {
      item.dispose();
    }
  }

  private static void fillEmptyItems( Table table, int minItemCount ) {
    for( int i = table.getItemCount(); i < minItemCount; i++ ) {
      new TableItem( table, SWT.NONE );
    }
  }

  private void createContributionItem( Table table, UiComponent component ) {
    TableItem item = new TableItem( table, SWT.NONE );
    item.setData( component );
    item.setImage( 0, getTypeImage( component ) );
    item.setText( 1, component.getName() );
    item.setText( 2, component.getBundleName() );
  }

  private Image getTypeImage( UiComponent component ) {
    Image result;
    if( component.isApplication() ) {
      result = images.applicationImage;
    } else {
      result = images.contributionImage;
    }
    return result;
  }

  private ChangeTracker createChangeTracker( final Display display ) {
    return new ChangeTracker() {
      @Override
      protected void update() {
        display.asyncExec( new Runnable() {
          public void run() {
            AdminUI.this.update();
          }
        } );
      }
    };
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
    layout.verticalSpacing = 20;
    return layout;
  }

}
