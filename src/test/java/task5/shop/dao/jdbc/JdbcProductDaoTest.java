package task5.shop.dao.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import task5.shop.ConnectionProvider;
import task5.shop.model.Category;
import task5.shop.model.Product;
import task5.shop.model.Supplier;

class JdbcProductDaoTest extends DataSourceBasedDBTestCase {

	private JdbcProductDao productDao;
	private static String URL;
	private static String USERNAME;
	private static String PASSWORD;
	private static final String SCRIPT = ";init=runscript from 'classpath:schema.sql'";

	@Override
	protected DataSource getDataSource() {
		JdbcDataSource dataSource = new JdbcDataSource();
		Properties properties = new Properties();
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream("database.property"));
			URL = properties.getProperty("url");
			USERNAME = properties.getProperty("username");
			PASSWORD = properties.getProperty("password");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		dataSource.setURL(URL + SCRIPT);
		dataSource.setUser(USERNAME);
		dataSource.setPassword(PASSWORD);

		return dataSource;
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("testdata/test-data.xml")) {
			return new FlatXmlDataSetBuilder().build(resourceAsStream);
		}
	}

	@Override
	protected DatabaseOperation getSetUpOperation() {
		return DatabaseOperation.REFRESH;
	}

	@Override
	protected DatabaseOperation getTearDownOperation() {
		return DatabaseOperation.DELETE_ALL;
	}

	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
		productDao = new JdbcProductDao(new ConnectionProvider("database.property"));
		getDataSet();
	}

	@AfterEach
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	void givenProduct_whenCreate_thenCreate() {
		Category category = new Category();
		category.setId(1);
		Supplier supplier = new Supplier();
		supplier.setId(3);
		Product product = new Product();
		product.setName("Green tea");
		product.setPrice(new BigDecimal(10));
		product.setCategory(category);
		product.setSupplier(supplier);

		productDao.create(product);

		int expected = 6;
		int actual = productDao.getAll().size();

		assertEquals(expected, actual);
	}

	@Test
	void givenProductId_whenGetById_thenGetById() {
		Product expected = new Product();
		expected.setId(2);
		expected.setName("Chang");
		expected.setPrice(new BigDecimal(19.00));

		Product actual = productDao.getById(expected.getId()).get();

		assertEquals(expected, actual);
	}

	@Test
	void givenTableProducts_whenGetAll_thenGetAll() {
		int expected = 5;

		int actual = productDao.getAll().size();

		assertEquals(expected, actual);
	}

	@Test
	void givenProduct_whenUpdate_thenUpdateProduct() {
		Category category = new Category();
		category.setId(1);
		Supplier supplier = new Supplier();
		supplier.setId(3);
		Product expected = new Product();
		expected.setId(4);
		expected.setName("Green tea");
		expected.setPrice(new BigDecimal(10));
		expected.setCategory(category);
		expected.setSupplier(supplier);

		productDao.update(expected);

		Product actual = productDao.getById(expected.getId()).get();

		assertEquals(expected, actual);
	}

	@Test
	void givenProduct_whenDelete_thenDelete() {
		Product product = new Product();
		product.setId(4);

		productDao.delete(product);

		int expected = 4;
		int actual = productDao.getAll().size();
		assertEquals(expected, actual);
	}
	
	@Test
	void  givenLetter_whenGetByFirstLetter_thenReturnProducts() {
		int expected = 4;
		
		int actual = productDao.getByFirstLetter("C").size();
		
		assertEquals(expected, actual);
	}
	
	@Test
	void givenTableProducts_whenGetByLowestPrice_thenGetByLowestPrice() {
		Product expected = new Product();
		expected.setId(3);
		expected.setName("Aniseed Syrup");
		expected.setPrice(new BigDecimal(10.00));
		
		Product actual = productDao.getByLowestPrice().get();
		
		assertEquals(expected, actual);
	}
	
	@Test
	void givenTableProducts_whenGetUSaProductPrice_thenReturnProducts() {
		List<BigDecimal> expected = new ArrayList<>();
		expected.add(new BigDecimal(22.00));
		expected.add(new BigDecimal(21.35));
		
		List<BigDecimal> actual = productDao.getUsaProductsPrice("USA");
		
		assertEquals(expected, actual);
	}
}
