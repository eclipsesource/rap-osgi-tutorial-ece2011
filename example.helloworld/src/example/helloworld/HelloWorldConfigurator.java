package example.helloworld;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.client.WebClient;


public class HelloWorldConfigurator implements ApplicationConfiguration {

  @Override
  public void configure( Application configuration ) {
    Map<String, String> properties = new HashMap<String, String>();
    properties.put( WebClient.PAGE_TITLE, "Helloworld" );
    configuration.addEntryPoint( "/hello", HelloWorld.class, properties );
  }

}
