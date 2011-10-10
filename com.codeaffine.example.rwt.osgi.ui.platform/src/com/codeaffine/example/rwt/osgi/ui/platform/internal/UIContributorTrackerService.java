package com.codeaffine.example.rwt.osgi.ui.platform.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;


public class UIContributorTrackerService {
  
  final ServiceTracker<UIContributor, UIContributor> serviceTracker;
  final Map<ServiceReference<UIContributor>,UIContributor> contributors;
  final Set<Tracker> trackers;
  final Object lock;
  
  public interface Tracker {
    void removedService( ServiceReference<UIContributor> reference, UIContributor service );
    void addingService( ServiceReference<UIContributor> reference, UIContributor service );
  }

  public UIContributorTrackerService( BundleContext context ) {
    Class<UIContributor> type = UIContributor.class;
    this.lock = new Object();
    this.trackers = new HashSet<Tracker>();
    this.contributors = new HashMap<ServiceReference<UIContributor>,UIContributor>();
    this.serviceTracker = new ServiceTracker<UIContributor,UIContributor>( context, type, null ) {

      @Override
      public UIContributor addingService( ServiceReference<UIContributor> reference ) {
        UIContributor result = super.addingService( reference );
        Object[] trackerList;
        synchronized( lock ) {
          contributors.put( reference, result );
          trackerList = trackers.toArray();
        }
        for( int i = 0; i < trackerList.length; i++ ) {
          ( ( Tracker )trackerList[ i ] ).addingService( reference, result );
        }
        return result;
      }

      @Override
      public void removedService( ServiceReference<UIContributor> reference,
                                  UIContributor service )
      {
        Object[] trackerList;
        synchronized( lock ) {
          contributors.remove( reference );
          trackerList = trackers.toArray();
        }
        for( int i = 0; i < trackerList.length; i++ ) {
          ( ( Tracker )trackerList[ i ] ).removedService( reference, service );
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
    synchronized( lock ) {
      trackers.add( tracker );
      references = contributors.keySet().toArray();
      services = contributors.values().toArray();
    }
    for( int i = 0; i < services.length; i++ ) {
      tracker.addingService( ( ServiceReference<UIContributor> )references[ i ], 
                             ( UIContributor )services[ i ] );
    }
  }
  
  @SuppressWarnings( "unchecked" )
  public void removeTracker( Tracker tracker ) {
    Object[] references;
    Object[] services;
    synchronized( lock ) {
      trackers.remove( tracker );
      references = contributors.keySet().toArray();
      services = contributors.values().toArray();
    }
    for( int i = 0; i < services.length; i++ ) {
      tracker.removedService( ( ServiceReference<UIContributor> )references[ i ], 
                              ( UIContributor )services[ i ] );
    }
  }
}
