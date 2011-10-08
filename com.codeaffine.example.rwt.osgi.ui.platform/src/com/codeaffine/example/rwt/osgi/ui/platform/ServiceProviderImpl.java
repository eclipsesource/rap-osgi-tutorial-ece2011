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


public class ServiceProviderImpl implements ServiceProvider {
  private Map<Class<?>, Object> services;

  public ServiceProviderImpl() {
    services = new HashMap<Class<?>, Object>();
  }
  
  @Override
  public <T> T register( Class<T> service, T instance ) {
    services.put( service, instance );
    return instance;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public <T> T get( Class<T> service ) {
    return ( T )services.get( service );
  }
}