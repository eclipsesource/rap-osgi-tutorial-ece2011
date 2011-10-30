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
package com.codeaffine.example.rwt.osgi.ui.example.slides;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.codeaffine.example.rwt.osgi.ui.platform.PageService;
import com.codeaffine.example.rwt.osgi.ui.platform.PageTracker;
import com.codeaffine.example.rwt.osgi.ui.platform.ServiceProvider;
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;

public class MenuBar implements UIContributor {
  private static final String PAGE_ID_COOKIE = "pageId";
  public static final String MENU_BAR_CONTROL = MenuBar.class.getName() + "#MENUBAR";
  // NOTE: this value reflects the height of the menubar_background image set via css
  static final int MENU_BAR_HEIGHT = 41;
  private static final String MENUBAR_BACKGROUND = "menubar_background";
  private static final String MENU_BUTTON = "menu_button";

  private final ServiceProvider serviceProvider;
  final Map<String,Button> buttons;

  public MenuBar( ServiceProvider serviceProvider ) {
    this.serviceProvider = serviceProvider;
    this.buttons = new HashMap<String,Button>();
  }

  @Override
  public String getId() {
    return MENU_BAR_CONTROL;
  }

  @Override
  public Control contribute( Composite parent ) {
    final Composite result = new Composite( parent, SWT.INHERIT_DEFAULT );
    result.setData( WidgetUtil.CUSTOM_VARIANT, MENUBAR_BACKGROUND );
    RowLayout layout = new RowLayout();
    layout.marginTop = 8;
    layout.marginLeft = 30;
    result.setLayout( layout );
    
    final PageService pageService = serviceProvider.get( PageService.class );
    pageService.addPageTracker( new PageTracker() {

      @Override
      public void pageAdded( UIContributor page ) {
        buttons.put( page.getId(), createMenuButton( result, pageService, page.getId() ) );
      }

      @Override
      public void pageRemoved( UIContributor page ) {
        Button removed = buttons.remove( page.getId() );
        removed.dispose();
        selectMenuBarButton();
      }
    } );
    
    return result;
  }

  Button createMenuButton( Composite parent,
                           final PageService pageService,
                           final String pageId )
  {
    unselectButtons();
    final Button result = new Button( parent, SWT.TOGGLE );
    RowData data = new RowData();
    data.height = 40;
    result.setLayoutData(  data );
    result.setData( WidgetUtil.CUSTOM_VARIANT, MENU_BUTTON );
    result.setText( pageId );
    result.addSelectionListener( new SelectionAdapter() {
      private static final long serialVersionUID = 1L;

      @Override
      public void widgetSelected( SelectionEvent evt ) {
        unselectButtons();
        pageService.selectPage( pageId );
        result.setSelection( true );
        updatePageToSelect( pageId );
      }
    } );
    
    if( getPageToSelect().equals( pageId ) ) {
      pageService.selectPage( pageId );
      result.setSelection( true );
      updatePageToSelect( pageId );
    }
    selectMenuBarButton();
    parent.layout( true, true );
    return result;
  }

  void selectMenuBarButton() {
    Button toSelect = buttons.get( getPageToSelect() );
    if( toSelect != null ) {
      unselectButtons();
      toSelect.setSelection( true );
    }
  }

  void updatePageToSelect( String pageId ) {
    CookieUtil.setCookie( PAGE_ID_COOKIE, pageId );
  }

  String getPageToSelect() {
    Cookie cookie = CookieUtil.getCookie( PAGE_ID_COOKIE );
    String result = SlidesUIContributor.ID; 
    if( cookie != null ) {
      result = cookie.getValue();
    }
    return result;
  }

  void unselectButtons() {
    Iterator<Button> iterator = buttons.values().iterator();
    while( iterator.hasNext() ) {
      iterator.next().setSelection( false );
    }
  }
}