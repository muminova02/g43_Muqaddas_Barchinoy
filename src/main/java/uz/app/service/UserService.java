package uz.app.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.app.enums.Card;
import uz.app.enums.User;
import uz.app.enums.UserState;
import uz.app.repository.UserRepositary;

import java.util.*;

public class UserService {

    private final ReplyMarkupService replyMarkupService = new ReplyMarkupService();
    private final InlineMarkupService inlineMarkupService = new InlineMarkupService();
    UserRepositary userRepositary = UserRepositary.getInstance();

//    public User getUserById(Long userId){
//        return db.getUsers().get(userId);
//    }

    public void updateState(Long chatId, UserState state){
        userRepositary.setUpdateState(chatId,state.toString());
    }

    public UserState getState(Long chatId){
//        HashMap<Long, User> users = db.getUsers();
//        User user1 = users.get(chatId);
//       return user1.getState();
        return null;
    }



    public void addCardForUser(Long chatId, Card card) {
        Integer userIdInDb = userRepositary.getUserIdByChatid(Math.toIntExact(chatId));
        card.setUser_id(userIdInDb);
        userRepositary.addCard(card);
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

    public List<Card> showCards(Long chatId) {
        List<Card> cards = userRepositary.getCardsById(chatId);
        return cards;
    }

    public boolean setTransferAmount(Long chatId, String text) {
        Integer userIdByChatid = userRepositary.getUserIdByChatid(Math.toIntExact(chatId));
        boolean b = userRepositary.checkBalanseUserInTransfer(chatId, text);
        if (b){
            userRepositary.setTransferAmount(userIdByChatid,text);
            return true;
        }
        else return false;
    }

    public void TransferAmount(Long chatId) {
        String[] transferActivebyUser = userRepositary.getTransferActivebyUser(chatId);
        String user_id = transferActivebyUser[0];
        Double amount = Double.parseDouble(transferActivebyUser[3]);
        String card1 = transferActivebyUser[1];
        String card2 = transferActivebyUser[2];
        userRepositary.transferBetCards(Integer.valueOf(user_id),card1,card2,amount);
    }
}
