package task5.shop.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import task5.shop.model.Product;

public interface ProductDao extends GenericDao<Product>{

	public List<Product> getByFirstLetter(String letter);
	public Optional<Product> getByLowestPrice();
	public List<BigDecimal> getUsaProductsPrice(String countryName);
}