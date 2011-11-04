package com.codeaffine.example.rwt.osgi.ui.example.slides;

import javax.servlet.http.Cookie;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;


public class SlidesUIContributor implements UIContributor {

  static final String ID = "Slides";
  private static final String SLIDE_COOKIE = "slide";

  private static String[] slides;

  private Composite slidesHolder;
  private Label counter;
  private Browser slide;
  NavigationKeyBinding keyBindingListener;
  int selection;

  class NavigationKeyBinding implements Listener {

    private static final long serialVersionUID = 1L;

    @Override
    public void handleEvent( Event event ) {
      if( event.keyCode == SWT.PAGE_DOWN ) {
        showNextSlide();
      } else if( event.keyCode == SWT.PAGE_UP ) {
        showPreviousSlide();
      }
    }

    public String[] getActiveKeys() {
      return new String[] { "PAGE_UP", "PAGE_DOWN" };
    }
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public Control contribute( Composite parent ) {
    createSlidesHolder( parent );
    createSlideNavigation();
    selectSlide();
    return slidesHolder;
  }

  private void createSlidesHolder( Composite parent ) {
    slidesHolder = new Composite( parent, SWT.NONE );
    slidesHolder.setLayout( new FillLayout() );
    slide = new Browser( slidesHolder, SWT.NONE );
    initializeSelection();
  }

  private void initializeSelection() {
    Cookie cookie = CookieUtil.getCookie( SLIDE_COOKIE );
    if( cookie != null ) {
      int slideLength = getSlides().length;
      int cookieSelection = Integer.parseInt( cookie.getValue() );
      selection = cookieSelection > slideLength ? 1 : cookieSelection;
    } else {
      selection = 1;
    }
  }

  void selectSlide() {
    String slideHTMLSnippet = getSlides()[ selection - 1 ];
    if( slideHTMLSnippet.startsWith( "http://" ) ) {
      slide.setUrl( slideHTMLSnippet );
    } else {
      slide.setText( slideHTMLSnippet );
    }
    updateCounterLabel();
  }

  private void createSlideNavigation() {
    final Composite navigation = createNavigationControl();
    createNavigationContent( navigation );
    layoutNavigation( navigation );
    registerKeyBindings( navigation );
    addDisposeWatchdog( navigation );
  }

  private void registerKeyBindings( Composite navigation ) {
    final Display display = navigation.getDisplay();
    keyBindingListener = new NavigationKeyBinding();
    display.setData( RWT.ACTIVE_KEYS, keyBindingListener.getActiveKeys() );
    display.addFilter( SWT.KeyDown, keyBindingListener );
  }

  private void addDisposeWatchdog( final Composite navigation ) {
    slidesHolder.addDisposeListener( new DisposeListener() {
      private static final long serialVersionUID = 1L;

      @Override
      public void widgetDisposed( DisposeEvent event ) {
        final Display display = navigation.getDisplay();
        display.removeFilter( SWT.KeyDown, keyBindingListener );
        if( !navigation.isDisposed() ) {
          navigation.dispose();
        }
      }
    } );
  }

  private void layoutNavigation( final Composite navigation ) {
    FormData data = new FormData();
    navigation.setLayoutData( data );
    data.height = MenuBar.MENU_BAR_HEIGHT;
    data.width = 80;
    data.left = new FormAttachment( 100, -10 - data.width );
    data.top = new FormAttachment( 0, 0 );
  }

  private Composite createNavigationControl() {
    Shell shell = slidesHolder.getShell();
    final Composite result = new Composite( shell, SWT.NONE );
    RowLayout layout = new RowLayout();
    layout.marginTop = 10;
    result.setLayout( layout );
    result.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    result.moveAbove( null );
    return result;
  }

  private void createNavigationContent( Composite navigation ) {
    createBackButton( navigation );
    createCounterLabel( navigation );
    createForwardButton( navigation );
  }

  private void createForwardButton( Composite navigation ) {
    Button forward = new Button( navigation, SWT.PUSH );
    forward.setText( "+" );
    forward.setData( WidgetUtil.CUSTOM_VARIANT, "slide_navigation_forward" );
    forward.addSelectionListener( new SelectionAdapter() {

      private static final long serialVersionUID = 1L;

      @Override
      public void widgetSelected( SelectionEvent e ) {
        showNextSlide();
      }
    } );
  }

  private void createCounterLabel( Composite navigation ) {
    counter = new Label( navigation, SWT.NONE );
    updateCounterLabel();
    counter.setData( WidgetUtil.CUSTOM_VARIANT, "slide_counter" );
  }

  private void createBackButton( Composite navigation ) {
    final Button back = new Button( navigation, SWT.PUSH );
    back.setText( "-" );
    back.setData( WidgetUtil.CUSTOM_VARIANT, "slide_navigation_back" );
    back.addSelectionListener( new SelectionAdapter() {

      private static final long serialVersionUID = 1L;

      @Override
      public void widgetSelected( SelectionEvent e ) {
        showPreviousSlide();
      }
    } );
  }

  private void updateCounterLabel() {
    counter.setText( calculateSelection() );
    CookieUtil.setCookie( SLIDE_COOKIE, String.valueOf( selection ) );
  }

  private String calculateSelection() {
    return selection + "/" + getSlides().length;
  }

  String[] getSlides() {
    if( slides == null ) {
      registerImage( "/resources/chart/admin_9090.png" );
      registerImage( "/resources/chart/admin_9091.png" );
      registerImage( "/resources/chart/admin_app_conf.png" );
      registerImage( "/resources/chart/admin_app.png" );
      registerImage( "/resources/chart/example_9090.png" );
      registerImage( "/resources/chart/example_9091.png" );
      registerImage( "/resources/chart/example_app_conf.png" );
      registerImage( "/resources/chart/example_app.png" );
      registerImage( "/resources/chart/example_bundles.png" );
      registerImage( "/resources/chart/http_jetty.png" );
      registerImage( "/resources/chart/jetty.png" );
      registerImage( "/resources/chart/port_9090.png" );
      registerImage( "/resources/chart/port_9091.png" );
      registerImage( "/resources/chart/rwt_osgi.png" );
      registerImage( "/resources/chart/rwt.png" );

      String jQuery = "/resources/jquery-1.5.min.js";
      RWT.getResourceManager().register( jQuery,
                                         getClass().getClassLoader().getResourceAsStream( jQuery ),
                                         "UTF-8",
                                         RegisterOptions.NONE );
      String chart = "/resources/chart/chart.js";
      RWT.getResourceManager().register( chart,
                                         getClass().getClassLoader().getResourceAsStream( chart ),
                                         "UTF-8",
                                         RegisterOptions.NONE );
      String chartPage = "/resources/chart/chart.html";
      RWT.getResourceManager().register( chartPage,
                                         getClass().getClassLoader().getResourceAsStream( chartPage ),
                                         "UTF-8",
                                         RegisterOptions.NONE );

      int localPort = RWT.getRequest().getLocalPort();

      slides = new String[] {
        getHTMLSnippet( registerImage( "start.png" ) ),
        getHTMLSnippet( registerImage( "WebFrameworks.png" ) ),
        getHTMLSnippet( registerImage( "widget-toolkit.png" ) ),
        getHTMLSnippet( registerImage( "widgets.png" ) ),
        getHTMLSnippet( registerImage( "modularity.png" ) ),
        getHTMLSnippet( registerImage( "chaos.png" ) ),
        getHTMLSnippet( registerImage( "container.png" ) ),
        getHTMLSnippet( registerImage( "dynamic.png" ) ),
        getHTMLSnippet( registerImage( "twitter.png" ) ),
        getHTMLSnippet( registerImage( "munsters.png" ) ),
        "http://localhost:" + localPort + "/" + Application.RESOURCES + chartPage,
        getHTMLSnippet( registerImage( "resources.png" ) )
      };
    }
    return slides;
  }

  private String getHTMLSnippet( String slideName ) {
    return "<img src=\"" + slideName + "\" width=\"100%\" height=\"100%\" />";
  }

  private String registerImage( String name ) {
    String result = "/" + Application.RESOURCES + "/" + name;
    if( name.startsWith( "/" ) ) {
      result = "/" + Application.RESOURCES + name;
    }
    RWT.getResourceManager().register( name, getClass().getResourceAsStream( name ) );
    return result;
  }

  void showNextSlide() {
    if( getSlides().length > selection ) {
      selection++;
      selectSlide();
    }
  }

  void showPreviousSlide() {
    if( 1 < selection ) {
      selection--;
      selectSlide();
    }
  }
}