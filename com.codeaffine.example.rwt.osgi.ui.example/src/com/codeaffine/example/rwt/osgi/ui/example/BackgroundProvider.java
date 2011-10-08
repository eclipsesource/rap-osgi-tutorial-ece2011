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

import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;

class BackgroundProvider implements UIContributor {
  public static final String BACKGROUND_CONTROL
    = BackgroundProvider.class.getName() + "#Background";

  @Override
  public String getId() {
    return BACKGROUND_CONTROL;
  }

  @Override
  public Control contribute( Composite parent ) {
    Label result = new Label( parent, SWT.NONE );
    result.setData( WidgetUtil.CUSTOM_VARIANT, "content-background" );
    result.moveBelow( null );
    return result;
  }
}