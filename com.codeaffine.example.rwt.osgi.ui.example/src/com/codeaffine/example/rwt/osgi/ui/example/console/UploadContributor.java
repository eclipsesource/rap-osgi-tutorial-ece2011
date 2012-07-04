package com.codeaffine.example.rwt.osgi.ui.example.console;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadHandler;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadReceiver;
import org.eclipse.rap.rwt.supplemental.fileupload.IFileUploadDetails;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.rwt.widgets.DialogCallback;
import org.eclipse.rwt.widgets.DialogUtil;
import org.eclipse.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

import com.codeaffine.example.rwt.osgi.ui.platform.ServiceProvider;
import com.codeaffine.example.rwt.osgi.ui.platform.UIContributor;


public class UploadContributor implements UIContributor {

  private static final int OFFSET = 5;

  FileUpload fileChooserButton;
  Label selectionLabel;
  FileUploadHandler handler;
  ServiceProvider serviceProvider;
  Button uploadButton;

  void setServiceProvider( ServiceProvider serviceProvider ) {
    this.serviceProvider = serviceProvider;
  }

  @Override
  public Control contribute( Composite parent ) {
    Composite result = new Composite( parent, SWT.NONE );
    result.setData( WidgetUtil.CUSTOM_VARIANT, "upload-panel" );
    result.setLayout( new FormLayout() );
    createContent( result );
    return result;
  }

  private void createContent( Composite container ) {
    createFileChooser( container );
    createSelectionLabel( container );
    createUploadButton( container );
    configureFileUpload();
  }

  private void createFileChooser( Composite container ) {
    fileChooserButton = new FileUpload( container, SWT.NONE );
    fileChooserButton.setText( "Choose a bundle to upload" );
    FormData fileChooserButtonData = new FormData();
    fileChooserButton.setLayoutData( fileChooserButtonData );
    fileChooserButtonData.left = new FormAttachment( 0, 5 );
    fileChooserButtonData.top = new FormAttachment( 0, OFFSET );
    fileChooserButtonData.bottom = new FormAttachment( 100, -OFFSET );
  }

  private void createSelectionLabel( Composite container ) {
    selectionLabel = new Label( container, SWT.NONE );
    selectionLabel.setData( WidgetUtil.CUSTOM_VARIANT, "upload-selection" );
    selectionLabel.setText( "" );
    FormData selectionLabelData = new FormData();
    selectionLabel.setLayoutData( selectionLabelData );
    selectionLabelData.left = new FormAttachment( fileChooserButton, OFFSET );
    selectionLabelData.top = new FormAttachment( 0, OFFSET );
    selectionLabelData.height = 18;
    selectionLabelData.width = 400;
  }

  private void createUploadButton( Composite container ) {
    uploadButton = new Button( container, SWT.PUSH );
    uploadButton.setText( "upload" );
    uploadButton.setEnabled( false );
    uploadButton.setData( WidgetUtil.CUSTOM_VARIANT, "upload-button" );
    FormData uploadButtonData = new FormData();
    uploadButton.setLayoutData( uploadButtonData );
    uploadButtonData.left = new FormAttachment( selectionLabel, 5 );
    uploadButtonData.top = new FormAttachment( 0, OFFSET );
    uploadButton.addSelectionListener( new SelectionAdapter() {
      private static final long serialVersionUID = 1L;

      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( !selectionLabel.getText().equals( "" ) ) {
          fileChooserButton.submit( handler.getUploadUrl() );
          uploadButton.setEnabled( false );
        }
      }
    } );
  }

  private void configureFileUpload() {
    FileUploadReceiver receiver = new FileUploadReceiver() {
      @Override
      public void receive( InputStream dataStream, IFileUploadDetails details ) throws IOException {
        installBundle( dataStream, details );
      }
    };
    handler = new FileUploadHandler( receiver );
    fileChooserButton.addSelectionListener( new SelectionAdapter() {
      private static final long serialVersionUID = 1L;

      @Override
      public void widgetSelected( SelectionEvent e ) {
        handleSelectedFile();
      }
    } );
  }

  protected void installBundle( InputStream bundleStream, IFileUploadDetails details ) {
    String location = details.getFileName();
    BundleContext bundleContext = getBundle().getBundleContext();
    try {
      Bundle bundle = bundleContext.installBundle( location, bundleStream );
      bundle.start();
    } catch( BundleException be ) {
      throw new IllegalArgumentException( "Unable to register regulation bundle: " + location, be );
    }
  }


  Bundle getBundle() {
    return FrameworkUtil.getBundle( getClass() );
  }

  void handleSelectedFile() {
    String fileName = fileChooserButton.getFileName();
    if( fileName.endsWith( ".jar" ) ) {
      handleValidFileSelection();
    } else {
      handleInvalidFileSelection();
    }
  }

  private void handleValidFileSelection() {
    selectionLabel.setText( fileChooserButton.getFileName() );
    uploadButton.setEnabled( true );
  }

  private void handleInvalidFileSelection() {
    uploadButton.setEnabled( false );
    MessageBox box = new MessageBox( fileChooserButton.getShell(), SWT.ICON_ERROR | SWT.OK );
    box.setMessage( "The selected file is not a jar file." );
    selectionLabel.setText( "" );
    DialogUtil.open( box, new DialogCallback() {
      private static final long serialVersionUID = 1L;

      @Override
      public void dialogClosed( int returnCode ) {
        // TODO [fappel]: remove this once null is allowed as dialog callback parameter
      }
    } );
  }

  public void reenableUploadButton() {
    uploadButton.setEnabled( true );
  }

  @Override
  public String getId() {
    return "Bundle-Upload";
  }
}
