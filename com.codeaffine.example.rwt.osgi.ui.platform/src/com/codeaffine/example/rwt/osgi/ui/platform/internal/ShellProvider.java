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
package com.codeaffine.example.rwt.osgi.ui.platform.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.example.rwt.osgi.ui.platform.LayoutProvider;
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;

public class ShellProvider {
  public static final String APPLICATION_SHELL
    = ShellProvider.class.getName() + "#APPLICATION_SHELL";

  private final UIContributor[] uiProviders;
  private final LayoutProvider layoutProvider;
  private final LayoutContextImpl layoutContext;

  public ShellProvider( UIContributor[] uiProviders,
                        LayoutProvider layoutProvider,
                        LayoutContextImpl ctx )
  {
    this.uiProviders = uiProviders;
    this.layoutProvider = layoutProvider;
    this.layoutContext = ctx;
  }

  public Shell createShell() {
    Shell result = createApplicationShell();
    createContent( result );
    layoutShell();
    return result;
  }

  private void layoutShell() {
    layoutProvider.layout( layoutContext );
    layoutContext.layoutShell();
  }

  private void createContent( Shell result ) {
    for( UIContributor uiProvider : uiProviders ) {
      layoutContext.setControl( uiProvider.getId(), uiProvider.contribute( result ) );
    }
  }

  private Shell createApplicationShell() {
    Shell result = new Shell( Display.getDefault(), SWT.INHERIT_DEFAULT );
    result.setData( APPLICATION_SHELL, APPLICATION_SHELL );
    result.setLayout( new FormLayout() );
    return result;
  }

  public static boolean isApplicationShell( Shell shell ) {
    return shell.getData( APPLICATION_SHELL ) != null;
  }
}
