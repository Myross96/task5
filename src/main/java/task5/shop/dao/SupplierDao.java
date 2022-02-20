package task5.shop.dao;

import java.util.List;

import task5.shop.model.Supplier;

public interface SupplierDao extends GenericDao<Supplier>{

	List<Supplier> getByCategoryName(String category);
}
