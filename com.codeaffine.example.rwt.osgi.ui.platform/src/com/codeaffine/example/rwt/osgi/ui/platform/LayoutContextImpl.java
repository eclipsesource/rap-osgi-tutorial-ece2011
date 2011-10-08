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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.example.rwt.osgi.ui.platform.LayoutProvider.LayoutContext;


class LayoutContextImpl implements LayoutContext {
  private final Map<String, Control> controls;
  private Shell shell;

  LayoutContextImpl() {
    this.controls = new HashMap<String,Control>();
  }
  
  @Override
  public Control getControl( String key ) {
    return controls.get( key );
  }

  void setControl( String key, Control control ) {
    controls.put( key, control );
    shell = control.getShell();
  }

  void layoutShell() {
    if( shell != null ) {
      shell.layout( true, true );
    }
  }
}