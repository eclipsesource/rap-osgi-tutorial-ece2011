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

import org.eclipse.rwt.widgets.DialogCallback;
import org.eclipse.rwt.widgets.DialogUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;


public class HomePageAction {

  void execute() {
    MessageBox messageBox = new MessageBox( Display.getCurrent().getActiveShell() );
    messageBox.setMessage( "Huhu" );
    DialogUtil.open( messageBox, new DialogCallback() {
      private static final long serialVersionUID = 1L;

      @Override
      public void dialogClosed( int returnCode ) {
        // TODO [fappel]: remove this once null is allowed as dialog callback parameter
      }
    } );
  }
}
