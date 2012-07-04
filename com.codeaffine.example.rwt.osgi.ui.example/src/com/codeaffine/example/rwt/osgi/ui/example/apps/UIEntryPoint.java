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
package com.codeaffine.example.rwt.osgi.ui.example.apps;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.codeaffine.example.rwt.osgi.ui.platform.LayoutProvider;
import com.codeaffine.example.rwt.osgi.ui.platform.ServiceProvider;
import com.codeaffine.example.rwt.osgi.ui.platform.ShellConfigurator;
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;


public class UIEntryPoint implements IEntryPoint {
  private ServiceProvider serviceProvider;
  private Shell shell;

  @Override
  public int createUI() {
    initializeServiceProvider();
    configureShell();
    openShell();
    return 0;
  }

  private void configureShell() {
    UIContributor[] pageStructureProviders = new UIContributor[] {
      new HeaderProvider(),
      new MenuBarProvider( serviceProvider ),
      new ContentProvider( serviceProvider ),
      new FooterProvider(),
      new BackgroundProvider()
    };
    LayoutProvider layoutProvider = new LayoutProviderImpl();
    ShellConfigurator configurator = new ShellConfigurator( serviceProvider );
    shell = configurator.configure( pageStructureProviders, layoutProvider );
  }

  private void initializeServiceProvider() {
    getServiceProvider();
    serviceProvider.register( HomePageAction.class, new HomePageAction() );
  }

  private void getServiceProvider() {
    Class<ServiceProvider> type = ServiceProvider.class;
    BundleContext context = FrameworkUtil.getBundle( getClass() ).getBundleContext();
    ServiceReference<ServiceProvider> serviceReference = context.getServiceReference( type );
    serviceProvider = ( ServiceProvider )context.getService( serviceReference );
  }

  private void openShell() {
    shell.open();
  }
}
