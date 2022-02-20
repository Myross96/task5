DROP TABLE IF EXISTS categories CASCADE;
CREATE TABLE categories (
	id SERIAL PRIMARY KEY,
	category_name VARCHAR(70) NOT NULL,
 	description VARCHAR(70) NOT NULL
);

DROP TABLE IF EXISTS suppliers CASCADE;
CREATE TABLE suppliers (
	id SERIAL PRIMARY KEY, 
	supplier_name VARCHAR(50) NOT NULL,
	city VARCHAR(50) NOT NULL,
	country VARCHAR(50) NOT NULL
);

DROP TABLE IF EXISTS products CASCADE;
CREATE TABLE products (
	id SERIAL PRIMARY KEY,
	product_name VARCHAR(50) NOT NULL,
	supplier_id INT REFERENCES suppliers(id),
	category_id INT REFERENCES categories(id),
	price money NOT NULL
);

DROP TABLE IF EXISTS supplier_category CASCADE;
CREATE TABLE supplier_category (
	supplier_id INT REFERENCES suppliers(id) ON DELETE CASCADE,
	category_id INT REFERENCES categories(id) ON DELETE CASCADE,
	PRIMARY KEY(supplier_id, category_id)
);