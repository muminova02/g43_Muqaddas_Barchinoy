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


    private static UserRepositary userRepositary;

    public static UserRepositary getInstance() {
        if (userRepositary == null) {
            userRepositary = new UserRepositary();
        }
        return userRepositary;
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
    public List<Card> getCarsById(Long id) throws SQLException {
        Statement statement = testConnection.getStatement();

        ResultSet resultSet = statement.executeQuery(String.format("select * from card where id = '%d';",id));
        return List.of();
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

    public Integer getUserIdByChatid(int chat_id) {
        Statement statement = testConnection.getStatement();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select * from users where chat_id = '%d';", chat_id));
            resultSet.next();
            return resultSet.getInt("chat_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void setUpdateState(Long chatId, String string) {
        Statement statement = testConnection.getStatement();

        try {
            String format = String.format("update users  set  state = '%s' where chat_id = '%d';", string, chatId);
            statement.execute(format);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void setUserName(Long chatId, String text) {

        Statement statement = testConnection.getStatement();

        String format = String.format(" UPDATE users SET name = %s WHERE chatId = %s",text,chatId);
        try {
            statement.execute(format);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean addCard(Card card) {
        Statement statement = testConnection.getStatement();
        String query = String.format("INSERT INTO card(number,balance,user_id) values('%s','%f','%d',)",card.getNumber(),card.getBalance(),card.getUser_id());
        try {
            statement.execute(query);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean isUserHave(int chatId) {
        Optional<User> optional = getUserById(chatId);
        if (optional.isPresent()) {
            return true;
        }
        return false;
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


    public void setTransferSecondCardNumber(Long chatId, String text) {
        int id = getUserIdByChatid(Math.toIntExact(chatId));
        Statement statement = testConnection.getStatement();
        String query = String.format("update transfer set to_card = '%s' where user_id = '%d';",text,id);
        try {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkBalanseUserInTransfer(Long chatId, String text) {
        Integer userId = getUserIdByChatid(Math.toIntExact(chatId));
        Statement statement = testConnection.getStatement();
        String from_card;
        Double balance = 0d;
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select from_card from transfer where user_id = '%d'", userId));
            resultSet.next();
            from_card=resultSet.getString("from_card");
            Statement statement1 = testConnection.getStatement();
            ResultSet resultSet1 = statement1.executeQuery(String.format("select balance from card where number = '%s'",from_card));
            resultSet1.next();
            balance = resultSet1.getDouble("balance");
            if (balance<Double.valueOf(text)){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;


    }

    public void setTransferAmount(Integer userIdByChatid, String text) {
        Statement statement = testConnection.getStatement();
        String query = String.format("update transfer set amount = %f where user_id = %d and active = true", Double.valueOf(text), userIdByChatid);
        try {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUserActiveTransferInform(Long chatId) {
        Integer user_id = getUserIdByChatid(Math.toIntExact(chatId));
        Statement statement = testConnection.getStatement();
        String query = String.format("select * from transfer where user_id = %d and active = true", user_id);
        try {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("qaysi kartadan: ");
            stringBuilder.append(resultSet.getString("from_card"));
            stringBuilder.append("\nshu kartaga: ");
            stringBuilder.append(resultSet.getString("to_card"));
            stringBuilder.append("\no'tkazilayotgan summa: ");
            stringBuilder.append(resultSet.getDouble("balance"));
            return stringBuilder.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "mavjud emas;";
    }
    public String[] getTransferActivebyUser(Long chatId){
        Integer user_id = getUserIdByChatid(Math.toIntExact(chatId));
        Statement statement = testConnection.getStatement();
        String query = String.format("select * from transfer where user_id = '%d' and active = true", user_id);
        try {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            String[] strings = new String[4];
            strings[0] = String.valueOf(resultSet.getInt("user_id"));
            strings[1] = resultSet.getString("from_card");
            strings[2] = resultSet.getString("to_card");
            strings[3] = String.valueOf(resultSet.getDouble("balance"));
            return strings;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setTransferCanceled(Long chatId) {
        Statement statement = testConnection.getStatement();
        Integer user_id = getUserIdByChatid(Math.toIntExact(chatId));
        String query = String.format("update transfer set active = false where user_id = '%d'", user_id);
        try {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transferBetCards(Integer integer, String card1, String card2, Double amount) {
        Statement statement = testConnection.getStatement();
        String query = String.format("Update card set balance = balance - %f where user_id = %d and number = '%s'", amount, integer, card1);
        try {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query2 = String.format("UPDATE card set balance = balance + %f where number = '%s'", amount, card2);
        try {
            statement.execute(query2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void depositAmount(Integer userId, String number, String text) {
        Statement statement = testConnection.getStatement();
        String query = String.format("UPDATE card set balance = balance + %f where user_id = %d and number = '%s'", Double.valueOf(text), userId, number);
        try {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
