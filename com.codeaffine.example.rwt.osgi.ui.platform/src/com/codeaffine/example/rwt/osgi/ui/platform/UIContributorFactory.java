package com.codeaffine.example.rwt.osgi.ui.platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;


public class UIContributorFactory {
  
  private static final String CLASS = "class";
  
  private Class<UIContributor> type;
  private ServiceProvider serviceProvider;

  public UIContributor create() {
    try {
      UIContributor result = type.newInstance();
      injectServiceProvider( result );
      return result;
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception instantiationProblem ) {
      throw new IllegalStateException( instantiationProblem );
    }
  }

  private void injectServiceProvider( UIContributor result )
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    Method method = getMethod( result.getClass() );
    if( method != null ) {
      method.setAccessible( true );
      method.invoke( result, serviceProvider );
    }
  }

  private Method getMethod( Class<? extends UIContributor> clazz ) {
    Method result = null;
    try {
      result = clazz.getDeclaredMethod( "setServiceProvider", ServiceProvider.class );
    } catch( SecurityException ignore ) {
      // nothing to inject
    } catch( NoSuchMethodException ignore ) {
      // nothing to inject
    }
    return result;
  }
  
  @SuppressWarnings( "unchecked" )
  void activate( ComponentContext context ) throws ClassNotFoundException {
    String className = ( String )context.getProperties().get( CLASS );
    BundleContext bundleContext = context.getBundleContext();
    Bundle bundle = bundleContext.getBundle();
    type = ( Class<UIContributor> )bundle.loadClass( className );
  }
  
  public void setServiceProvider( ServiceProvider serviceProvider ) {
    this.serviceProvider = serviceProvider;
  }
}