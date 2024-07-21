package uz.app.service;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.app.enums.Card;
import uz.app.enums.User;
import uz.app.enums.UserState;
import uz.app.repository.UserRepositary;
import uz.app.utils.Utils;

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
                tansfer_deposit(chatId);
//                userServise.updateState(chatId,);
//                shu yerda state ni o'zgartirib calbackda cardni ushlab oladi,
//                keyin transferdagi db ga bittasini saqlaydi, keyin ikkinchini shu pasda saqlaymiz keyin transfer,

            }
            case Utils.AGREE -> {
                userServise.TransferAmount(chatId);
                sendMessage.setText("O'kazma muvaffaqiyatli amalga oshirildi");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.MENU));
                botService.executeMessages(sendMessage);
                userRepositary.setTransferCanceled(chatId);
            }
            case Utils.DEPOSIT ->{
                tansfer_deposit(chatId);
//                userServise.updateState(chatId,);
//                shu yerda state ni o'zgartirib calbackda cardni ushlab oladi,
//                keyin summani kiriting deymiz, kiritadi, o'sha summani o'sha kartaga qo'shamiz,
//                botService.executeMessages(sendMessage);
//                userServise.updateState(chatId,UserState.MAIN_MENU);
            }
            case Utils.HISTORY -> {

//
            }
            case Utils.CANCEL -> {
                userRepositary.setTransferCanceled(chatId);
                sendMessage.setText("canseled 👌");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.MENU));
                botService.executeMessages(sendMessage);
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

    private void tansfer_deposit(Long chatId) {
        List<Card> cards = userServise.showCards(chatId);
        if (cards.isEmpty()) {
            sendMessage.setText("cards not yet");
            botService.executeMessages(sendMessage);
            return;
        }
        sendMessage.setText("choose card number: ");
        sendMessage.setReplyMarkup(inlineService.inlineMarkup(cards));
        botService.executeMessages(sendMessage);
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
                boolean b = userServise.setTransferAmount(chatId, text);
                if (!b){
                    sendMessage.setText("Mabla'ingizni tekshirib ko'ring: ");
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.MENU));
                    botService.executeMessages(sendMessage);
                    return;
                }
                String  inform = userRepositary.getUserActiveTransferInform(chatId);
                sendMessage.setText("Siz kiritgan ma'lumotlar: \n" + inform );
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.TRANSFER_AGREE));
                botService.executeMessages(sendMessage);
            }
            case DEPOSIT_AMOUNT -> {
                userServise.depositAmountInCard(chatId,text);
                sendMessage.setText("pul Kiritildi");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.MENU));
                botService.executeMessages(sendMessage);
            }
            default -> {
                sendMessage.setText("Menuni tanlang");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.MENU));
                botService.executeMessages(sendMessage);
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
