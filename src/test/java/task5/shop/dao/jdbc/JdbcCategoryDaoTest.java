package task5.shop.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
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

class JdbcCategoryDaoTest extends DataSourceBasedDBTestCase {

	private JdbcCategoryDao categoryDao;
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
		categoryDao = new JdbcCategoryDao(new ConnectionProvider("database.property"));
		getDataSet();
	}

	@AfterEach
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	void givenCategory_wheCreate_thenCreate() {
		Category category = new Category();
		category.setName("test");
		category.setDescription("test description");

		categoryDao.create(category);

		int expected = 6;
		int actual = categoryDao.getAll().size();

		assertEquals(expected, actual);
	}

	@Test
	void givenCategoryId_whenGetById_thenGetById() {
		Category expected = new Category();
		expected.setId(2);
		expected.setName("Condiments");
		expected.setDescription("Sweet and savory sauces, relishes, spreads, and seasonings");

		Category actual = categoryDao.getById(2).get();

		assertEquals(expected, actual);
	}

	@Test
	void givenTableCategories_whenGetAll_thenGetAll() {
		int expected = 5;
		int actual = categoryDao.getAll().size();

		assertEquals(expected, actual);
	}

	@Test
	void givenCategory_whenUpdate_thenUpdateCategory() {
		Category expected = new Category();
		expected.setId(1);
		expected.setName("test");
		expected.setDescription("test description");

		categoryDao.update(expected);

		Category actual = categoryDao.getById(1).get();
		assertEquals(expected, actual);
	}

	@Test
	void givenCategory_whenDelete_thenThrowException() {
		Category category = new Category();
		category.setId(1);

		assertThrows(RuntimeException.class, () -> categoryDao.delete(category));
	}
}
