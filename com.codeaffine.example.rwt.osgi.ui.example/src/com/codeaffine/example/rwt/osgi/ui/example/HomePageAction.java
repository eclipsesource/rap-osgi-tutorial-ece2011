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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;


class HomePageAction {

  void execute() {
    MessageBox messageBox = new MessageBox( Display.getCurrent().getActiveShell() );
    messageBox.setMessage( "Huhu" );
    messageBox.open();
  }
}
