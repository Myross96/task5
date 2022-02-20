package task5.shop.dao.jdbc;

import java.io.IOException;
import java.io.InputStream;
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
import task5.shop.model.Supplier;

class JdbcSupplierDaoTest extends DataSourceBasedDBTestCase {

	private JdbcSupplierDao supplierDao;
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
		supplierDao = new JdbcSupplierDao(new ConnectionProvider("database.property"));
		getDataSet();
	}

	@AfterEach
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	void givenSupplier_wheCreatethenCreate() {
		Category category = new Category();
		category.setId(2);
		Supplier supplier = new Supplier();
		supplier.setName("test");
		supplier.setCity("test");
		supplier.setCountry("test");
		supplier.getCategories().add(category);

		supplierDao.create(supplier);

		int expected = 6;
		int actual = supplierDao.getAll().size();

		assertEquals(expected, actual);
	}

	@Test
	void givenSupplierId_whenGetById_thenGetById() {
		Supplier expected = new Supplier();
		expected.setId(1);
		expected.setName("Exotic Liquid");
		expected.setCity("London");
		expected.setCountry("UK");

		Supplier actual = supplierDao.getById(expected.getId()).get();

		assertEquals(expected, actual);
	}

	@Test
	void givenTableSuppliers_whenGetAll_thenGetAll() {
		int expected = 5;

		int actual = supplierDao.getAll().size();

		assertEquals(expected, actual);
	}

	@Test
	void givenSupplier_whenUpdate_thenUpdate() {
		Category category = new Category();
		category.setId(2);
		Supplier expected = new Supplier();
		expected.setId(1);
		expected.setName("test");
		expected.setCity("test");
		expected.setCountry("test");
		expected.getCategories().add(category);

		supplierDao.update(expected);

		Supplier actual = supplierDao.getById(expected.getId()).get();
		assertEquals(expected, actual);
	}

	@Test
	void givenSupplier_whenDelete_thenDelete() {
		Supplier supplier = new Supplier();
		supplier.setId(4);

		supplierDao.delete(supplier);

		int expected = 4;
		int actual = supplierDao.getAll().size();
		assertEquals(expected, actual);
	}
	
	@Test
	void givenCategoryName_whenGetByCategoryName_thenGetSuppliers() {
		Supplier supplier = new Supplier();
		supplier.setId(3);
		supplier.setName("Grandma Kelly’s Homestead");
		supplier.setCity("Ann Arbor");
		supplier.setCountry("USA");
		List<Supplier> expected = new ArrayList<>();
		expected.add(supplier);
		
		List<Supplier> actual = supplierDao.getByCategoryName("Confections");
		
		assertEquals(expected, actual);
	}
}
