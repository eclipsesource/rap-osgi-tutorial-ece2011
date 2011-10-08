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

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ShellPositioner implements PhaseListener {
  private static final long serialVersionUID = 1L;

  static final int APPLICATION_SHELL_WIDTH = 900;

  @Override
  public void beforePhase( PhaseEvent event ) {
    computeShellBounds( Display.getCurrent() );
  }

  @Override
  public void afterPhase( PhaseEvent event ) {
    // do nothing
  }

  @Override
  public PhaseId getPhaseId() {
    return PhaseId.RENDER;
  }
  
  void computeShellBounds( Display display ) {
    Shell applicationShell = findApplicationShell( display );
    Rectangle bounds = display.getBounds();
    Rectangle newBounds = computeShellBounds( applicationShell, bounds );
    applicationShell.setBounds( newBounds );
  }

  Rectangle computeShellBounds( Shell applicationShell, Rectangle displayBounds ) {
    Point size = applicationShell.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    return new Rectangle( calculateXPos( displayBounds ), 0, APPLICATION_SHELL_WIDTH, size.y );
  }

  private int calculateXPos( Rectangle displayBounds ) {
    return Math.max( 0, ( displayBounds.width - APPLICATION_SHELL_WIDTH ) ) / 2;
  }

  public Shell findApplicationShell( Display display ) {
    Shell[] shells = display.getShells();
    Shell result = null;
    for( Shell shell : shells ) {
      if( ShellProvider.isApplicationShell( shell ) ) {
        result = shell;
      }
    }
    return result;
  }
}