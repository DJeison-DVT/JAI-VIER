package com.oracle.database;

import io.helidon.config.Config;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class AppDatasource {

    // connection pooling using UCP
    private final PoolDataSource pds;
    private static AppDatasource instance = null;

    // static get
    public synchronized static AppDatasource get(Config c) {
        if (instance == null) {
            instance = new AppDatasource(c);
        }
        return instance;
    }

    // constructor, initialize datasource
    private AppDatasource(Config c) {
        this.pds = PoolDataSourceFactory.getPoolDataSource();
        String url = c.get("url").asString().orElse("");
        String username = c.get("user").asString().orElse("");
        String password = c.get("password").asString().orElse("");

        try {

            // In this application, we don't set any init, min or max size in UCP. We
            // also don't start the pool explicitly. This means that the very first
            // connection request will start the pool. The default maximum pool size
            // is MAX_INT which isn't appropriate and should be configured properly in
            // production.
            this.pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
            this.pds.setConnectionPoolName("JDBC_UCP_POOL");
            this.pds.setInactiveConnectionTimeout(60);
            this.pds.setMaxStatements(10);

            // if provided, set
            if (!url.isEmpty()) {
                this.pds.setURL(url);
            }
            if (!username.isEmpty()) {
                this.pds.setUser(username);
            }
            if (!password.isEmpty()) {
                this.pds.setPassword(password);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // retrieve connection
    public Connection getConnection() throws SQLException {
        return this.pds.getConnection();
    }








}
