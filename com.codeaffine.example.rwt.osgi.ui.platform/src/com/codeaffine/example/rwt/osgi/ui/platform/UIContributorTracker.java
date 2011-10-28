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

import com.codeaffine.example.rwt.osgi.ui.platform.internal.UIContributorTrackerService;
import com.codeaffine.example.rwt.osgi.ui.platform.internal.UIContributorTrackerService.Tracker;


public class UIContributorTracker {

  final Display display;
  final UIContributorTrackerService trackerService;
  final TrackerImpl tracker;

  class TrackerImpl implements Tracker {

    @Override
    public void removedService( final ServiceReference<UIContributorFactory> reference,
                                final UIContributor service )
    {
      if( !display.isDisposed() ) {
        display.asyncExec( new Runnable() {
  
          @Override
          public void run() {
            if( canRun( reference ) ) {
              UIContributorTracker.this.removedService( reference, service );
            }
          }
        } );
      }
    }

    @Override
    public void addingService( final ServiceReference<UIContributorFactory> reference,
                               final UIContributor service )
    {
      if( !display.isDisposed() ) {
        display.asyncExec( new Runnable() {
          
          @Override
          public void run() {
            if( canRun( reference ) ) {
              UIContributorTracker.this.addingService( reference, service );
            }
          }
        } );
      }
    }

    boolean canRun( final ServiceReference<UIContributorFactory> reference ) {
      return display.getThread() == Thread.currentThread()
          && ConfiguratorTracker.matches( reference );
    }
  }

  private class UIContributorTrackerServiceTracker
    extends ServiceTracker<UIContributorTrackerService, UIContributorTrackerService>
  {
    UIContributorTrackerServiceTracker() {
      super( getBundleContext(), UIContributorTrackerService.class, null );
    }
  }
  
  public UIContributorTracker() {
    display = Display.getDefault();
    int hashCode = display.hashCode();
    UICallBack.activate( String.valueOf( hashCode ) );
    trackerService = getTrackerService();
    tracker = new TrackerImpl();
    trackerService.addTracker( tracker );
    closeOnSessionTimeout();
  }

  private UIContributorTrackerService getTrackerService() {
    UIContributorTrackerServiceTracker tracker = new UIContributorTrackerServiceTracker();
    tracker.open();
    UIContributorTrackerService result = tracker.getService();
    tracker.close();
    return result;
  }

  public void removedService( ServiceReference<UIContributorFactory> reference,
                              UIContributor service )
  {
    // subclasses may override
  }

  public void addingService( ServiceReference<UIContributorFactory> reference,
                             UIContributor service )
  {
    // subclasses may override
  }

  private boolean closeOnSessionTimeout() {
    return RWT.getSessionStore().addSessionStoreListener( new SessionStoreListener() {
      private static final long serialVersionUID = 1L;

      @Override
      public void beforeDestroy( SessionStoreEvent event ) {
        trackerService.removeTracker( tracker );
        UICallBack.deactivate( String.valueOf( display.hashCode() ) );
      }
    } );
  }
    
  static BundleContext getBundleContext() {
    return FrameworkUtil.getBundle( UIContributorTracker.class ).getBundleContext();
  }
}