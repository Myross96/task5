package task5.shop.dao.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import task5.shop.ConnectionProvider;
import task5.shop.dao.ProductDao;
import task5.shop.model.Category;
import task5.shop.model.Product;
import task5.shop.model.Supplier;

public class JdbcProductDao implements ProductDao {

	private static final String CREATE = "INSERT INTO products (product_name, supplier_id, category_id, price) VALUES (?, ?, ?, ?)";
	private static final String GET_BY_ID = "SELECT * FROM products WHERE id = ?";
	private static final String GET_ALL = "SELECT * FROM products";
	private static final String UPDATE = "UPDATE products SET product_name = ?, supplier_id = ?, category_id = ?, price = ? WHERE id = ?";
	private static final String DELETE = "DELETE FROM products WHERE id = ?";
	private static final String GET_BY_FIRST_LETTER = "SELECT * FROM products WHERE product_name LIKE ?";
	private static final String GET_BY_LOWEST_PRICE = "SELECT * FROM products WHERE price=(SELECT MIN(price) FROM products)";
	private static final String GET_USA_PRODUCTS_PRICE = "SELECT price FROM products LEFT JOIN suppliers ON supplier_id = suppliers.id WHERE country = ?";

	private ConnectionProvider provider;

	public JdbcProductDao(ConnectionProvider provider) {
		this.provider = provider;
	}

	@Override
	public void create(Product product) {
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, product.getName());
			statement.setInt(2, product.getSupplier().getId());
			statement.setInt(3, product.getCategory().getId());
			statement.setBigDecimal(4, product.getPrice());
			statement.execute();
			ResultSet set = statement.getGeneratedKeys();
			while (set.next()) {
				product.setId(set.getInt(1));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<Product> getById(int id) {
		Product product = null;
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_BY_ID)) {
			statement.setInt(1, id);
			ResultSet set = statement.executeQuery();
			
			if (set.next()) {
				product = mapToProduct(set);
			}
			return Optional.ofNullable(product);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Product> getAll() {
		List<Product> products = new ArrayList<>();

		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_ALL)) {
			ResultSet set = statement.executeQuery();
			while (set.next()) {
				products.add(mapToProduct(set));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return products;
	}

	@Override
	public void update(Product product) {
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE)) {
			statement.setString(1, product.getName());
			statement.setInt(2, product.getSupplier().getId());
			statement.setInt(3, product.getCategory().getId());
			statement.setBigDecimal(4, product.getPrice());
			statement.setInt(5, product.getId());

			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(Product product) {
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE)) {
			statement.setInt(1, product.getId());
			
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Product> getByFirstLetter(String letter) {
		List<Product> products = new ArrayList<>();
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_BY_FIRST_LETTER)) {
			statement.setString(1, letter + "%");	
			
			ResultSet set = statement.executeQuery();
			
			while (set.next()) {
				products.add(mapToProduct(set));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return products;
	}

	@Override
	public Optional<Product> getByLowestPrice() {
		Product product = null;
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_BY_LOWEST_PRICE)) {
			ResultSet set = statement.executeQuery();
			
			if (set.next()) {
				product = mapToProduct(set);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return Optional.ofNullable(product);
	}

	@Override
	public List<BigDecimal> getUsaProductsPrice(String countryName) {
		List<BigDecimal> prices = new ArrayList<>();
		try (Connection connection = provider.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_USA_PRODUCTS_PRICE)) {
			statement.setString(1, countryName);
			
			ResultSet set = statement.executeQuery();
			while (set.next()) {
				prices.add(set.getBigDecimal("price"));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}	
		return prices;
	}

	private Product mapToProduct(ResultSet set) throws SQLException {
		Product product = new Product();
		Category category = new Category();
		category.setId(set.getInt("category_id"));
		Supplier supplier = new Supplier();
		supplier.setId(set.getInt("supplier_id"));
		product.setId(set.getInt("id"));
		product.setName(set.getString("product_name"));
		product.setPrice(set.getBigDecimal("price"));
		product.setSupplier(supplier);
		product.setCategory(category);
		return product;
	}
}
