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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


@SuppressWarnings( "serial" )
public class AdminUI implements IEntryPoint {

  private Shell shell;
  private Table contributionsTable;
  private Table activeContributionsTable;
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
    SashForm sashForm = new SashForm( parent, SWT.NONE );
    sashForm.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    Composite leftFrame = new Composite( sashForm, SWT.NONE );
    Composite rightFrame = new Composite( sashForm, SWT.NONE );
    createLeftTable( leftFrame );
    createRightTable( rightFrame );
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "update" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        update();
      }
    } );
  }

  private void createLeftTable( Composite parent ) {
    parent.setLayout( createGridLayout() );
    Label headerLabel = new Label( parent, SWT.NONE );
    headerLabel.setText( "Available Contributions" );
    headerLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    contributionsTable = createTable( parent );
    contributionsTable.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    contributionsTable.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
  }

  private void createRightTable( Composite parent ) {
    parent.setLayout( createGridLayout() );
    Label headerLabel = new Label( parent, SWT.NONE );
    headerLabel.setText( "Deployed" );
    headerLabel.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    activeContributionsTable = createTable( parent );
    activeContributionsTable.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    activeContributionsTable.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
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
    Component[] components = UiComponents.getAllComponents();
    renderComponents( components );
  }

  private void renderComponents( Component[] components ) {
    List<Component> activeContributions = new ArrayList<Component>();
    List<Component> contributions = new ArrayList<Component>();
    if( components != null ) {
      for( Component component : components ) {
        contributions.add( component );
        if( component.getState() == Component.STATE_ACTIVE ) {
          activeContributions.add( component );
        }
      }
    }
    Collections.sort( contributions, new UIComponentComparator() ); 
    Collections.sort( activeContributions, new UIComponentComparator() ); 
    clearTableItems( contributionsTable );
    for( Component component : contributions ) {
      createActiveContributionItem( contributionsTable, component );
    }
    clearTableItems( activeContributionsTable );
    for( Component component : activeContributions ) {
      createContributionItem( activeContributionsTable, component );
    }
  }

  private void clearTableItems( Table table ) {
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

  private void createActiveContributionItem( Table table, Component component ) {
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

  private static final class UIComponentComparator implements Comparator< Component > {
  
    public int compare( Component component1, Component component2 ) {
      int result;
      boolean isApplication1 = UiComponents.isApplication( component1 );
      boolean isApplication2 = UiComponents.isApplication( component2 );
      if( isApplication1 && !isApplication2 ) {
        result = -1;
      } else if( !isApplication1 && isApplication2 ) {
        result = 1;
      } else {
        String name1 = component1.getName().toLowerCase( Locale.ENGLISH );
        String name2 = component2.getName().toLowerCase( Locale.ENGLISH );
        result = name1.compareTo( name2 );
      }
      return result;
    }
  }

}
