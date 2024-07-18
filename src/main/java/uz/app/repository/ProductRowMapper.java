/*
package uz.app.repository;

import uz.app.entity.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product>{
    @Override
    public Product mapRow(ResultSet resultSet) throws SQLException {
        Product products = new Product();
        products.setId(resultSet.getInt("id"));
        products.setName(resultSet.getString("name"));
        products.setActive(resultSet.getBoolean("active"));
        products.setDescription(resultSet.getString("description"));
        products.setPrice(resultSet.getDouble("price"));
        products.setCategory_id(resultSet.getInt("category_id"));
        return products;
    }
}
*/
