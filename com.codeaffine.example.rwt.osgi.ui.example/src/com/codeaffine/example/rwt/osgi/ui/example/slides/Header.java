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

import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;

public class Header implements UIContributor {
  public static final String HEADER_CONTROL = Header.class.getName() + "#HEADER";
  static final int HEADER_HEIGHT = 0;
  
  @Override
  public String getId() {
    return HEADER_CONTROL;
  }
  
  @Override
  public Control contribute( Composite parent ) {
    Composite result = new Composite( parent, SWT.INHERIT_DEFAULT );
    result.setData( WidgetUtil.CUSTOM_VARIANT, "header" );
    
    result.setLayout( new FormLayout() );
    
    Label logo = new Label( result, SWT.NONE );
    logo.setData( WidgetUtil.CUSTOM_VARIANT, "logo" );
    FormData logoData = new FormData();
    logo.setLayoutData( logoData );
    logoData.top = new FormAttachment( 0, 8 );
    logoData.left = new FormAttachment( 0, 10 );
    logoData.width = 199;
    logoData.height = 61;
    
    Label appname = new Label( result, SWT.NONE );
    appname.setData( WidgetUtil.CUSTOM_VARIANT, "appname" );
    FormData appnameData = new FormData();
    appname.setLayoutData( appnameData );
    appnameData.top = new FormAttachment( 0, 10 );
    appnameData.left = new FormAttachment( 100, -176 );
    appnameData.width = 170;
    appnameData.height = 42;
    return result;
  }
}