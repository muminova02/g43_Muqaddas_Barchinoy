package uz.app.service;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.app.enums.User;
import uz.app.enums.UserState;
import uz.app.repository.UserRepositary;
import uz.app.utils.Utils;

import java.io.File;

import java.util.List;

public class BotLogicService {
    private final UserService userServise = UserService.getInstance();
    UserRepositary userRepositary = UserRepositary.getInstance();
    private final CardService cardService = CardService.getInstance();
    private final SendMessage sendMessage = new SendMessage();
    private BotService botService = BotService.getInstance();
    private final User currentUser =new User();
    private final ReplyMarkupService replyService = new ReplyMarkupService();
    private final InlineMarkupService inlineService = new InlineMarkupService();

    public void messageHandler(Update update){
        Long chatId = update.getMessage().getChatId();
//        if (chatId == 6436944940L ){
////            userServise.updateState(chatId, AdminState.ADMIN_START);
////                    userStateHandler(update);
//            adminStateHandler(chatId,update);
//            return;
//        }
        String text = update.getMessage().getText();
        System.out.println(chatId);
        User currentUser1;
        sendMessage.setReplyMarkup(null);
        sendMessage.setChatId(chatId);

        switch (text){
            case "/start" -> {
                if (!userRepositary.isUserHave(chatId.intValue())) {
                    User user = new User();
                    user.setChat_id(chatId.intValue());
                    user.setState(String.valueOf(UserState.START));
                    userServise.saveUser(user);
                    userStateHandler(update);
//                   userServise.updateState(chatId,UserState.START);
                }
            }
            case Utils.SHOW_CARDS-> {
//                sendMessage.setText("choose menu type:");
//                sendMessage.setReplyMarkup(replyService.keyboardMaker(db.getAllMeals().keySet()));
//                botService.executeMessages(sendMessage);
//                userServise.updateState(chatId,UserState.CHOOSE_MENU);
            }
            case Utils.ADD_CARD -> {
//                userServise.updateState(chatId,UserState.MAIN_MENU);
            }
            case Utils.TRANSFER-> {
//
//                botService.executeMessages(sendMessage1);
//                userServise.updateState(chatId,UserState.SEARCH_SAVAT);
            }
            case Utils.DEPOSIT ->{
//                botService.executeMessages(sendMessage);
//                userServise.updateState(chatId,UserState.MAIN_MENU);
            }
            case Utils.HISTORY -> {
//                sendMessage.setText("Agar biror muammo tug'ilgan bo'lsa +998901234567 yoki" +
//                        " +998912345678 shu raqamlarga murojat qilishingiz mumkin");
//                botService.executeMessages(sendMessage);
            }
            case "orqaga"-> {
                System.out.println("orqaga");
                botService.executeMessages(sendMessage);
                userServise.updateState(chatId,UserState.MAIN_MENU);
            }
            default -> {
                userStateHandler(update);
            }
        }
    }

    public void userStateHandler(Update update){
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        System.out.println(chatId);
        sendMessage.setReplyMarkup(null);
        sendMessage.setChatId(chatId);
        UserState state = userServise.getState(chatId);

        switch (state){
            case START -> {
//                sendMessage.setText("Iltimos ismingizni kiriting.");
//                userServise.updateState(chatId,UserState.NAME);
//                botService.executeMessages(sendMessage);
            }
            case NAME -> {
//                User user = db.getUsers().get(chatId);
//                user.setName(text);
//                userServise.updateState(chatId,UserState.PHONE_NUMBER);
//                sendMessage.setText("Telfon Raqamingizni Kiriting.");
//                botService.executeMessages(sendMessage);
            }
            case WRITING -> {
//                xabar.setDesc(text);
//                xabar.setChatId(chatId);
//                db.setXabar(chatId,xabar);
//                sendMessage.setText("Xabaringiz qabul qilindi tez orada siz bilan bog'lanamiz ;)");
//                botService.executeMessages(sendMessage);
            }
            default -> {
//                sendMessage.setText("Menuni tanlang");
//                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.mainMenuUser));
//                botService.executeMessages(sendMessage);
            }
        }



    }


    private static BotLogicService botLogicService;

    public static BotLogicService getInstance() {
        if (botLogicService == null) {
            botLogicService = new BotLogicService();
//            botLogicService.admins.put(6436944940l,"main");

        }
        return botLogicService;
    }
}
