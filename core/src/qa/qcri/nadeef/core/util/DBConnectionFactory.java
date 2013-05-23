/*
 * Copyright (C) Qatar Computing Research Institute, 2013.
 * All rights reserved.
 */

package qa.qcri.nadeef.core.util;

import com.google.common.base.Preconditions;

import org.postgresql.ds.PGPoolingDataSource;
import qa.qcri.nadeef.core.datamodel.NadeefConfiguration;
import qa.qcri.nadeef.tools.DBConfig;
import qa.qcri.nadeef.tools.SQLDialect;
import qa.qcri.nadeef.tools.Tracer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Creates DB connection.
 */
public class DBConnectionFactory {
    private static final int MAX_CONNECTION = 20;
    private static PGPoolingDataSource nadeefPool;
    private static PGPoolingDataSource sourcePool;
    private static DBConfig dbConfig;
    private static Tracer tracer = Tracer.getTracer(DBConnectionFactory.class);

    // <editor-fold desc="Public methods">

    /**
     * Initialize NADEEF database connection pool.
     */
    public static synchronized void initializeNadeefConnectionPool() {
        if (nadeefPool != null) {
            return;
        }

        DBConfig sourceConfig = NadeefConfiguration.getDbConfig();
        nadeefPool = new PGPoolingDataSource();
        nadeefPool.setDataSourceName("nadeef pool");
        nadeefPool.setDatabaseName(sourceConfig.getDatabaseName());
        nadeefPool.setServerName(sourceConfig.getServerName());
        nadeefPool.setUser(sourceConfig.getUserName());
        nadeefPool.setPassword(sourceConfig.getPassword());
        nadeefPool.setMaxConnections(MAX_CONNECTION);
    }

    /**
     * Shutdown the connection pool.
     */
    public synchronized static void shutdown() {
        sourcePool.close();
        nadeefPool.close();
    }

    /**
     * Initialize the source database connection pool.
     * @param sourceConfig source config.
     */
    public synchronized static void initializeSource(DBConfig sourceConfig) {
        Preconditions.checkNotNull(sourceConfig);
        if (dbConfig == sourceConfig || (dbConfig != null && dbConfig.equals(sourceConfig))) {
            return;
        }

        if (sourcePool != null) {
            sourcePool.close();
        }

        dbConfig = sourceConfig;
        sourcePool = new PGPoolingDataSource();
        sourcePool.setDataSourceName("source pool");
        sourcePool.setDatabaseName(sourceConfig.getDatabaseName());
        sourcePool.setServerName(sourceConfig.getServerName());
        sourcePool.setUser(sourceConfig.getUserName());
        sourcePool.setPassword(sourceConfig.getPassword());
        sourcePool.setMaxConnections(MAX_CONNECTION);
    }

    /**
     * Creates a new JDBC connection on the Nadeef database.
     * @return new JDBC connection.
     */
    public static synchronized Connection getNadeefConnection() throws SQLException {
        Connection conn = nadeefPool.getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Creates a new JDBC connection on the source DB from a clean plan.
     * @return new JDBC connection.
     */
    public static Connection getSourceConnection() throws SQLException {
        Connection conn = sourcePool.getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    // </editor-fold>

    //<editor-fold desc="Private methods">
    private static String getDriverName(SQLDialect dialect) {
        switch (dialect) {
            case POSTGRES:
                return "org.postgresql.Driver";
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Get the JDBC connection based on the dialect.
     * @param dialect Database type.
     * @param url Database URL.
     * @param userName login user name.
     * @param password Login user password.
     * @return JDBC connection.
     */
    public static Connection createConnection(
        SQLDialect dialect,
        String url,
        String userName,
        String password
    ) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
        String driverName = getDriverName(dialect);
        Class.forName(driverName).newInstance();
        Connection conn = DriverManager.getConnection(url, userName, password);
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Get the JDBC connection based on the dialect.
     * @param dbConfig dbconfig.
     * @return JDBC connection.
     */
    public static Connection createConnection(DBConfig dbConfig)
        throws
            ClassNotFoundException,
            SQLException,
            IllegalAccessException,
            InstantiationException {
        String driverName = getDriverName(dbConfig.getDialect());
        Class.forName(driverName).newInstance();
        Connection conn =
            DriverManager.getConnection(
                dbConfig.getUrl(),
                dbConfig.getUserName(),
                dbConfig.getPassword()
            );
        conn.setAutoCommit(false);
        return conn;
    }

    //</editor-fold>
}
