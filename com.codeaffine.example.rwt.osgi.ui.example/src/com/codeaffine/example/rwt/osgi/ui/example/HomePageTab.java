/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package com.codeaffine.example.rwt.osgi.ui.example;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.framework.ServiceReference;

import com.codeaffine.example.rwt.osgi.configurationadmin.console.OSGiConsole;
import com.codeaffine.example.rwt.osgi.ui.platform.ConfiguratorTracker;
import com.codeaffine.example.rwt.osgi.ui.platform.ServiceProvider;
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributorTracker;


public class HomePageTab implements UIContributor {
  private static final int UPLOAD_HEIGHT = 33;
  
  ServiceProvider serviceProvider;
  UIContributor bundleUploadContributor;
  Composite composite;
  
  public HomePageTab() {
    new UIContributorTracker() {
      
      @Override
      public UIContributor addingService( ServiceReference<UIContributor> reference ) {
        UIContributor result = super.addingService( reference );
        if( ConfiguratorTracker.matchesType( "BundleUpload", reference ) ) {
          bundleUploadContributor = result;
          updatePage();
        }
        return result;
      }
      
      @Override
      public void removedService( ServiceReference<UIContributor> reference,
                                  UIContributor service ) {
        if( ConfiguratorTracker.matchesType( "BundleUpload", reference ) ) {
          bundleUploadContributor = null;
          System.out.println( "here we go!" );
          updatePage();
        }        
      }

      private void updatePage() {
        if( composite != null ) {
          Control[] children = composite.getChildren();
          for( Control control : children ) {
            control.dispose();
          }
          createContent( composite );
          composite.layout();
        }
      }
    };
  }

  void setServiceProvider( ServiceProvider serviceProvider ) {
    this.serviceProvider = serviceProvider;
  }
  
  @Override
  public String getId() {
    return "Home";
  }

  @Override
  public Control contribute( Composite parent ) {
    composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new FormLayout() );
    createContent( composite );
    return composite;
  }

  void createContent( Composite parent ) {
    Control upload = createUploadPanel( parent );
    layoutUpload( upload );
    Control osgiConsole = createOSGiConsole( parent );
    layoutOSGiConsole( osgiConsole );
  }
  
  private Control createUploadPanel( Composite parent ) {
    Composite result = new Composite( parent, SWT.NONE );
    result.setLayout( new FillLayout() );
    if( hasBundleUploadContributor() ) {
      bundleUploadContributor.contribute( result );
    }
    return result;
  }

  private void layoutUpload( Control upload ) {
    FormData data = new FormData();
    upload.setLayoutData( data );
    data.top = new FormAttachment( 0, 5 );
    data.left = new FormAttachment( 0, 5 );
    data.right = new FormAttachment( 100, -5 );
    data.bottom = new FormAttachment( 0, getUploadHeight() );
  }

  private Control createOSGiConsole( Composite parent ) {
    final OSGiConsole osgiConsole = new OSGiConsole();
    osgiConsole.create( parent );
    Control result = osgiConsole.getControl();
    result.addDisposeListener( new DisposeListener() {
      private static final long serialVersionUID = 1L;

      @Override
      public void widgetDisposed( DisposeEvent event ) {
        osgiConsole.dispose();
      }
    } );
    result.setFocus();
    return osgiConsole.getControl();
  }
  
  private void layoutOSGiConsole( Control osgiConsole ) {
    FormData data = new FormData();
    osgiConsole.setLayoutData( data );
    data.top = new FormAttachment( 0, getUploadHeight() + 1 );
    data.left = new FormAttachment( 0, 5 );
    data.right = new FormAttachment( 100, -5 );
    data.bottom = new FormAttachment( 100, -5 );
  }
  
  private int getUploadHeight() {
    return hasBundleUploadContributor() ? UPLOAD_HEIGHT : 0;
  }

  private boolean hasBundleUploadContributor() {
    return bundleUploadContributor != null;
  }
}
