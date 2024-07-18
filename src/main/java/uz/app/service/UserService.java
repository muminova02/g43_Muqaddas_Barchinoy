package uz.app.service;

import org.example.db.Db;
import org.example.entity.Buyurtma;
import org.example.entity.MenuType;
import org.example.entity.User;
import org.example.enums.AdminState;
import org.example.enums.State;
import org.example.enums.UserState;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.app.enums.User;
import uz.app.repository.UserRepositary;

import java.util.*;

public class UserService {

    private static final Db db = Db.getInstance();
    private final ReplyMarkupService replyMarkupService = new ReplyMarkupService();
    private final InlineMarkupService inlineMarkupService = new InlineMarkupService();
    UserRepositary userRepositary = UserRepositary.getInstance();

    public User getUserById(Long userId){
        return db.getUsers().get(userId);
    }

    public void updateState(Long chatId, State state){
        HashMap<Long, User> users = db.getUsers();
        users.forEach((aLong, user) ->{
            if(Objects.equals(aLong, chatId)){
                user.setState(state);
            }
        });
    }

    public State getState(Long chatId){
        HashMap<Long, User> users = db.getUsers();
        User user1 = users.get(chatId);
       return user1.getState();
    }

    public UserState getUserState(Long chatId){
        HashMap<Long, User> users = db.getUsers();
        User user1 = users.get(chatId);
        return (UserState) user1.getState();
    }

    public AdminState getAdminState(Long chatId){
        HashMap<Long, User> users = db.getUsers();
        User user1 = users.get(chatId);
        return (AdminState) user1.getState();
    }

    public void addBuyurtma(Long chatId, Buyurtma buyurtma) {
        if (!db.getMySavat().containsKey(chatId)) {
            HashMap<Long, ArrayList<Buyurtma>> mySavat = db.getMySavat();
            ArrayList<Buyurtma> buyurtmas = new ArrayList<>();
            buyurtmas.add(buyurtma);
            mySavat.put(chatId,buyurtmas);
        }else {
            db.getMySavat().get(chatId).add(buyurtma);
        }
    }

    public void addOrder(Long chatId, ArrayList<Buyurtma> userSavatToBuyurtma) {
        if (!db.getBuyurtma().containsKey(chatId)) {
            HashMap<Long, ArrayList<Buyurtma>> buyurtma = db.getBuyurtma();
            ArrayList<Buyurtma> orders = new ArrayList<>(userSavatToBuyurtma);
            buyurtma.put(chatId,orders);
        }else {
            db.getBuyurtma().get(chatId).addAll(userSavatToBuyurtma);
        }
    }

    private static UserService userService;

    public static UserService getInstance() {
        if (userService == null) {
            userService=new UserService();
        }
        return userService;
    }

    public void saveUser(User user) {
        userRepositary.save(user);

    }
}
