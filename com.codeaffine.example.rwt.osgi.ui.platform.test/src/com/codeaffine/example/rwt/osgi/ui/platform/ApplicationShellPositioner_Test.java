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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.example.rwt.osgi.ui.platform.ShellPositioner;
import com.codeaffine.example.rwt.osgi.ui.platform.ShellProvider;



public class ApplicationShellPositioner_Test {
  private static final int PREFERRED_SHELL_WIDTH = 200;
  private static final int PREFERRED_SHELL_HEIGHT = 1000;

  private ShellPositioner positioner;
  private Shell applicationShell;

  @Test
  public void testGetPhaseId() {
    assertSame( PhaseId.RENDER, positioner.getPhaseId() );
  }
  
  @Test
  public void testFindApplicationShell() {
    Display display = mockDisplayWithShells();
    
    Shell found = positioner.findApplicationShell( display );
    
    assertSame( applicationShell, found );
  }
  
  @Test
  public void testComputeShellBoundsWithLargeDisplay() {
    Rectangle displayBounds = createDisplayBounds( 100 );
    
    Rectangle calculated = positioner.computeShellBounds( applicationShell, displayBounds );
    
    checkBoundCalculation( calculated, 50 );
  }

  @Test
  public void testComputeShellBoundsWithSmallDisplay() {
    Rectangle displayBounds = createDisplayBounds( -100 );
    
    Rectangle calculated = positioner.computeShellBounds( applicationShell, displayBounds );

    checkBoundCalculation( calculated, 0 );
  }
  
  @Test
  public void testComputeShellBounds() {
    Display display = mockDisplayWithShells();

    positioner.computeShellBounds( display );

    verify( applicationShell ).setBounds( createExpectedShellBounds() );
  }

  @Before
  public void setUp() {
    mockApplicationShell();
    positioner = new ShellPositioner();
  }

  private void checkBoundCalculation( Rectangle calculated, int calculatedXPos ) {
    assertEquals( calculatedXPos, calculated.x );
    assertEquals( 0, calculated.y );
    assertEquals( ShellPositioner.APPLICATION_SHELL_WIDTH, calculated.width );
    assertEquals( PREFERRED_SHELL_HEIGHT, calculated.height );
  }

  private Rectangle createExpectedShellBounds() {
    int width = ShellPositioner.APPLICATION_SHELL_WIDTH;
    return new Rectangle( 50, 0, width, PREFERRED_SHELL_HEIGHT );
  }

  private Rectangle createDisplayBounds( int widthDelta ) {
    int width = ShellPositioner.APPLICATION_SHELL_WIDTH + widthDelta;
    return new Rectangle( 0, 0, width, 800 );
  }

  private Display mockDisplayWithShells() {
    Shell simpleShell = mock( Shell.class );
    Display result = mock( Display.class );
    when( result.getShells() ).thenReturn( (new Shell[] { applicationShell, simpleShell } ) );
    when( result.getBounds() ).thenReturn( createDisplayBounds( 100 ) );
    return result;
  }

  private void mockApplicationShell() {
    applicationShell = mock( Shell.class );
    String key = ShellProvider.APPLICATION_SHELL;
    when( applicationShell.getData( key ) ).thenReturn( "true" );
    Point preferredSize = new Point( PREFERRED_SHELL_WIDTH, PREFERRED_SHELL_HEIGHT );
    when( applicationShell.computeSize( SWT.DEFAULT, SWT.DEFAULT ) ).thenReturn( preferredSize );
  }
}