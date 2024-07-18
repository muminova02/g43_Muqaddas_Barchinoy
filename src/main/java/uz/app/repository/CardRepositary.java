package uz.app.repository;

import uz.app.enums.Card;
import uz.app.utils.TestConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class CardRepositary {

    TestConnection testConnection = TestConnection.getInstance();

    public <T> List<T> getAllThings(Class<T> clazz, String tableName, RowMapper<T> rowMapper) {
        try {
            Statement statement = testConnection.getStatement();
            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM %s;", tableName));
            return mapResults(resultSet, rowMapper);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    private <T> List<T> mapResults(ResultSet resultSet, RowMapper<T> rowMapper) throws SQLException {
        List<T> results = new ArrayList<>();
        while (resultSet.next()) {
            T rowObject = rowMapper.mapRow(resultSet);
            results.add(rowObject);
        }
        return results;
    }



//    public List<Category> getAllCategory() {
//        try {
//            Statement statement = testConnection.getStatement();
//            return getCategoryies(statement.executeQuery(String.format("select * from users;")));
//        } catch (
//        SQLException e) {
//        e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }
//
//
//
//    private List<Category> getCategoryies(ResultSet resultSet) {
//        List<Category> categories = new ArrayList<>();
//        try {
//            while (true) {
//                if (!resultSet.next()) break;
//                Category category = makeCategory(resultSet);
//                categories.add(category);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return categories;
//    }


    public void saveCard(Card card) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("insert into card(number,balance,user_id) values('%s','%f','%d')",
                    card.getNumber(),
                    card.getBalance(),
                    card.getUser_id()
            );
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void disableProduct(Integer product_id) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("Update product set active = false where id = %d",
                    product_id
            );
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private static CardRepositary cardRepositary;

    public static CardRepositary getInstance() {
        if (cardRepositary == null) {
            cardRepositary = new CardRepositary();
        }
        return cardRepositary;
    }
}
