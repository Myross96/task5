package task5.shop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionProvider {

	private static final String DRIVER_CLASS = "driver";
	private static final String URL = "url";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private Properties properties;

	public ConnectionProvider(String configFileName) {
		properties = new Properties();
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream(configFileName));
			Class.forName(properties.getProperty(DRIVER_CLASS));
		} catch(IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(properties.getProperty(URL), properties.getProperty(USERNAME),
				properties.getProperty(PASSWORD));

	}
}
