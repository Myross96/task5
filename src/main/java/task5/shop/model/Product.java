package task5.shop.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

	private int id;
	private String name;
	private BigDecimal price;
	private Supplier supplier;
	private Category category;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name, price);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		Product product = (Product) obj;
		return id == product.getId() && name.equals(product.getName())
				&& price.doubleValue() == product.getPrice().doubleValue();
	}
}
