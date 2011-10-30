package com.codeaffine.example.rwt.osgi.ui.example.slides;

import javax.servlet.http.Cookie;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.lifecycle.WidgetUtil;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;


public class SlidesUIContributor implements UIContributor {

  static final String ID = "Slides";
  private static final String SLIDE_COOKIE = "slide";

  private static String[] slides;

  private Composite slidesHolder;
  private Label counter;
  private Browser slide;
  int selection;

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
    String slideName = getSlides()[ selection - 1 ];
    slide.setText( "<img src=\"" + slideName + "\" width=\"100%\" height=\"100%\" />" );
    updateCounterLabel();
  }

  private void createSlideNavigation() {
    final Composite navigation = createNavigationControl();
    createNavigationContent( navigation );
    layoutNavigation( navigation );
    addDisposeWatchdog( navigation );
  }

  private void addDisposeWatchdog( final Composite navigation ) {
    slidesHolder.addDisposeListener( new DisposeListener() {
      private static final long serialVersionUID = 1L;

      @Override
      public void widgetDisposed( DisposeEvent event ) {
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
        if( getSlides().length > selection ) {
          selection++;
          selectSlide();
        }
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
        if( 1 < selection ) {
          selection--;
          selectSlide();
        }
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
      slides = new String[] {
        registerImage( "rwt-osgi-draft.1.png" ),
        registerImage( "chaos.png" ),
        registerImage( "container.png" )
      };
    }
    return slides;
  }

  private String registerImage( String name ) {
    String result = "/" + Application.RESOURCES + "/" + name;
    RWT.getResourceManager().register( name, getClass().getResourceAsStream( name ) );
    return result;
  }
}