package task5.shop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Supplier {

	private int id;
	private String name;
	private String city;
	private String country;
	List<Category> categories = new ArrayList<>();

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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name, city, country);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		Supplier supplier = (Supplier) obj;
		return id == supplier.getId() && name.equals(supplier.getName())
				&& city.equals(supplier.getCity()) && country.equals(supplier.getCountry());
	}
}
