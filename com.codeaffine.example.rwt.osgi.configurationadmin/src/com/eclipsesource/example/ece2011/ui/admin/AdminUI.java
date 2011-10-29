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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.eclipsesource.example.ece2011.ui.admin.UiComponents.UIComponentComparator;


@SuppressWarnings( "serial" )
public class AdminUI implements IEntryPoint {

  private static final String UICALLBACK_ID = AdminUI.class.getName();
  private Shell shell;
  private TabFolder portsTabFolder;
  private Tree contributionsTree;
  private Images images;
  private ChangeTracker changeTracker;

  public int createUI() {
    final Display display = new Display();
    shell = new Shell( display, SWT.NO_TRIM );
    shell.setMaximized( true );
    createContent( shell );
    shell.layout();
    shell.open();
    UICallBack.activate( UICALLBACK_ID );
    shell.addDisposeListener( new DisposeListener() {

      public void widgetDisposed( DisposeEvent event ) {
        dispose();
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
    changeTracker = new UiChangeTracker();
    changeTracker.start();
  }

  public void dispose() {
    changeTracker.stop();
    UICallBack.deactivate( UICALLBACK_ID );
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
    contributionsTree = createTree( frame, SWT.BORDER );
    contributionsTree.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    return frame;
  }

  private Tree createTree( Composite parent, int style ) {
    Tree tree = new Tree( parent, style | SWT.SINGLE | SWT.FULL_SELECTION );
    tree.setLinesVisible( true );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setWidth( 64 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setWidth( 200 );
    TreeColumn column3 = new TreeColumn( tree, SWT.NONE );
    column3.setWidth( 500 );
    tree.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetDefaultSelected( SelectionEvent e ) {
        TreeItem item = ( TreeItem )e.item;
        if( item != null ) {
          UiComponent component = ( UiComponent )item.getData();
          if( component != null ) {
            handleSelection( component );
          }
        }
      }
    } );
    return tree;
  }

  protected void handleSelection( final UiComponent component ) {
    UiComponentDialog dialog = new UiComponentDialog( shell, component );
    String port = getSelectedPort();
    if( port != null ) {
      dialog.selectPort( port );
    }
    dialog.open();
  }

  protected void update() {
    createTabItemsForPorts();
    createTreeItemsForActiveComponents();
    createTreeItemsForAvailableComponents( contributionsTree );
    shell.layout( true );
  }

  private void createTreeItemsForActiveComponents() {
    TabItem[] tabItems = portsTabFolder.getItems();
    for( TabItem tabItem : tabItems ) {
      String port = (String)tabItem.getData();
      Tree tree = (Tree)tabItem.getControl();
      createTreeItemsForActiveComponents( tree, port );
    }
  }

  private void createTreeItemsForActiveComponents( Tree tree, String port ) {
    clearTreeItems( tree );
    List<UiComponent> contributions = UiComponents.getActiveComponents( port );
    Collections.sort( contributions, new UIComponentComparator() );
    for( UiComponent component : contributions ) {
      if( component.isApplication() ) {
        createContributionItem( tree, component );
      }
    }
    TreeItem[] parentItems = tree.getItems();
    for( TreeItem parentItem : parentItems ) {
      UiComponent parentComponent = (UiComponent)parentItem.getData();
      String parentName = parentComponent.getName();
      for( UiComponent component : contributions ) {
        if( component.isUiContribution() && parentName.equals( component.getApplication() ) ) {
          createContributionItem( parentItem, component );
        }
      }
      parentItem.setExpanded( true );
    }
    fillEmptyItems( tree, 4 );
  }

  private void createTreeItemsForAvailableComponents( Tree tree ) {
    List<UiComponent> components = UiComponents.getAvailableComponents();
    List<UiComponent> contributions = new ArrayList<UiComponent>();
    for( UiComponent component : components ) {
      contributions.add( component );
    }
    Collections.sort( contributions, new UIComponentComparator() );
    clearTreeItems( tree );
    for( UiComponent component : contributions ) {
      createContributionItem( tree, component );
    }
    fillEmptyItems( tree, 7 );
  }

  private void createTabItemsForPorts() {
    List<String> ports = UiComponents.getAvailablePorts();
    String selectedPort = getSelectedPort();
    TabItem[] items = portsTabFolder.getItems();
    for( TabItem item : items ) {
      if( !ports.contains( item.getData() ) ) {
        item.getControl().dispose();
        item.dispose();
      }
    }
    for( String port : ports ) {
      int insertIndex = getTabInsertPosition( port );
      if( insertIndex != -1 ) {
        createPortTabItem( port, insertIndex );
      }
    }
    selectTabForPort( selectedPort );
  }

  private String getSelectedPort() {
    String result = null;
    TabItem selectedItem = getSelectedItem( portsTabFolder );
    if( selectedItem != null ) {
      result = (String)selectedItem.getData();
    }
    return result;
  }

  private void selectTabForPort( String port ) {
    TabItem item = findTabItemWithData( portsTabFolder, port );
    if( item != null ) {
      selectTabItem( item );
    }
  }

  private int getTabInsertPosition( String port ) {
    int insertIndex = 0;
    int count = portsTabFolder.getItemCount();
    for( int i = 0; i < count; i++ ) {
      TabItem item = portsTabFolder.getItem( i );
      String itemPort = (String)item.getData();
      if( port.equals( itemPort ) ) {
        insertIndex = -1;
        break;
      } else if( port.compareTo( itemPort ) > 0 ) {
        insertIndex = i + 1;
      }
    }
    return insertIndex;
  }

  private TabItem createPortTabItem( String port, int index ) {
    TabItem tabItem = new TabItem( portsTabFolder, SWT.NONE, index );
    tabItem.setText( "Port " + port );
    tabItem.setData( port );
    Tree tree = createTree( portsTabFolder, SWT.NONE );
    tree.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    tabItem.setControl( tree );
    createTreeItemsForActiveComponents( tree, port );
    return tabItem;
  }

  private void createContributionItem( Tree parent, UiComponent component ) {
    TreeItem item = new TreeItem( parent, SWT.NONE );
    item.setData( component );
    item.setImage( 0, getTypeImage( component ) );
    item.setText( 1, component.getName() );
    item.setText( 2, component.getBundleName() );
  }

  private void createContributionItem( TreeItem parent, UiComponent component ) {
    TreeItem item = new TreeItem( parent, SWT.NONE );
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

  private static TabItem getSelectedItem( TabFolder folder ) {
    TabItem selectedItem = null;
    int selectionIndex = folder.getSelectionIndex();
    if( selectionIndex != -1 ) {
      selectedItem = folder.getItem( selectionIndex );
    }
    return selectedItem;
  }

  private static TabItem findTabItemWithData( TabFolder folder, Object data ) {
    TabItem[] items = folder.getItems();
    if( data != null ) {
      for( TabItem item : items ) {
        if( data.equals( item.getData() ) ) {
          return item;
        }
      }
    }
    return null;
  }

  private static void selectTabItem( TabItem item ) {
    if( !item.isDisposed() ) {
      TabFolder folder = item.getParent();
      folder.setSelection( item );
    }
  }

  private static void clearTreeItems( Tree tree ) {
    TreeItem[] items = tree.getItems();
    for( TreeItem item : items ) {
      item.dispose();
    }
  }

  private static void fillEmptyItems( Tree tree, int minItemCount ) {
    int itemCount = getFullItemCount( tree );
    for( int i = itemCount; i < minItemCount; i++ ) {
      new TreeItem( tree, SWT.NONE );
    }
  }

  private static int getFullItemCount( Tree tree ) {
    int result = 0;
    TreeItem[] items = tree.getItems();
    for( TreeItem item : items ) {
      result++;
      result += item.getItemCount();
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
    layout.verticalSpacing = 20;
    return layout;
  }

  private final class UiChangeTracker extends ChangeTracker {
    @Override
    protected void changeDetected() {
      if( !shell.isDisposed() ) {
        shell.getDisplay().asyncExec( new Runnable() {
          public void run() {
            update();
          }
        } );
      }
    }
  }
}
