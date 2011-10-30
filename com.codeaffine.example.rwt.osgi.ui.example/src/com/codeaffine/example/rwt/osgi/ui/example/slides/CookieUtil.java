package com.codeaffine.example.rwt.osgi.ui.example.slides;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.RWT;


public class CookieUtil {

  static Cookie getCookie( String cookieName ) {
    HttpServletRequest request = RWT.getRequest();
    Cookie[] cookies = request.getCookies();
    Cookie result = null;
    for( int i = 0; result == null && i < cookies.length; i++ ) {
      if( cookieName.equals( cookies[ i ].getName() ) ) {
        result = cookies[ i ];
      }
    }
    return result;
  }

  static void setCookie( String cookieName, String value ) {
    Cookie cookie = new Cookie( cookieName, value );
    cookie.setMaxAge( -1 );
    RWT.getResponse().addCookie( cookie );
  }
}
