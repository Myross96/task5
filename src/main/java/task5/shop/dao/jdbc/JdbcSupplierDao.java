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
import task5.shop.dao.SupplierDao;
import task5.shop.model.Category;
import task5.shop.model.Supplier;

public class JdbcSupplierDao implements SupplierDao {
	
	private static final String CREATE = "INSERT INTO suppliers(supplier_name, city, country) VALUES (?, ?, ?)";
	private static final String GET_BY_ID = "SELECT * FROM suppliers WHERE id = ?";
	private static final String GET_ALL = "SELECT * FROM suppliers";
	private static final String UPDATE = "UPDATE suppliers SET supplier_name =?, city = ?, country = ? WHERE id = ?";
	private static final String DELETE = "DELETE FROM suppliers WHERE id = ?";
	private static final String GET_BY_CATEGORY_NAME = "SELECT suppliers.id, supplier_name, city, country FROM suppliers LEFT JOIN supplier_category ON suppliers.id = supplier_id"
			+ " LEFT JOIN categories ON category_id = categories.id WHERE category_name = ?";
	private static final String CREATE_SUPPLIER_CATEGORY = "INSERT INTO supplier_category (supplier_id, category_id) VALUES (?, ?)";
	private static final String REMOVE_CATEGORIES_FROM_SUPPLIER = "DELETE FROM supplier_category WHERE supplier_id = ?";
	private static final String GET_CATEGORIES_RELATED_TO_SUPPLIER = "SELECT id, category_name, description FROM categories LEFT JOIN "
			+ "supplier_category ON id = category_id WHERE supplier_id = ?";
	
	private ConnectionProvider provider;
	
	public JdbcSupplierDao(ConnectionProvider provider) {
		this.provider = provider;
	}

	@Override
	public void create(Supplier supplier) {
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(CREATE,
						Statement.RETURN_GENERATED_KEYS)) {
			connection.setAutoCommit(false);
			statement.setString(1, supplier.getName());
			statement.setString(2, supplier.getCity());
			statement.setString(3, supplier.getCountry());
			statement.execute();
			
			ResultSet set = statement.getGeneratedKeys();
			if(set.next()) {
				supplier.setId(set.getInt(1));
			}
			createSupplierCategory(supplier.getId(), supplier.getCategories(), connection);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}	
		
	}

	@Override
	public Optional<Supplier> getById(int id) {
		Supplier supplier = null;
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_BY_ID)) {
			statement.setInt(1, id);
			
			ResultSet set = statement.executeQuery();
			if (set.next()) {
				supplier = mapToSupplier(set);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		if (supplier != null) {
			supplier.setCategories(getCategoriesRelatedToSupplier(id));
		}
		return Optional.ofNullable(supplier);
	}

	@Override
	public List<Supplier> getAll() {
		List<Supplier> suppliers = new ArrayList<>();
		
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_ALL)) {
			ResultSet set = statement.executeQuery();
			while(set.next()) {
				suppliers.add(mapToSupplier(set));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return suppliers;
	}

	@Override
	public void update(Supplier supplier) {
		try (Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE)) {
			connection.setAutoCommit(false);
			
			statement.setString(1, supplier.getName());
			statement.setString(2, supplier.getCity());
			statement.setString(3, supplier.getCountry());
			statement.setInt(4, supplier.getId());
			
			statement.executeUpdate();
			
			removeCategory(supplier.getId(), connection);
			createSupplierCategory(supplier.getId(), supplier.getCategories(), connection);
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }			
	}

	@Override
	public void delete(Supplier supplier) {
		try (Connection connection = provider.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE)) {
			statement.setInt(1, supplier.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
	}

	@Override
	public List<Supplier> getByCategoryName(String category) {
		List<Supplier> suppliers = new ArrayList<>();
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_BY_CATEGORY_NAME)) {
			statement.setString(1, category);
			ResultSet set = statement.executeQuery();
			while (set.next()) {
				suppliers.add(mapToSupplier(set));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
 		return suppliers;
	}

	private void createSupplierCategory(int supplierId, List<Category> categories, Connection connection) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(CREATE_SUPPLIER_CATEGORY)) {
			for (int i = 0; i < categories.size(); i++) {
				statement.setInt(1, supplierId);
				statement.setInt(2, categories.get(i).getId());
				statement.executeUpdate();
			}
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw new RuntimeException(e);
		}
	}
	
	private void removeCategory(int supplierId, Connection connection) {
		try (PreparedStatement statement = connection.prepareStatement(REMOVE_CATEGORIES_FROM_SUPPLIER)) {
			statement.setInt(1, supplierId);
			
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Supplier mapToSupplier(ResultSet set) throws SQLException {
		Supplier supplier = new Supplier();
		supplier.setId(set.getInt("id"));
		supplier.setName(set.getString("supplier_name"));
		supplier.setCity(set.getString("city"));
		supplier.setCountry(set.getString("country"));
		return supplier;
	}
	
	private Category mapToCategory(ResultSet set) throws SQLException {
		Category category = new Category();
		category.setId(set.getInt("id"));
		category.setName(set.getString("category_name"));
		category.setDescription(set.getString("description"));
		return category;
	}
	
	private List<Category> getCategoriesRelatedToSupplier(int id) {
		List<Category> categories = new ArrayList<>();
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_CATEGORIES_RELATED_TO_SUPPLIER)) {
			statement.setInt(1, id);
			ResultSet set = statement.executeQuery();
			
			while (set.next()) {
				categories.add(mapToCategory(set));
			}			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return categories;
	}
}
