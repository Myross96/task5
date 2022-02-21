package task5.shop.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import task5.shop.ConnectionProvider;
import task5.shop.dao.CategoryDao;
import task5.shop.model.Category;

public class JdbcCategoryDao implements CategoryDao{
	
	private static final String CREATE = "INSERT INTO categories(category_name, description) VALUES (?, ?) ";
	private static final String GET_BY_ID = "SELECT id, category_name, description FROM categories WHERE id = ?";
	private static final String GET_ALL = "SELECT * FROM categories";
	private static final String UPDATE = "UPDATE categories SET category_name = ?, description = ? WHERE id = ?";
	private static final String DELETE = "DELETE FROM categories WHERE id = ?";
	
	private ConnectionProvider provider;
	
	public JdbcCategoryDao(ConnectionProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public void create(Category category) {
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(CREATE,
						Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, category.getName());
			statement.setString(2, category.getDescription());
			
			statement.executeUpdate();
			
			ResultSet set = statement.getGeneratedKeys();
			if (set.next()) {
				category.setId(set.getInt(1));
			}
		} catch (SQLException e) {	
			throw new RuntimeException(e);
		}		
	}

	@Override
	public Optional<Category> getById(int id) {
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_BY_ID)) {
			statement.setInt(1, id);
			
			Category category = null;
			ResultSet set = statement.executeQuery();
			if(set.next()) {
				category = mapToCategory(set);
			}
			return Optional.ofNullable(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Category> getAll() {
		List<Category> categories = new ArrayList<>();
		
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_ALL)) {
			ResultSet set = statement.executeQuery();
			
			while(set.next()) {
				categories.add(mapToCategory(set));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return categories;
	}

	@Override
	public void update(Category category) {
		try (Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE)) {
			statement.setString(1, category.getName());
			statement.setString(2, category.getDescription());
			statement.setInt(3, category.getId());
			
			statement.executeUpdate();
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }		
	}

	@Override
	public void delete(Category category) {
		try (Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE)) {
			statement.setInt(1, category.getId());
			
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Category mapToCategory(ResultSet set) throws SQLException {
		Category category = new Category();
		category.setId(set.getInt("id"));
		category.setName(set.getString("category_name"));
		category.setDescription(set.getString("description"));
		return category;
	}
}
