package uz.app.repository;


import uz.app.enums.Card;
import uz.app.enums.User;
import uz.app.utils.TestConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositary {

    TestConnection testConnection = TestConnection.getInstance();

    public User makeUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setState("start");
        return user;
    }

    public List<User> getUsers(ResultSet resultSet) {
        List<User> users = new ArrayList<>();
        try {
            while (true) {
                if (!resultSet.next()) break;
                User user = makeUser(resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public void save(User user) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("insert into users(state,chat_id) values('%s','%d')",
                    user.getState(),
                    user.getChat_id()
            );
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public boolean addProductInNewBasket(Basket basket, int productId) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("insert into basket(user_id) values('%d')",
                    basket.getUser_id()
            );
            statement.execute(query);
            int basketActive = isBasketActive(basket.getUser_id());
            if (addProductActiveBasket(basketActive,productId)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }



    private static UserRepositary userRepositary;

    public static UserRepositary getInstance() {
        if (userRepositary == null) {
            userRepositary = new UserRepositary();
        }
        return userRepositary;
    }


    public List<Product> getProducts(ResultSet resultSet) {
        ProductRowMapper productRowMapper= new ProductRowMapper();
        List<Product> products = new ArrayList<>();
        try {
            while (true) {
                if (!resultSet.next()) break;
                Product product = productRowMapper.mapRow(resultSet);
                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public List<Product> getProductsInBasket(int userId) {
        Statement statement = testConnection.getStatement();
        try {
            return getProducts(statement.executeQuery(String.format("SELECT p.*\n" +
                    "FROM basket b\n" +
                    "Join basket_product bp ON b.id = bp.backet_id\n" +
                    "JOIN product p ON bp.product_id = p.id\n" +
                    "WHERE b.active = true\n" +
                    "  AND b.user_id = %d;\n;",userId)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void createHistory(int basketActive, Double overAllsumma) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("insert into history(basket_id,overall_amount) values('%d','%f')",
                    basketActive,
                    overAllsumma
            );
            statement.execute(query);
            turnOffActiveBasket(basketActive);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void turnOffActiveBasket(int basketActive) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("Update basket set active = false where id = %d",
                    basketActive
            );
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> getUserById(int userId) {
        Statement statement = testConnection.getStatement();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select * from users where id = '%d';", userId));
            resultSet.next();
//            System.out.println(resultSet.getString("password"));
//            int row = resultSet.getRow();
            User user1 =  makeUser(resultSet);
            return Optional.of(user1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

//    public void minusUserBalanse(int user_id, double v) {
//        Statement statement = testConnection.getStatement();
//        try {
//            String query = String.format("Update users set balance = %f where id = %d",
//                    v,
//                    user_id
//            );
//            statement.execute(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//    public void plusUserBalanse(int user_id, double v) {
//        Statement statement = testConnection.getStatement();
//        try {
//            String query = String.format("Update users set balance = %f where id = %d",
//                    v,
//                    user_id
//            );
//            statement.execute(query);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public List<Product> getProductsInHistory(User user) {
//        Statement statement = testConnection.getStatement();
//        try {
//            return getProducts(statement.executeQuery(String.format("SELECT p.*\n" +
//                    "FROM basket b\n" +
//                    "Join basket_product bp ON b.id = bp.backet_id\n" +
//                    "JOIN product p ON bp.product_id = p.id\n" +
//                    "WHERE b.active = false\n" +
//                    " AND b.user_id = %d;\n;",user.getId())));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }


    public boolean isUserHave(int chatId) {
        Optional<User> optional = getUserById(chatId);
        if (optional.isPresent()) {
            return true;
        }
        return false;
    }

    public void setUpdateState(Long chatId, String string) {


    }

    public void setUserName(Long chatId, String text) {


    }

    public List<Card> getCardsById(Long chatId) {
        try {
            Statement statement = testConnection.getStatement();
            return getCards(statement.executeQuery(String.format("select card.* from card join users on card.user_id = users.id where users.chat_id = %d;",chatId)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Card> getCards(ResultSet resultSet) {
        List<Card> cards = new ArrayList<>();
        try {
            while (true) {
                if (!resultSet.next()) break;
                Card card = makeCard(resultSet);
                cards.add(card);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cards;
    }

    public Card makeCard(ResultSet resultSet) throws SQLException {
        Card card = new Card();
        card.setId(resultSet.getInt("id"));
        card.setNumber(resultSet.getString("number"));
        card.setBalance(resultSet.getDouble("balance"));
        card.setUser_id(resultSet.getInt("user_id"));
        return card;
    }



}
