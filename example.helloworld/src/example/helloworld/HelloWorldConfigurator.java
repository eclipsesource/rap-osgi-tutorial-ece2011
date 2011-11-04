package example.helloworld;

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.branding.AbstractBranding;


public class HelloWorldConfigurator implements ApplicationConfigurator {

  @Override
  public void configure( ApplicationConfiguration configuration ) {
    configuration.addEntryPoint( "default", HelloWorld.class );
    configuration.addBranding( new AbstractBranding() {
      @Override
      public String getTitle() {
        return "Helloworld";
      }
      @Override
      public String getServletName() {
        return "hello";
      }
    } );
  }

}
