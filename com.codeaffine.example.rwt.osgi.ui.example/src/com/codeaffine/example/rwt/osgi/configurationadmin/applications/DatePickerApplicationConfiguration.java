/*******************************************************************************
 * Copyright (c) 2011 Frank Appel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.codeaffine.example.rwt.osgi.configurationadmin.applications;

import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.ApplicationConfiguration;


public class DatePickerApplicationConfiguration implements ApplicationConfiguration {

  @Override
  public void configure( Application configuration ) {
    configuration.addEntryPoint( "/datepicker", DatePicker.class, null );
  }
}
