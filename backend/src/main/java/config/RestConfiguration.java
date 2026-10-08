package config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

// esta anotacion define que todas las rutas empezarán con /api/
@ApplicationPath("api")
public class RestConfiguration extends Application {

}
