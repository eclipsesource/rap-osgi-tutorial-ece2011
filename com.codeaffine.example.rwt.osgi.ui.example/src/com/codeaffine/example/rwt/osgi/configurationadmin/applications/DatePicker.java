/*******************************************************************************
 * Copyright (c) 2011 Frank Appel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.codeaffine.example.rwt.osgi.configurationadmin.applications;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class DatePicker implements IEntryPoint {

  static class UI1 {
    private final Shell shell;
    private final DateTime calendar;
    private final Label selectedDate;
    private final DateSelectionService dateSelectionService;

    public UI1( DateSelectionService dateSelectionService ) {
      shell = createShell();
      calendar = createCalendar();
      selectedDate = createSelectedDate();
      this.dateSelectionService = dateSelectionService;
      registerDateSelectionListener();
    }

    void open() {
      shell.open();
      shell.layout();
    }

    private Shell createShell() {
      return DatePicker.createShell( new Rectangle( 30, 30, 300, 250 ) );
    }

    private DateTime createCalendar() {
      DateTime result = new DateTime( shell, SWT.CALENDAR | SWT.BORDER );
      result.addSelectionListener( new SelectionAdapter() {
        private static final long serialVersionUID = 1L;

        @Override
        public void widgetSelected( SelectionEvent e ) {
          triggerSelectedDateHasChanged();
        }
      } );
      return result;
    }

    private Label createSelectedDate() {
      return new Label( shell, SWT.NONE );
    }

    void triggerSelectedDateHasChanged() {
      int month = calendar.getMonth() + 1;
      int day = calendar.getDay();
      int year = calendar.getYear();
      String date =  month + "/" + day + "/" + year;
      dateSelectionService.triggerSelection( date );
    }

    void changeSelectedDate( String date ) {
      selectedDate.setText( date );
      shell.layout();
    }

    private void registerDateSelectionListener() {
      dateSelectionService.addServiceListener( new DateSelectionListener() {
        @Override
        public void notify( String date ) {
          changeSelectedDate( date );
        }
      } );
    }
  }

  static class UI2 {
    private final Shell shell;

    UI2( DateSelectionService dateSelectionService ) {
      shell = DatePicker.createShell( new Rectangle( 360, 30, 300, 250 ) );
      final Text text = new Text( shell, SWT.BORDER | SWT.SINGLE );

      dateSelectionService.addServiceListener( new DateSelectionListener() {
        @Override
        public void notify( String date ) {
          text.setText( date );
        }
      } );
    }

    void open() {
      shell.open();
      shell.layout();
    }
  }

  interface DateSelectionListener {
    void notify( String date );
  }

  static class DateSelectionService {
    private final Set<DateSelectionListener> listeners;

    DateSelectionService() {
      listeners = new HashSet<DateSelectionListener>();
    }

    void addServiceListener( DateSelectionListener listener ) {
      listeners.add( listener );
    }

    void triggerSelection( String date ) {
      Iterator<DateSelectionListener> iterator = listeners.iterator();
      while( iterator.hasNext() ) {
        iterator.next().notify( date );
      }
    }
  }

  @Override
  public int createUI() {
    RWT.getSettingStore();
    new Display();

    DateSelectionService dateSelectionService = new DateSelectionService();
    UI1 ui1 = new UI1( dateSelectionService );
    UI2 ui2 = new UI2( dateSelectionService );
    ui1.open();
    ui2.open();
    return 0;
  }

  static Shell createShell( Rectangle bounds ) {
    Shell result = new Shell( Display.getCurrent(), SWT.TITLE | SWT.MAX | SWT.RESIZE );
    result.setBounds( bounds );
    result.setLayout( new GridLayout() );
    result.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    return result;
  }
}
