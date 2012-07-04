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
package com.codeaffine.example.rwt.osgi.ui.example.slides;

import org.eclipse.rwt.branding.AbstractBranding;

class Branding extends AbstractBranding {

  @Override
  public String getServletName() {
    return PresentationConfigurator.SLIDES;
  }

  @Override
  public String getThemeId() {
    return PresentationConfigurator.SLIDES;
  }

  @Override
  public String getTitle() {
    return "Slides";
  }
}
