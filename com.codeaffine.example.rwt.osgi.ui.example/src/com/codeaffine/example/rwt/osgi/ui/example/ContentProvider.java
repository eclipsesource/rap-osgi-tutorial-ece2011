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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.codeaffine.example.rwt.osgi.ui.platform.PageService;
import com.codeaffine.example.rwt.osgi.ui.platform.ServiceProvider;
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;

public class ContentProvider implements UIContributor {
  public static final String CONTENT_CONTROL = ContentProvider.class.getName() + "#CONTENT";
  
  private final ServiceProvider serviceProvider;

  public ContentProvider( ServiceProvider serviceProvider ) {
    this.serviceProvider = serviceProvider;
  }
  
  @Override
  public String getId() {
    return CONTENT_CONTROL;
  }
  
  @Override
  public Control contribute( Composite parent ) {
    Composite result = new Composite( parent, SWT.INHERIT_DEFAULT );
    result.setLayout( new FillLayout() );
    PageService pageService = serviceProvider.get( PageService.class );
    pageService.registerContentParent( result );
    return result;
  }
}