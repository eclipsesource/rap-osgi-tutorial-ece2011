package com.eclipsesource.example.ece2011.ui.admin;

import org.eclipse.rwt.application.ApplicationConfigurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

import com.codeaffine.example.rwt.osgi.configurationadmin.DeploymentHelper;
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributorFactory;


public class ChangeTracker {

  private UpdateServiceTracker httpServiceTracker;
  private UpdateServiceTracker appConfigServiceTracker;
  private UpdateServiceTracker uiContribServiceTracker;

  public ChangeTracker() {
    BundleContext bundleContext = DeploymentHelper.getBundleContext();
    httpServiceTracker = new UpdateServiceTracker( bundleContext, HttpService.class.getName() );
    appConfigServiceTracker = new UpdateServiceTracker( bundleContext, ApplicationConfigurator.class.getName() );
    uiContribServiceTracker = new UpdateServiceTracker( bundleContext, UIContributorFactory.class.getName() );
  }

  public void start() {
    httpServiceTracker.open();
    appConfigServiceTracker.open();
    uiContribServiceTracker.open();
  }

  public void stop() {
    httpServiceTracker.close();
    appConfigServiceTracker.close();
    uiContribServiceTracker.close();
    httpServiceTracker = null;
    appConfigServiceTracker = null;
    uiContribServiceTracker = null;
  }

  protected void update() {
  }

  private final class UpdateServiceTracker extends ServiceTracker<Object, Object> {

    private UpdateServiceTracker( BundleContext context, String className ) {
      super( context, className, null );
    }

    @Override
    public Object addingService( ServiceReference<Object> reference ) {
      Object result = super.addingService( reference );
      update();
      return result;
    }
    
    @Override
    public void removedService( ServiceReference<Object> reference, Object service ) {
      super.removedService( reference, service );
      update();
    }
  }

}
