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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.codeaffine.example.rwt.osgi.ui.platform.PageService;
import com.codeaffine.example.rwt.osgi.ui.platform.PageTracker;
import com.codeaffine.example.rwt.osgi.ui.platform.ServiceProvider;
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;

class MenuBarProvider implements UIContributor {
  public static final String MENU_BAR_CONTROL = MenuBarProvider.class.getName() + "#MENUBAR";
  // NOTE: this value reflects the height of the menubar_background image set via css
  static final int MENU_BAR_HEIGHT = 41;
  private static final String MENUBAR_BACKGROUND = "menubar_background";
  private static final String MENU_BUTTON = "menu_button";

  private final ServiceProvider serviceProvider;

  MenuBarProvider( ServiceProvider serviceProvider ) {
    this.serviceProvider = serviceProvider;
  }

  @Override
  public String getId() {
    return MENU_BAR_CONTROL;
  }

  @Override
  public Control contribute( Composite parent ) {
    final Composite result = new Composite( parent, SWT.INHERIT_DEFAULT );
    result.setData( WidgetUtil.CUSTOM_VARIANT, MENUBAR_BACKGROUND );
    result.setLayout( new RowLayout() );
    
    final PageService pageService = serviceProvider.get( PageService.class );
    pageService.addPageTracker( new PageTracker() {
      Map<UIContributor,Button> buttons = new HashMap<UIContributor,Button>();

      @Override
      public void pageAdded( UIContributor page ) {
        buttons.put( page, createMenuButton( result, pageService, page.getId() ) );
      }

      @Override
      public void pageRemoved( UIContributor page ) {
        Button removed = buttons.remove( page );
        removed.dispose();
      }
    } );
    
    return result;
  }

  Button createMenuButton( Composite parent,
                           final PageService pageService,
                           final String pageId )
  {
    Button result = new Button( parent, SWT.PUSH );
    result.setData( WidgetUtil.CUSTOM_VARIANT, MENU_BUTTON );
    result.setText( pageId );
    result.addSelectionListener( new SelectionAdapter() {
      private static final long serialVersionUID = 1L;

      @Override
      public void widgetSelected( SelectionEvent evt ) {
        pageService.selectPage( pageId );
      }
    } );
    parent.layout( true, true );
    pageService.selectPage( pageId );
    return result;
  }
}