package com.codeaffine.example.rwt.osgi.ui.example.slides;

import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;


public class SlidesUIContributor implements UIContributor {

  private Composite slidesHolder;
  private Label counter;
  private Label slide;
  int selection;

  @Override
  public String getId() {
    return "Slides";
  }

  @Override
  public Control contribute( Composite parent ) {
    createSlidesHolder( parent );
    createSlideNavigation();
    return slidesHolder;
  }

  private void createSlidesHolder( Composite parent ) {
    slidesHolder = new Composite( parent, SWT.NONE );
    slidesHolder.setLayout( new FillLayout() );
    slide = new Label( slidesHolder, SWT.NONE );
    selection = 1;
    selectSlide();
  }

  void selectSlide() {
    Image image = getSlides()[ selection - 1 ];
    slide.setImage( image );
  }

  private void createSlideNavigation() {
    Shell shell = slidesHolder.getShell();
    final Composite navigation = new Composite( shell, SWT.NONE );
    RowLayout layout = new RowLayout();
    layout.marginTop = 10;
    navigation.setLayout( layout );
    navigation.setData( WidgetUtil.CUSTOM_VARIANT, "navigation" );
    navigation.moveAbove( null );

    createNavigationControls( navigation );
    
    FormData data = new FormData();
    navigation.setLayoutData( data );
    data.height = MenuBar.MENU_BAR_HEIGHT;
    data.width = 80;
    data.left = new FormAttachment( 100, -10 - data.width );
    data.top = new FormAttachment( 0, 0 );
    
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

  private void createNavigationControls( Composite navigation ) {
    
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
    
    counter = new Label( navigation, SWT.NONE );
    counter.setText( calculateSelection() );
    counter.setData( WidgetUtil.CUSTOM_VARIANT, "slide_counter" );
    
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

  private String calculateSelection() {
    return selection + "/" + getSlides().length;
  }
  
  Image[] getSlides() {
    return new Image[] {
      new Image( Display.getCurrent(), getClass().getResourceAsStream( "rwt-osgi-draft.1.png" ) ),
      new Image( Display.getCurrent(), getClass().getResourceAsStream( "chaos.png" ) ),
      new Image( Display.getCurrent(), getClass().getResourceAsStream( "container.png" ) )
    };
  }
}
