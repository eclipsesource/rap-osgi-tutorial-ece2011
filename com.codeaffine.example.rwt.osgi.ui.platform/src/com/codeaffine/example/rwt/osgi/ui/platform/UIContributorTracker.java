package com.codeaffine.example.rwt.osgi.ui.platform;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.rwt.service.SessionStoreEvent;
import org.eclipse.rwt.service.SessionStoreListener;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


public class UIContributorTracker {

  final Display display;
  final ServiceTrackerForUIThreadUsage tracker;

  private class ServiceTrackerForUIThreadUsage
    extends ServiceTracker<UIContributor, UIContributor>
  {

    ServiceTrackerForUIThreadUsage() {
      super( getBundleContext(), UIContributor.class, null );
    }

    @Override
    public UIContributor addingService( final ServiceReference<UIContributor> reference ) {
      final UIContributor[] result = new UIContributor[ 1 ];
      display.asyncExec( new Runnable() {

        @Override
        public void run() {
          if( ConfiguratorTracker.matches( reference ) ) {
            result[ 0 ] = UIContributorTracker.this.addingService( reference );
          }
        }
      } );
      return result[ 0 ];
    }

    @Override
    public void modifiedService( final ServiceReference<UIContributor> reference, 
                                 final UIContributor service )
    {
      display.asyncExec( new Runnable() {

        @Override
        public void run() {
          if( ConfiguratorTracker.matches( reference ) ) {
            UIContributorTracker.this.modifiedService( reference, service );
          }
        }
      } );      
    }

    @Override
    public void removedService( final ServiceReference<UIContributor> reference, 
                                final UIContributor service )
    {
      display.asyncExec( new Runnable() {

        @Override
        public void run() {
          if( ConfiguratorTracker.matches( reference ) ) {
            UIContributorTracker.this.removedService( reference, service );
          }
        }
      } );      
    }
  }
  
  public UIContributorTracker() {
    display = Display.getDefault();
    UICallBack.activate( String.valueOf( display.hashCode() ) );
    tracker = new ServiceTrackerForUIThreadUsage();
    tracker.open();
    closeOnSessionTimeout();
  }

  public void removedService( ServiceReference<UIContributor> reference, UIContributor service ) {
    // subclasses may override
  }

  public void modifiedService( ServiceReference<UIContributor> reference, UIContributor service ) {
    // subclasses may override
  }

  public UIContributor addingService( ServiceReference<UIContributor> reference ) {
    return getBundleContext().getService( reference );
  }

  private boolean closeOnSessionTimeout() {
    return RWT.getSessionStore().addSessionStoreListener( new SessionStoreListener() {
      private static final long serialVersionUID = 1L;

      @Override
      public void beforeDestroy( SessionStoreEvent event ) {
        tracker.close();
        UICallBack.deactivate( String.valueOf( display.hashCode() ) );
      }
    } );
  }
    
  static BundleContext getBundleContext() {
    return FrameworkUtil.getBundle( UIContributorTracker.class ).getBundleContext();
  }
}