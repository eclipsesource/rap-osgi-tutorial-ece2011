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

import org.eclipse.rwt.branding.AbstractBranding;

class UIBranding extends AbstractBranding {

  @Override
  public String getServletName() {
    return App1.EXAMPLE_UI;
  }

  @Override
  public String getThemeId() {
    return App1.EXAMPLE_UI;
  }
  
  @Override
  public String getTitle() {
    return "Dynamic Duo";
  }
}