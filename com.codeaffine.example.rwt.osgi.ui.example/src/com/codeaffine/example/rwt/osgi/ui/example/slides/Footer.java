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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;

public class Footer implements UIContributor {
  public static final String FOOTER_CONTROL = Footer.class.getName() + "#FOOTER";
  // NOTE: this value reflects the height of the footer_separator background image set via css
  private static final int SEPARATOR_HEIGHT = 8;
  static final int FOOTER_HEIGHT = SEPARATOR_HEIGHT;


  @Override
  public String getId() {
    return FOOTER_CONTROL;
  }

  @Override
  public Control contribute( Composite parent ) {
    Composite result = new Composite( parent, SWT.INHERIT_DEFAULT );
    result.setLayout( new FormLayout() );

    Label separator = new Label( result, SWT.NONE );
    separator.setData( WidgetUtil.CUSTOM_VARIANT, "footer_separator" );
    FormData separatorData = new FormData();
    separator.setLayoutData( separatorData );
    separatorData.top = new FormAttachment( 0, 0 );
    separatorData.left = new FormAttachment( 0, 0 );
    separatorData.right = new FormAttachment( 100, 0 );
    separatorData.height = SEPARATOR_HEIGHT;

    Label versionInfo = new Label( result, SWT.NONE );
    versionInfo.setText( getVersionInfo() );
    versionInfo.pack();
    FormData versionInfoData = new FormData();
    versionInfo.setLayoutData( versionInfoData );
    Point size = versionInfo.getSize();
    versionInfoData.top = new FormAttachment( 50, -( size.y / 2 ) + ( SEPARATOR_HEIGHT / 2 ) );
    versionInfoData.left = new FormAttachment( 50, -( size.x / 2 ) );
    return result;
  }

  private String getVersionInfo() {
    Bundle bundle = FrameworkUtil.getBundle( getClass() ).getBundleContext().getBundle();
    String name = bundle.getHeaders().get( "Bundle-Name" );
    Version version = bundle.getVersion();
    return name + " (" + version + ")";
  }
}
