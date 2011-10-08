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
package com.codeaffine.example.rwt.osgi.ui.platform;

import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.ServiceReference;


public class ShellConfigurator {
  private final ServiceProvider serviceProvider;

  public ShellConfigurator( ServiceProvider serviceProvider ) {
    this.serviceProvider = serviceProvider;
  }
  
  public Shell configure( UIContributor[] pageStructureProviders, LayoutProvider layoutProvider ) {
    LayoutContextImpl layoutContext = new LayoutContextImpl();
    PageServiceImpl pageService = new PageServiceImpl( layoutProvider, layoutContext );
    serviceProvider.register( PageService.class, pageService );
    trackUIContributions( pageService );

    ShellProvider shellProvider
      = new ShellProvider( pageStructureProviders, layoutProvider, layoutContext );
    Shell result = shellProvider.createShell();
    return result;
  }

  private void trackUIContributions( final PageServiceImpl pageService ) {
    new UIContributorTracker() {
      
      private boolean initialized;
      
      @Override
      public UIContributor addingService( ServiceReference<UIContributor> reference ) {
        UIContributor result = super.addingService( reference );
        if( ConfiguratorTracker.matches( reference ) ) {
          pageService.addPageContributor( result );
          if( !initialized ) {
            pageService.selectHomePage();
            initialized = true;
          }
        }
        return result;
      }
      @Override
      public void removedService( ServiceReference<UIContributor> reference, UIContributor service )
      {
        if( ConfiguratorTracker.matches( reference ) ) {
          pageService.removePageContibutor( service );
          pageService.selectHomePage();
        }
      }
    };
  }
}