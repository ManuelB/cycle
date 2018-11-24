package de.incentergy.cycle;

import javax.annotation.sql.DataSourceDefinition;

@DataSourceDefinition(name = "java:/PostgresSQL", className = "org.postgresql.ds.PGPoolingDataSource", serverName = "localhost", portNumber = 5434, databaseName = "cycle", user = "postgres", password = "toaster")
public class Datasource {

}
