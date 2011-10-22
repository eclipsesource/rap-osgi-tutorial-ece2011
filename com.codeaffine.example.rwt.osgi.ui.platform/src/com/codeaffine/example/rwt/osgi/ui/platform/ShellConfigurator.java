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

import com.codeaffine.example.rwt.osgi.ui.platform.internal.LayoutContextImpl;
import com.codeaffine.example.rwt.osgi.ui.platform.internal.PageServiceImpl;
import com.codeaffine.example.rwt.osgi.ui.platform.internal.ShellProvider;


public class ShellConfigurator {
  private static final String UI_CONTRIBUTOR_TYPE_PAGE = "page";
  
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
    return shellProvider.createShell();
  }

  private void trackUIContributions( final PageServiceImpl pageService ) {
    new UIContributorTracker() {
      
      private boolean initialized;
      
      @Override
      public void addingService( ServiceReference<UIContributorFactory> reference,
                                 UIContributor contributor )
      {
        if( ConfiguratorTracker.matchesType( UI_CONTRIBUTOR_TYPE_PAGE, reference ) ) {
          pageService.addPageContributor( contributor );
          if( !initialized ) {
            pageService.selectHomePage();
            initialized = true;
          }
        }
      }
      
      @Override
      public void removedService( ServiceReference<UIContributorFactory> reference,
                                  UIContributor service )
      {
        if( ConfiguratorTracker.matchesType( UI_CONTRIBUTOR_TYPE_PAGE, reference ) ) {
          pageService.removePageContibutor( service );
          pageService.selectHomePage();
        }
      }
    };
  }
}