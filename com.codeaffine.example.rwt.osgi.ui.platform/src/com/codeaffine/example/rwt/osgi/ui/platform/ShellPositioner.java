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

import org.eclipse.rwt.lifecycle.PhaseEvent;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.example.rwt.osgi.ui.platform.internal.ShellProvider;


public class ShellPositioner implements PhaseListener {
  private static final long serialVersionUID = 1L;

  static final int APPLICATION_SHELL_WIDTH = 900;

  private final boolean useMaximumHeight;


  public ShellPositioner( boolean useMaximumHeight ) {
    this.useMaximumHeight = useMaximumHeight;
  }

  public ShellPositioner() {
    this( true );
  }

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
    Rectangle displayBounds = display.getBounds();
    Rectangle oldBounds = applicationShell.getBounds();
    Rectangle newBounds = computeShellBounds( applicationShell, displayBounds );
    if( !newBounds.equals( oldBounds ) ) {
      applicationShell.setBounds( newBounds );
      applicationShell.layout( true, true );
    }
  }

  Rectangle computeShellBounds( Shell applicationShell, Rectangle displayBounds ) {
    int heightHint = useMaximumHeight ? displayBounds.height : SWT.DEFAULT;
    Point size = applicationShell.computeSize( SWT.DEFAULT, heightHint );
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
