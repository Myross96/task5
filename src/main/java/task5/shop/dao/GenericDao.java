package task5.shop.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T> {

	public void create(T entity);
	public Optional<T> getById(int id);
	public List<T> getAll();
	public void update(T entity);
	public void delete(T entity);
}
