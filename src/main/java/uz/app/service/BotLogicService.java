package uz.app.service;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.app.enums.Card;
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
                }
            }
            case Utils.SHOW_CARDS-> {
                List<Card> cards = userServise.showCards(chatId);
                if (cards.isEmpty()) {
                    sendMessage.setText("cards not yet");
                    botService.executeMessages(sendMessage);
                    return;
                }
                sendMessage.setText("your cards: ");
                sendMessage.setReplyMarkup(inlineService.inlineMarkup(cards));
                botService.executeMessages(sendMessage);
                sendMessage.setText("a");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.MENU));
                botService.executeMessages(sendMessage);

                // keyin calbackda kartani bosganda karta haqida ma'lumotlar chiqsin;
//                userServise.updateState(chatId,UserState.CHOOSE_MENU);
            }
            case Utils.ADD_CARD -> {
                sendMessage.setText("write your new card number: ");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.MENU));
                botService.executeMessages(sendMessage);
                userServise.updateState(chatId,UserState.ADD_CARD_NUMBER);
            }
            case Utils.TRANSFER-> {
                List<Card> cards = userServise.showCards(chatId);
                if (cards.isEmpty()) {
                    sendMessage.setText("cards not yet");
                    botService.executeMessages(sendMessage);
                    return;
                }
                sendMessage.setText("your card number: ");
                sendMessage.setReplyMarkup(inlineService.inlineMarkup(cards));
                botService.executeMessages(sendMessage);
//                userServise.updateState(chatId,);
//                shu yerda state ni o'zgartirib calbackda cardni ushlab oladi,
//                keyin transferdagi db ga bittasini saqlaydi, keyin ikkinchini shu pasda saqlaymiz keyin transfer,

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
                sendMessage.setText("Iltimos ismingizni kiriting.");
                userServise.updateState(chatId,UserState.NAME);
                botService.executeMessages(sendMessage);
            }
            case NAME -> {
                  userRepositary.setUserName(chatId,text);
                  userServise.updateState(chatId,UserState.MAIN_MENU);
                  sendMessage.setText("Welcome Menu");
                  sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.MENU));
                  botService.executeMessages(sendMessage);
            }
            case ADD_CARD_NUMBER ->{
                Card card = new Card();
                card.setNumber(text);
                card.setBalance((double) 0);
                userServise.addCardForUser(chatId,card);
                sendMessage.setText("Card qo'shildi");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.MENU));
                userServise.updateState(chatId,UserState.MAIN_MENU);
                botService.executeMessages(sendMessage);
            }
            case TRANSFER_CARD_2 -> {
                userRepositary.setTransferSecondCardNumber(chatId,text);
                sendMessage.setText("O'tkazmoqchi bo'lgan summani kiriting: ");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(null));
                botService.executeMessages(sendMessage);
                userServise.updateState(chatId,UserState.TRANSFER_AMOUNT);
            }
            case TRANSFER_AMOUNT -> {
                userServise.setTransferAmount(chatId,text);
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
