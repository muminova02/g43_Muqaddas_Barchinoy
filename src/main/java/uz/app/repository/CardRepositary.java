package uz.app.repository;

import uz.app.enums.Card;
import uz.app.utils.TestConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class CardRepositary {

    TestConnection testConnection = TestConnection.getInstance();

//    public <T> List<T> getAllThings(Class<T> clazz, String tableName, RowMapper<T> rowMapper) {
//        try {
//            Statement statement = testConnection.getStatement();
//            ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM %s;", tableName));
//            return mapResults(resultSet, rowMapper);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }


//    private <T> List<T> mapResults(ResultSet resultSet, RowMapper<T> rowMapper) throws SQLException {
//        List<T> results = new ArrayList<>();
//        while (resultSet.next()) {
//            T rowObject = rowMapper.mapRow(resultSet);
//            results.add(rowObject);
//        }
//        return results;
//    }

    public Card makeCard(ResultSet resultSet) throws SQLException {
        Card card = new Card();
        card.setId(resultSet.getInt("id"));
        card.setNumber(resultSet.getString("number"));
        card.setBalance(resultSet.getDouble("balance"));
        card.setUser_id(resultSet.getInt("user_id"));
        return card;
    }



    public Optional<Card> getCardsById(String numaber ){
        Statement statement = testConnection.getStatement();

        ResultSet resultSet = null;
        try {
            String format = String.format("select * from card where user_id = '%d' or number = '%s' ;", numaber, numaber);
            resultSet = statement.executeQuery(format);
            resultSet.next();
            Card card =  makeCard(resultSet);
            return Optional.of(card);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


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





    private static CardRepositary cardRepositary;

    public static CardRepositary getInstance() {
        if (cardRepositary == null) {
            cardRepositary = new CardRepositary();
        }
        return cardRepositary;
    }
}
