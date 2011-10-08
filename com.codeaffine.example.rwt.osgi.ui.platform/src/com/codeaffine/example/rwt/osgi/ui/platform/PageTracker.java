package com.codeaffine.example.rwt.osgi.ui.platform;


public interface PageTracker {
  void pageAdded( UIContributor page );
  void pageRemoved( UIContributor page );
}
