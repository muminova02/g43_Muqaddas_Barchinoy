package uz.app.repository;

import uz.app.entity.*;
import uz.app.utils.TestConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthRepository {
    TestConnection testConnection = TestConnection.getInstance();


    public void save(User user) {
        Statement statement = testConnection.getStatement();
        try {
            String query = String.format("insert into users(name,email,password,smscode) values('%s','%s','%s','%s')",
                    user.getName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getSmsCode()
                    );
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<User> getAllUsers() {
        try {
            Statement statement = testConnection.getStatement();
            return getUsers(statement.executeQuery(String.format("select * from users;")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Optional<User> getByEmail(String email) {
        Statement statement = testConnection.getStatement();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("select * from users where email = '%s';", email));

            resultSet.next();
            System.out.println(resultSet.getString("password"));
            int row = resultSet.getRow();
            User user1 = makeUser(resultSet);
            return Optional.of(user1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
//        return users.stream().filter(user -> user.getEmail().equals(email)).findFirst();

    }

    public User makeUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setPhone(resultSet.getString("phone_number"));
        user.setRole(resultSet.getString("role"));
        user.setEnabled(resultSet.getBoolean("enabled"));
        user.setBalance(resultSet.getDouble("balance"));
        user.setSmsCode(resultSet.getString("smscode"));
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

    public void setActive(int id) throws SQLException {
        Statement statement = testConnection.getStatement();
        statement.execute(String.format("Update users SET enabled = true where id = %d",id));
    }

    private static AuthRepository instance;

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }
}
