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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;


public class OtherPageTab implements UIContributor {

  @Override
  public String getId() {
    return "Other Page";
  }

  @Override
  public Control contribute( Composite parent ) {
    Composite result = new Composite( parent, SWT.NONE );
    result.setLayout( new RowLayout( SWT.VERTICAL ) );
    for( int i = 0; i < 40; i++ ) {
      Label label = new Label( result, SWT.NONE );
      label.setText( "This is row " + i +" of the other page content." );
    }
    return result;
  }
}
