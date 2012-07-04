package com.codeaffine.example.rwt.osgi.ui.example.admin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;
import com.eclipsesource.example.ece2011.ui.admin.AdminUI;


public class AdminTab implements UIContributor {

  private AdminUI adminUI;

  @Override
  public String getId() {
    return "Admin";
  }

  @Override
  public Control contribute( Composite parent ) {
    adminUI = new AdminUI();
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new FillLayout() );
    adminUI.createContent( composite );
    return composite;
  }
}
