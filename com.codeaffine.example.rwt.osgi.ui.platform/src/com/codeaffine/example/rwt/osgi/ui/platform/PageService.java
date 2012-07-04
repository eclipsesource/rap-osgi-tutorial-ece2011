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

import org.eclipse.swt.widgets.Composite;

public interface PageService {
  void registerContentParent( Composite contentParent );
  void addPageTracker( PageTracker pageTracker );
  void removePageTracker( PageTracker pageTracker );

  String[] getPageIds();
  void selectPage( String pageId );
  void selectHomePage();
}
