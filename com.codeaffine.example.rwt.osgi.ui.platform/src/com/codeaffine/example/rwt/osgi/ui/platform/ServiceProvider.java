package com.codeaffine.example.rwt.osgi.ui.platform;

public interface ServiceProvider {
  <T> T register( Class<T> service, T instance );
  <T> T get( Class<T> service );
}
