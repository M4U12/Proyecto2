package dbconection;

import excepciones.BDException;
import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBConection {
    
    public Connection getConection() throws BDException {
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/code_n_school");
            return ds.getConnection();
        } catch (Exception e) {
            throw new BDException("Error al obtener la conexión del Pool del servidor: " + e.getMessage(), e);
        }
    }
}