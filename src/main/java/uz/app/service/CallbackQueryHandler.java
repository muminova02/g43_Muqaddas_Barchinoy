package uz.app.service;

//import delivery.Maps;
//import delivery.Steps;
//import model_repo.BasketRepo;
import lombok.RequiredArgsConstructor;
import org.example.db.Db;
import org.example.entity.Buyurtma;
import org.example.entity.MenuType;
import org.example.entity.User;
import org.example.enums.AdminState;
import org.example.enums.BuyurtmaState;
import org.example.enums.UserState;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;

@RequiredArgsConstructor
public class CallbackQueryHandler {

   private static UserService userService = UserService.getInstance();
//  private static BotLogicService botLogicService = BotLogicService.getInstance();
    private static Db db = Db.getInstance();
    public static void handle(CallbackQuery callbackQueryHandler, TelegramLongPollingBot bot) throws TelegramApiException {
        Long chatId1 = callbackQueryHandler.getMessage().getChatId();
        if (callbackQueryHandler.getData().startsWith("+")) {
            incrementQuantity(callbackQueryHandler, bot);
        } else if (callbackQueryHandler.getData().startsWith("-")) {
            decrementQuantity(callbackQueryHandler, bot);
        }
        else if (callbackQueryHandler.getData().equals("ha")){
//            bot.execute(new SendMessage(chatId1.toString(),"Mahsulot photosini kiriting"));
//            UserService.updateState(chatId1, AdminState.SEND_PRODUCT_PHOTO);
          /*  MenuType menuType1=new MenuType();
            MenuType menuType=BotLogicService.getInstance().menuType;
            menuType1.setPhoto(menuType.getPhoto());
            menuType1.setTitle(menuType.getTitle());*/
        }else if (callbackQueryHandler.getData().startsWith("basket")) {
            addBasket(callbackQueryHandler, bot, chatId1);
//            TextHandler.menuButtons(callbackQueryHandler.getMessage(), bot);
//            Maps.USER_STEPS.put(callbackQueryHandler.getMessage().getChatId(), Steps.MENU);
        }
        if (userService.getState(callbackQueryHandler.getMessage().getChatId()).equals(UserState.SEARCH_SAVAT)) {
            deleteBuyurtmaInSavat(callbackQueryHandler, bot, chatId1);
        } else if (callbackQueryHandler.getData().equals(String.valueOf(BuyurtmaState.KUTILMOQADA))) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId1);
            sendMessage.setText("Yuborilgan Buyurtmangiz Kutilmoqda");
            bot.execute(sendMessage);
        } else if (callbackQueryHandler.getData().equals(String.valueOf(BuyurtmaState.CANCELED))) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId1);
            sendMessage.setText("Bazi sabablarga bu ko'ra buyurtma bekor qilindi");
            bot.execute(sendMessage);
        } else if (callbackQueryHandler.getData().equals(String.valueOf(BuyurtmaState.CONFIRMED))) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId1);
            sendMessage.setText("Buyurtma tasdiqlandi, Tez Orada yetib boradi");
            bot.execute(sendMessage);
        } else if(callbackQueryHandler.getData().startsWith("Confirm")){
            String[] split = callbackQueryHandler.getData().split(";");
            Buyurtma buyurtma = db.getBuyurtma(chatId1,split[1]);
            if (buyurtma == null) {
                System.out.println("somthing wrong");
                return;
            }
            buyurtma.setState(BuyurtmaState.CONFIRMED);
            DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId1), callbackQueryHandler.getMessage().getMessageId());
            db.getHistory().add(buyurtma);
            bot.execute(deleteMessage);
        }else if (callbackQueryHandler.getData().startsWith("Canceled")){
            String[] split = callbackQueryHandler.getData().split(";");
            Buyurtma buyurtma = db.getBuyurtma(chatId1,split[1]);
            if (buyurtma == null) {
                System.out.println("somthing wrong");
                return;
            }
            buyurtma.setState(BuyurtmaState.CANCELED);
            DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId1), callbackQueryHandler.getMessage().getMessageId());
            db.getHistory().add(buyurtma);
            bot.execute(deleteMessage);
        }
    }

    private static void deleteBuyurtmaInSavat(CallbackQuery callbackQueryHandler, TelegramLongPollingBot bot, Long chatId1) throws TelegramApiException {
        HashMap<Long, ArrayList<Buyurtma>> mySavat = db.getMySavat();
        ArrayList<Buyurtma> buyurtmas = mySavat.get(chatId1);
        String[] split = callbackQueryHandler.getData().split(";");
        if (split[0].startsWith("delete")) {
            buyurtmas.removeIf(buyurtma -> buyurtma.getProductId().equals(split[1]));
            DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId1), callbackQueryHandler.getMessage().getMessageId());
            bot.execute(deleteMessage);
            userService.updateState(chatId1, UserState.SEARCH_SAVAT);
        }
    }

    private static void addBasket(CallbackQuery callbackQueryHandler, TelegramLongPollingBot bot, Long chatId1) throws TelegramApiException {
        String[] split = callbackQueryHandler.getData().split(";");
        Buyurtma buyurtma1 = BotLogicService.getInstance().buyurtma;
        Buyurtma buyurtma = new Buyurtma();
        User user = userService.getUserById(chatId1);
        String[] mealById = db.getMealById(split[1]);
        buyurtma.setName(user.getName());
        buyurtma.setPhone(user.getPhoneNumber());
        buyurtma.setOwnerChatId(user.getChatId());
        buyurtma.setPrice(Double.parseDouble(mealById[2]));
        buyurtma.setMealName(mealById[1]);
        buyurtma.setMenuType(mealById[0]);
        buyurtma.setCount(Integer.parseInt(split[2]));
        buyurtma.setState(BuyurtmaState.SAVATDA);
        userService.addBuyurtma(chatId1,buyurtma);
        buyurtma1.clear();
        bot.execute(new SendMessage(chatId1.toString(), "Successfully added ✅"));
        userService.updateState(chatId1, UserState.CHOOSE_MEAL);
    }

    private static void incrementQuantity(CallbackQuery callbackQuery, TelegramLongPollingBot bot) throws TelegramApiException {
        String[] split = callbackQuery.getData().split(";");
        String buyurtmaId = null;
        long l;
        try {
           l = Long.parseLong(split[2]);
        }catch (Exception e){
            buyurtmaId =split[2];
        }
        if (buyurtmaId !=null){
            Buyurtma buyurtma = db.getBuyurtmaInSavat(callbackQuery.getMessage().getChatId(),split[2]);
            buyurtma.setCount(buyurtma.getCount()+1);
            InlineKeyboardMarkup inlineKeyboardMarkup = CommandHandler.buyurtmaMurkup(Integer.parseInt(split[1]) + 1
                    , buyurtmaId);
            EditMessageReplyMarkup build = EditMessageReplyMarkup.builder()
                    .replyMarkup(inlineKeyboardMarkup)
                    .chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .build();
            bot.execute(build);
            return;
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = CommandHandler.productMurkup(Integer.parseInt(split[1]) + 1
                , Long.parseLong(split[2]));
        EditMessageReplyMarkup build = EditMessageReplyMarkup.builder()
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .build();
        bot.execute(build);
    }

    private static void decrementQuantity(CallbackQuery callbackQuery, TelegramLongPollingBot bot) throws TelegramApiException {
        String[] split = callbackQuery.getData().split(";");
        String buyurtmaId = null;
        long l;
        try {
            l = Long.parseLong(split[2]);
        }catch (Exception e){
            buyurtmaId =split[2];
        }
        if (!split[1].equals("0")) {
        if (buyurtmaId !=null){
            Buyurtma buyurtma = db.getBuyurtmaInSavat(callbackQuery.getMessage().getChatId(),split[2]);
            buyurtma.setCount(buyurtma.getCount()-1);
            InlineKeyboardMarkup inlineKeyboardMarkup = CommandHandler.buyurtmaMurkup(Integer.parseInt(split[1]) - 1
                    , buyurtmaId);
            EditMessageReplyMarkup build = EditMessageReplyMarkup.builder()
                    .replyMarkup(inlineKeyboardMarkup)
                    .chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .build();
            bot.execute(build);
            return;
        }
            InlineKeyboardMarkup inlineKeyboardMarkup = CommandHandler.productMurkup(Integer.parseInt(split[1]) - 1
                    , Long.parseLong(split[2]));

            EditMessageReplyMarkup build = EditMessageReplyMarkup.builder()
                    .replyMarkup(inlineKeyboardMarkup)
                    .chatId(callbackQuery.getMessage().getChatId())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .build();
            bot.execute(build);

        }
    }
}
