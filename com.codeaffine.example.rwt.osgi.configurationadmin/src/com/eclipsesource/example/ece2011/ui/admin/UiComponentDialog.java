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

import com.codeaffine.example.rwt.osgi.configurationadmin.DeploymentHelper;


@SuppressWarnings( "serial" )
public class UiComponentDialog {

  private final Shell parent;
  private final Shell shell;
  private final UiComponent component;
  private Combo portCombo;
  private Combo applicationCombo;
  private Images images;
  private boolean isApplication;
  private boolean isUndeploy;

  public UiComponentDialog( Shell parent, UiComponent component ) {
    this.parent = parent;
    this.component = component;
    isApplication = component.isApplication();
    isUndeploy = component.getDeployedOnPort() != null;
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
    if( !isUndeploy ) {
      createPortCombo( shell );
      if( !isApplication ) {
        createParentApplicationCombo( shell );
      }
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
    String text = isUndeploy ? "Undeploy " : "Deploy ";
    text += isApplication ? "Application" : "Contribution";
    textLabel.setText( text );
    iconLabel.setImage( isApplication ? images.applicationImage : images.contributionImage );
  }

  private void createLabels( Composite parent ) {
    new Label( parent, SWT.NONE ).setText( "Name:" );
    Label nameLabel = new Label( parent, SWT.WRAP );
    nameLabel.setText( component.getName() );
    new Label( parent, SWT.NONE ).setText( "Bundle:" );
    Label bundleLabel = new Label( parent, SWT.WRAP );
    bundleLabel.setText( component.getBundleName() );
    if( isUndeploy ) {
      new Label( parent, SWT.NONE ).setText( "Port:" );
      Label portLabel = new Label( parent, SWT.WRAP );
      portLabel.setText( component.getDeployedOnPort() );
    }
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
    label.setText( "Application: " );
    applicationCombo = new Combo( parent, SWT.CHECK | SWT.READ_ONLY );
    applicationCombo.select( 0 );
    applicationCombo.setLayoutData( new GridData( 200, SWT.DEFAULT ) );
  }

  private Control createButtonBar( Composite parent ) {
    Composite buttonBar = new Composite( parent, SWT.NONE );
    buttonBar.setLayout( createButtonBarLayout() );
    if( isUndeploy ) {
      createUndeployButton( buttonBar );
    } else {
      createDeployButton( buttonBar );
    }
    createCancelButton( buttonBar );
    return buttonBar;
  }

  private void createCancelButton( Composite buttonBar ) {
    Button button = new Button( buttonBar, SWT.PUSH );
    button.setText( "Cancel" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        shell.close();
      }
    } );
  }

  private void createDeployButton( Composite buttonBar ) {
    Button button = new Button( buttonBar, SWT.PUSH );
    button.setText( "Deploy" );
    shell.setDefaultButton( button );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        String port = portCombo.getText();
        DeploymentHelper deploymentHelper = new DeploymentHelper();
        if( isApplication ) {
          deploymentHelper.deployApplication( component.getName(), port, null );
        } else {
          String application = applicationCombo.getText();
          deploymentHelper.deployUIContribution( component.getName(), application, port, null );
        }
        shell.close();
      }
    } );
  }

  private void createUndeployButton( Composite buttonBar ) {
    Button deployButton = new Button( buttonBar, SWT.PUSH );
    deployButton.setText( "Undeploy" );
    shell.setDefaultButton( deployButton );
    deployButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        DeploymentHelper deploymentHelper = new DeploymentHelper();
        if( isApplication ) {
          deploymentHelper.undeployApplication( component.getName(),
                                                component.getDeployedOnPort(),
                                                null );
        } else {
          deploymentHelper.undeployUIContribution( component.getName(),
                                                   component.getApplication(),
                                                   component.getDeployedOnPort(),
                                                   null );
        }
        shell.close();
      }
    } );
  }

  private RowLayout createButtonBarLayout() {
    RowLayout layout = new RowLayout( SWT.HORIZONTAL );
    layout.marginTop = 20;
    layout.marginRight = 0;
    layout.marginBottom = 0;
    layout.spacing = 6;
    return layout;
  }

  private void updatePortCombo() {
    if( portCombo != null ) {
      List<String> ports = UiComponents.getAvailablePorts();
      String oldText = portCombo.getText();
      portCombo.setItems( ports.toArray( new String[ ports.size() ] ) );
      portCombo.setText( oldText );
      if( portCombo.getSelectionIndex() < 0 && portCombo.getItemCount() > 0 ) {
        portCombo.select( 0 );
      }
    }
  }

  private void updateParentCombo() {
    if( applicationCombo != null ) {
      String port = portCombo.getText();
      List<UiComponent> activeComponents = UiComponents.getActiveComponents( port );
      List<String> items = new ArrayList<String>();
      for( UiComponent component : activeComponents ) {
        items.add( component.getName() );
      }
      String oldText = applicationCombo.getText();
      applicationCombo.setItems( items.toArray( new String[ items.size() ] ) );
      applicationCombo.setText( oldText );
      if( applicationCombo.getSelectionIndex() < 0 && applicationCombo.getItemCount() > 0 ) {
        applicationCombo.select( 0 );
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
