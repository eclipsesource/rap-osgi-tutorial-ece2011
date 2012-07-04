package com.codeaffine.example.rwt.osgi.ui.platform.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributorFactory;


public class UIContributorTrackerService {

  final ServiceTracker<UIContributorFactory, UIContributorFactory> serviceTracker;
  final Map<ServiceReference<UIContributorFactory>,UIContributorFactory> contributors;
  final Map<Tracker,TrackerAdapter> trackers;
  final Object lock;
  private TrackerAdapter trackerAdapter;

  public interface Tracker {
    void removedService( ServiceReference<UIContributorFactory> reference, UIContributor service );
    void addingService( ServiceReference<UIContributorFactory> reference, UIContributor service );
  }

  private static class TrackerAdapter implements Tracker {

    private final ConcurrentHashMap<ServiceReference<UIContributorFactory>,UIContributor> contribs;
    private final Tracker tracker;

    TrackerAdapter( Tracker tracker ) {
      this.tracker = tracker;
      contribs = new ConcurrentHashMap<ServiceReference<UIContributorFactory>, UIContributor>();
    }

    @Override
    public void removedService( ServiceReference<UIContributorFactory> reference,
                                UIContributor service )
    {
      contribs.remove( reference );
      tracker.removedService( reference, service );
    }

    @Override
    public void addingService( final ServiceReference<UIContributorFactory> reference,
                               final UIContributor service )
    {
      tracker.addingService( reference, service );
      contribs.put( reference, service );
    }

    UIContributor getContributor( ServiceReference<UIContributorFactory> reference ) {
      return contribs.get( reference );
    }
  }

  public UIContributorTrackerService( BundleContext context ) {
    Class<UIContributorFactory> type = UIContributorFactory.class;
    this.lock = new Object();
    this.trackers = new HashMap<Tracker,TrackerAdapter>();
    this.contributors = new HashMap<ServiceReference<UIContributorFactory>,UIContributorFactory>();
    this.serviceTracker
      = new ServiceTracker<UIContributorFactory,UIContributorFactory>( context, type, null ) {

      @Override
      public UIContributorFactory addingService( ServiceReference<UIContributorFactory> reference ) {
        UIContributorFactory result = super.addingService( reference );
        Object[] trackerList;
        synchronized( lock ) {
          contributors.put( reference, result );
          trackerList = trackers.values().toArray();
        }
        for( int i = 0; i < trackerList.length; i++ ) {
          ( ( Tracker )trackerList[ i ] ).addingService( reference, result.create() );
        }
        return result;
      }

      @Override
      public void removedService( ServiceReference<UIContributorFactory> reference,
                                  UIContributorFactory service )
      {
        Object[] trackerList;
        synchronized( lock ) {
          contributors.remove( reference );
          trackerList = trackers.values().toArray();
        }
        for( int i = 0; i < trackerList.length; i++ ) {
          TrackerAdapter tracker = ( TrackerAdapter )trackerList[ i ];
          tracker.removedService( reference, tracker.getContributor( reference ) );
        }
        super.removedService( reference, service );
      }
    };
    serviceTracker.open();
  }

  @SuppressWarnings( "unchecked" )
  public void addTracker( Tracker tracker ) {
    Object[] references;
    Object[] services;
    trackerAdapter = new TrackerAdapter( tracker );
    synchronized( lock ) {
      trackers.put( tracker, trackerAdapter );
      references = contributors.keySet().toArray();
      services = contributors.values().toArray();
    }
    for( int i = 0; i < services.length; i++ ) {
      trackerAdapter.addingService( ( ServiceReference<UIContributorFactory> )references[ i ],
                                    ( ( UIContributorFactory )services[ i ] ).create() );
    }
  }

  @SuppressWarnings( "unchecked" )
  public void removeTracker( Tracker tracker ) {
    Object[] references;
    Object[] services;
    TrackerAdapter trackerAdapter;
    synchronized( lock ) {
      trackerAdapter = trackers.remove( tracker );
      references = contributors.keySet().toArray();
      services = contributors.values().toArray();
    }
    for( int i = 0; i < services.length; i++ ) {
      ServiceReference<UIContributorFactory> reference
        = ( ServiceReference<UIContributorFactory> )references[ i ];
      UIContributor contributor = trackerAdapter.getContributor( reference );
      trackerAdapter.removedService( reference, contributor );
    }
  }
}
