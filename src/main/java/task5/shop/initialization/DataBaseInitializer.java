package task5.shop.initialization;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import task5.shop.ConnectionProvider;

public class DataBaseInitializer {

	private ConnectionProvider provider;

	public DataBaseInitializer(ConnectionProvider provider) {
		this.provider = provider;
	}

	public void initialize() {
		Reader reader = new Reader();
		try (Connection connection = provider.getConnection(); Statement statement = connection.createStatement()) {
			String sql = reader.readFile("schema.sql");
			statement.execute(sql);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
