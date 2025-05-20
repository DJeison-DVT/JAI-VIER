package com.springboot.MyTodoList.config;

import oracle.jdbc.pool.OracleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
public class OracleConfiguration {
    Logger logger = LoggerFactory.getLogger(OracleConfiguration.class);

    @Autowired
    private DbSettings dbSettings;

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() throws SQLException {
        try {
            // String walletLocation = "app/wallet";
            String walletLocation = "C:/VisualProyectsPC/JAIVIER/NEWJAIVIER/JAI-VIER/MtdrSpring/backend/src/main/resources/wallet";
            System.setProperty("oracle.net.tns_admin", walletLocation);
            logger.info("Set TNS_ADMIN to: " + walletLocation);

            OracleDataSource ds = new OracleDataSource();

            // Intenta obtener valores de las variables de entorno primero
            if (env.getProperty("db_user") != null) {
                ds.setDriverType(env.getProperty("driver_class_name"));
                logger.info("Using Driver " + env.getProperty("driver_class_name"));
                ds.setURL(env.getProperty("db_url"));
                logger.info("Using URL: " + env.getProperty("db_url"));
                ds.setUser(env.getProperty("db_user"));
                logger.info("Using Username " + env.getProperty("db_user"));
                ds.setPassword(env.getProperty("dbpassword"));
            }
            // Si no hay variables de entorno, usa la configuración específica para Oracle
            // Cloud
            else {
                logger.info("Environment variables not found, using wallet configuration");

                // Configurar para Oracle Cloud
                ds.setDriverType("thin");
                ds.setUser("TODOUSER");
                ds.setPassword("UC6KtSFHS5cW9qE");

                // Usar un formato simplificado con el TNS_ADMIN en las propiedades
                ds.setURL("jdbc:oracle:thin:@ixtbijmvb4aj7f1b_high");
                logger.info("Using URL: jdbc:oracle:thin:@ixtbijmvb4aj7f1b_high");

                // Configurar propiedades adicionales
                Properties props = new Properties();
                props.setProperty("oracle.net.tns_admin", walletLocation);
                props.setProperty("oracle.net.wallet_location",
                        "(SOURCE=(METHOD=file)(METHOD_DATA=(DIRECTORY=" + walletLocation + ")))");
                props.setProperty("oracle.net.ssl_server_dn_match", "true");
                ds.setConnectionProperties(props);

                // Prueba de conexión para verificar
                try (Connection conn = ds.getConnection()) {
                    logger.info("¡CONEXIÓN A ORACLE EXITOSA!");
                }
            }

            return ds;
        } catch (SQLException e) {
            logger.error("Error configurando Oracle DataSource: " + e.getMessage(), e);

            // Si falla, intentamos un enfoque alternativo
            try {
                logger.info("Trying alternate connection approach");

                OracleDataSource ds = new OracleDataSource();

                // Usar conexión directa
                ds.setDriverType("thin");
                ds.setServerName("adb.mx-queretaro-1.oraclecloud.com");
                ds.setPortNumber(1522);
                ds.setServiceName("gbd25337936632d_ixtbijmvb4aj7f1b_high.adb.oraclecloud.com");
                ds.setUser("TODOUSER");
                ds.setPassword("UC6KtSFHS5cW9qE");

                // Configuración de seguridad
                Properties props = new Properties();
                // String walletLocation = "app/wallet";
                String walletLocation = "C:/VisualProyectsPC/JAIVIER/NEWJAIVIER/JAI-VIER/MtdrSpring/backend/src/main/resources/wallet";
                props.setProperty("oracle.net.tns_admin", walletLocation);
                props.setProperty("oracle.net.wallet_location",
                        "(SOURCE=(METHOD=file)(METHOD_DATA=(DIRECTORY=" + walletLocation + ")))");
                props.setProperty("oracle.net.ssl_server_dn_match", "true");

                ds.setConnectionProperties(props);
                logger.info("Configured alternate connection");

                return ds;
            } catch (SQLException ex) {
                logger.error("Error in alternate method: " + ex.getMessage(), ex);
                throw ex;
            }
        }
    }
}