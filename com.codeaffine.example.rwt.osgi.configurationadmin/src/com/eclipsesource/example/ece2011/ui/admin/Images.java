package com.eclipsesource.example.ece2011.ui.admin;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;


public class Images {

  public Image applicationImage;
  public Image contributionImage;

  public Images( Device display ) {
    applicationImage = createImage( display, "resources/application-16.png" );
    contributionImage = createImage( display, "resources/contribution-16.png" );
  }

  private static Image createImage( Device device, String name ) {
    Image result;
    ClassLoader classLoader = AdminUI.class.getClassLoader();
    InputStream inputSteam = classLoader.getResourceAsStream( name  );
    if( inputSteam == null ) {
      throw new IllegalArgumentException( "Image not found" );
    }
    try {
      result = new Image( device, inputSteam );
    } finally {
      try {
        inputSteam.close();
      } catch( IOException e ) {
      }
    }
    return result;
  }

}
