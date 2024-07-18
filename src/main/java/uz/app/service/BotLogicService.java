package uz.app.service;

import org.example.db.Db;
import org.example.entity.Buyurtma;
import org.example.entity.Meal;
import org.example.entity.MenuType;
import org.example.entity.User;
import org.example.entity.Xabar;
import org.example.enums.AdminState;
import org.example.enums.BuyurtmaState;
import org.example.enums.UserState;
import org.example.payload.InlineString;
import org.example.util.Utils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BotLogicService {
    private final UserService userServise = UserService.getInstance();
    private final AdminService adminService = AdminService.getInstance();
    public final Buyurtma buyurtma = new Buyurtma();
    public final MenuType menuType = new MenuType();
    public final Meal meal = new Meal();
    private final SendMessage sendMessage = new SendMessage();
    private final SendMessage sendMessageToAdmin = new SendMessage();
    private Db db = Db.getInstance();
    private BotService botService = BotService.getInstance();
    private final User currentUser =new User();
    private final Xabar xabar=new Xabar();
    private final ReplyMarkupService replyService = new ReplyMarkupService();
    private final InlineMarkupService inlineService = new InlineMarkupService();

    public void messageHandler(Update update){
        Long chatId = update.getMessage().getChatId();
        if (chatId == 6436944940L ){
//            userServise.updateState(chatId, AdminState.ADMIN_START);
//                    userStateHandler(update);
            adminStateHandler(chatId,update);
            return;
        }
        String text = update.getMessage().getText();
        System.out.println(chatId);
        User currentUser1;
        sendMessage.setReplyMarkup(null);
        sendMessage.setChatId(chatId);

        switch (text){
            case "/start" -> {
                if (!db.getUsers().containsKey(chatId)) {
                    User user = new User();
                    user.setChatId(chatId);
                    user.setState(UserState.START);
                    db.getUsers().put(chatId,user);
                    userStateHandler(update);
//                   userServise.updateState(chatId,UserState.START);
                }
            }
            case Utils.SHOW_MENU-> {
                sendMessage.setText("choose menu type:");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(db.getAllMeals().keySet()));
                botService.executeMessages(sendMessage);
                userServise.updateState(chatId,UserState.CHOOSE_MENU);
            }
            case Utils.MY_ORDERS -> {
                ArrayList<Buyurtma> buyurtmas = db.getBuyurtma().get(chatId);
                if (buyurtmas.isEmpty()) {
                    sendMessage.setText("Buyurtma Mavjud Emas");
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.order_menu));
                    botService.executeMessages(sendMessage);
                    return;
                }
                int i=1;
                for (Buyurtma buyurtma : buyurtmas) {
                    StringBuilder ordersOneUser = new StringBuilder();
                    User user = db.getUsers().get(chatId);
                    ordersOneUser.append(user.getName()).append("\n");
                    ordersOneUser.append(user.getPhoneNumber()).append("\n");
//                    ordersOneUser.append(buyurtma.getProductId()).append("\n Meal name: ");
                    ordersOneUser.append(i++).append("   -> Menu: ");
                    ordersOneUser.append(buyurtma.getMenuType()).append("\n Meal name: ");
                    ordersOneUser.append(buyurtma.getMealName()).append("\n Miqdori: ");
                    ordersOneUser.append(buyurtma.getCount()).append("\n Narxi: ");
                    ordersOneUser.append(buyurtma.getPrice()).append("  sum\n");
                    sendMessage.setText(ordersOneUser.toString());
                    sendMessage.setReplyMarkup(CommandHandler.orderInline(String.valueOf(buyurtma.getState())));
                    botService.executeMessages(sendMessage);
                }
                SendMessage sendMessage1 = new SendMessage();
                sendMessage1.setChatId(chatId);
                sendMessage1.setText("Your orders");
                sendMessage1.setReplyMarkup(replyService.keyboardMaker(Utils.order_menu));
                botService.executeMessages(sendMessage1);
                userServise.updateState(chatId,UserState.MAIN_MENU);
            }
            case Utils.SAVAT -> {
                for (Buyurtma buyurtma1 : db.getMySavat().get(chatId)) {
                    sendMessage.setText(buyurtma1.toString());
                    sendMessage.setReplyMarkup(CommandHandler.buyurtmaMurkup(buyurtma1.getCount(),buyurtma1.getProductId()));
                    botService.executeMessages(sendMessage);
                }
                SendMessage sendMessage1 = new SendMessage();
                sendMessage1.setChatId(chatId);
                sendMessage1.setText("Buyurtmani bekor qilish uchun '   -   ' tugmasini bosing");
                sendMessage1.setReplyMarkup(replyService.keyboardMaker(Utils.savatMenu));
                botService.executeMessages(sendMessage1);
                userServise.updateState(chatId,UserState.SEARCH_SAVAT);
            }
            case Utils.BUYURTMA ->{
                ArrayList<Buyurtma> userSavatToBuyurtma = db.getMySavat().get(chatId);
                userSavatToBuyurtma.forEach(buyurtma1 -> buyurtma1.setState(BuyurtmaState.KUTILMOQADA));
                userServise.addOrder(chatId,userSavatToBuyurtma);
                db.getMySavat().get(chatId).clear();
                sendMessage.setText("Buyurtma jo'natildi");
                botService.executeMessages(sendMessage);
                userServise.updateState(chatId,UserState.MAIN_MENU);
            }
            case Utils.ALOQA -> {
                sendMessage.setText("Agar biror muammo tug'ilgan bo'lsa +998901234567 yoki" +
                        " +998912345678 shu raqamlarga murojat qilishingiz mumkin");
                botService.executeMessages(sendMessage);
            }
            case Utils.XABAR_YUBORISH -> {
                sendMessage.setText("Xabaringizni yozing : ");
                botService.executeMessages(sendMessage);
                userServise.updateState(chatId,UserState.WRITING);

            }
            case Utils.SOZLAMALAR -> {
               sendMessage.setText("Sozlamar bo'limiga xush kelibsiz ");
               sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.sozlamalarMenu));
               botService.executeMessages(sendMessage);
            }
            case Utils.BIZ_HAQIMIZDA -> {
                sendMessage.setText("Restaranimizning birinchi filiali 2006 yilda ochilgan bo’lib, shu kungacha muvaffaqiyatli faoliyat yuritib kelmoqdaligini bilarmidingiz? \n" +
                        "15 yil davomida kompaniya avtobus bekatidagi kichik ovqatlanish joyidan zamonaviy, kengaytirilgan tarmoqqa aylandi, u bugungi kunda O‘zbekiston bo‘ylab 60 dan ortiq restoranlarni, o‘zining eng tezkor yetkazib berish xizmatini, zamonaviy IT-infratuzilmasini va 2000 dan ortiq xodimlarni o‘z ichiga oladi.");
                    botService.executeMessages(sendMessage);
            }
            case "orqaga"->{
                if (userServise.getUserState(chatId).equals(UserState.CHOOSE_MEAL)){
                    sendMessage.setText("choose menu type:");
                    sendMessage.setReplyMarkup(replyService.keyboardMaker(db.getAllMeals().keySet()));
                    botService.executeMessages(sendMessage);
                    userServise.updateState(chatId,UserState.CHOOSE_MENU);
                    return;
                }
                sendMessage.setText("main menu");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.mainMenuUser));
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
//        User currentUser1 = db.getUsers().get(chatId);
        sendMessage.setReplyMarkup(null);
        sendMessage.setChatId(chatId);
        UserState state = userServise.getUserState(chatId);

        switch (state){
            case START -> {
                sendMessage.setText("Iltimos ismingizni kiriting.");
                userServise.updateState(chatId,UserState.FIRST_NAME);
                botService.executeMessages(sendMessage);
            }
            case FIRST_NAME -> {
                User user = db.getUsers().get(chatId);
                user.setName(text);
                userServise.updateState(chatId,UserState.PHONE_NUMBER);
                sendMessage.setText("Telfon Raqamingizni Kiriting.");
                botService.executeMessages(sendMessage);
            }
            case PHONE_NUMBER -> {
                User user = db.getUsers().get(chatId);
                String s = validatePhoneNumber(text);
                if (s!=null){
                    sendMessage.setText(s);
                    botService.executeMessages(sendMessage);
                return;
                }
                user.setPhoneNumber(text);
                userServise.updateState(chatId,UserState.MAIN_MENU);

                db.getUsers().forEach((aLong, user1) ->
                        System.out.println(user1));
                sendMessage.setText("Buyurtma tanlashingiz mumkin 😊");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.mainMenuUser));
                botService.executeMessages(sendMessage);
            }
            case CHOOSE_MENU -> {
                db.getAllMeals().forEach((menuType, meals) -> {
                    if (menuType.getTitle().equals(text)){
                        buyurtma.setMenuType(text);
                        buyurtma.setState(BuyurtmaState.CHALA);
                        if (menuType.getPhoto().startsWith("photo")){
                            sendMessage.setText(menuType.getTitle());
                            sendMessage.setReplyMarkup(replyService.keyboardMaker(meals));
                            botService.executeMessages(sendMessage);
                            userServise.updateState(chatId,UserState.CHOOSE_MEAL);
                            return;
                        }
                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setChatId(chatId);
                        if (menuType.getPhoto().startsWith("src")){
                            sendPhoto.setPhoto(new InputFile(new File(menuType.getPhoto())));
                        }else {
                            String[] split = menuType.getPhoto().split(";");
                            sendPhoto.setPhoto(new InputFile(split[0]));
                        }
                        sendPhoto.setCaption(menuType.getTitle()+"\nchoose Meal : ");
                        sendPhoto.setReplyMarkup(replyService.keyboardMaker(meals));
                        botService.executeMessages(sendPhoto);
                        userServise.updateState(chatId,UserState.CHOOSE_MEAL);
                    }
                });
            }
            case CHOOSE_MEAL -> {
                db.getAllMeals().forEach((menuType, meals) -> {
                    if(buyurtma.getMenuType().equals(menuType.getTitle())){
                        for (Meal meal : meals) {
                            if (meal.getTitle().equals(text)){
                                if (meal.getPhoto().startsWith("photo")){
                                    sendMessage.setText("Photo: " + meal.getPhoto()+ "\nname:"+meal.getTitle()+"\nDescription: "+ meal.getDescription()
                                            +"\nPrice: " + meal.getPrice());
                                    sendMessage.setReplyMarkup(CommandHandler.productMurkup(1,Long.parseLong(meal.getId())));
                                    botService.executeMessages(sendMessage);
                                    userServise.updateState(chatId,UserState.CHOOSE_MEAL);
                                    return;
                                }
                                SendPhoto sendPhoto = new SendPhoto();
                                sendPhoto.setChatId(chatId);
                                if(meal.getPhoto().startsWith("src")){
                                    sendPhoto.setPhoto(new InputFile(new File(meal.getPhoto())));
                                }else {
                                    String[] split = meal.getPhoto().split(";");
                                    sendPhoto.setPhoto(new InputFile(split[0]));
                                }
                                sendPhoto.setCaption("name:"+meal.getTitle()+"\nDescription: "+ meal.getDescription()
                                        +"\nPrice: " + meal.getPrice());
                                sendPhoto.setReplyMarkup(CommandHandler.productMurkup(1,Long.parseLong(meal.getId())));
                                botService.executeMessages(sendPhoto);
                                userServise.updateState(chatId,UserState.CHOOSE_MEAL);
                            }
                        }
                    }
                });

            }
            case WRITING -> {
                xabar.setDesc(text);
                xabar.setChatId(chatId);
                db.setXabar(chatId,xabar);
                sendMessage.setText("Xabaringiz qabul qilindi tez orada siz bilan bog'lanamiz ;)");
                botService.executeMessages(sendMessage);
            }
            default -> {
                sendMessage.setText("Menuni tanlang");
                sendMessage.setReplyMarkup(replyService.keyboardMaker(Utils.mainMenuUser));
                botService.executeMessages(sendMessage);
            }
        }



    }

    private void adminStateHandler(Long chatId, Update update) {

        String text = update.getMessage().getText();
        sendMessageToAdmin.setChatId(chatId);
        AdminState adminState=userServise.getAdminState(chatId);
        if (update.hasMessage() && update.getMessage().hasPhoto()&&userServise.getAdminState(chatId).equals(AdminState.MENU_PHOTO)) {
            List<PhotoSize> photos = update.getMessage().getPhoto();
            PhotoSize photo = photos.get(photos.size() - 1);
            menuType.setPhoto(photo.getFileId() + ";" + update.getMessage().getMessageId());
            MenuType menuType1 = new MenuType(menuType.getTitle(),menuType.getPhoto());
            adminService.addMenu(menuType1);
            sendMessageToAdmin.setText("menu qo'shildi");
            sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(Utils.create_menu));
            botService.executeMessages(sendMessageToAdmin);
            userServise.updateState(chatId,AdminState.ADMIN_MENU);
            return;
        }else if (update.hasMessage() && update.getMessage().hasPhoto()&&userServise.getAdminState(chatId).equals(AdminState.MEAL_PHOTO)){
            List<PhotoSize> photos = update.getMessage().getPhoto();
            PhotoSize photo = photos.get(photos.size() - 1);
            meal.setPhoto(photo.getFileId() + ";" + update.getMessage().getMessageId());
            Meal meal1 = new Meal(meal.getTitle(),meal.getPhoto(),meal.getDescription(),meal.getPrice());
            adminService.addMeal(menuType.getTitle(),meal1);
            sendMessageToAdmin.setText("Meal qo'shildi");
            sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(Utils.create_meal));
            botService.executeMessages(sendMessageToAdmin);
            userServise.updateState(chatId,AdminState.ADMIN_MENU);
            return;
        }
        switch (text){
            case "/start"->{
                userServise.updateState(chatId,AdminState.ADMIN_START);
                adminState=userServise.getAdminState(chatId);
            }
            case Utils.CREATE_MENU -> {
                sendMessageToAdmin.setText("Menu nomini kiriting");
                sendMessageToAdmin.setReplyMarkup(null);
                botService.executeMessages(sendMessageToAdmin);
                userServise.updateState(chatId,AdminState.MENU_NAME);
            }
            case Utils.ADD_MEAL -> {
                sendMessageToAdmin.setText("Qaysi Menu ga qo'shishingizni tanlang");
                sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(db.getAllMeals().keySet()));
                botService.executeMessages(sendMessageToAdmin);
                userServise.updateState(chatId,AdminState.MEAL_IN_MENU_TITLE);
            }
            case Utils.DELETE_MEAL -> {
                sendMessageToAdmin.setText("Menu type ni tanlang: ");
                sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(db.getAllMeals().keySet()));
                botService.executeMessages(sendMessageToAdmin);
                userServise.updateState(chatId,AdminState.DELETE_MEAL1);
            }
            case Utils.EDIT -> {
                sendMessageToAdmin.setText("Tanlang: ");
                sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(Utils.edit_base));
                botService.executeMessages(sendMessageToAdmin);
                userServise.updateState(chatId,AdminState.EDIT_BASE);
            }
            case Utils.CONFIRM_ORDER -> {
                HashMap<Long, ArrayList<Buyurtma>> buyurtma1 = db.getBuyurtma();
                if (buyurtma1.isEmpty()) {
                    sendMessageToAdmin.setText("Buyurtma Mavjud Emas");
                    sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(Utils.mainMenuAdmin));
                    botService.executeMessages(sendMessageToAdmin);
                    return;
                }
                buyurtma1.forEach((aLong, buyurtmas) -> {
                    if (!buyurtmas.isEmpty()) {
                        int i = 1;
                        for (Buyurtma buyurtma : buyurtmas) {
                            if(buyurtma.getState().equals(BuyurtmaState.KUTILMOQADA)) {
                                StringBuilder ordersOneUser = new StringBuilder();
                                ordersOneUser.append(buyurtma.getName()).append("\n");
                                ordersOneUser.append(buyurtma.getPhone()).append("\n");
                                ordersOneUser.append(i++).append("   -> Menu: ");
                                ordersOneUser.append(buyurtma.getMenuType()).append("\n Meal name: ");
                                ordersOneUser.append(buyurtma.getMealName()).append("\n Midori: ");
                                ordersOneUser.append(buyurtma.getCount()).append("\n Narxi: ");
                                ordersOneUser.append(buyurtma.getPrice()).append("\n");
                                sendMessageToAdmin.setText(ordersOneUser.toString());
                                sendMessageToAdmin.setReplyMarkup(CommandHandler.adminOrder(buyurtma.getProductId()));
                                botService.executeMessages(sendMessageToAdmin);
                            }
                        }
                    }
                });
                SendMessage sendMessage1 = new SendMessage();
                sendMessage1.setChatId(chatId);
                sendMessage1.setText("Tanlang");
                sendMessage1.setReplyMarkup(replyService.keyboardMaker(Utils.order_Admin_menu));
                botService.executeMessages(sendMessage1);
                userServise.updateState(chatId,AdminState.ORDER_PROSSES);
            }
            case Utils.SHOW_HISTORY -> {
                ArrayList<Buyurtma> history = db.getHistory();
                if (history.isEmpty()){
                    sendMessageToAdmin.setText("History Mavjud emas");
                    botService.executeMessages(sendMessageToAdmin);
                    return;
                }
                StringBuilder ordersOneUser = new StringBuilder();
                int i = 1;
                for (Buyurtma buyurtma1 : history) {
                    ordersOneUser.append(buyurtma1.getName()).append("\n");
                    ordersOneUser.append(buyurtma1.getPhone()).append("\n");
                    ordersOneUser.append(i++).append("   -> Menu: ");
                    ordersOneUser.append(buyurtma1.getMenuType()).append("\n Meal name: ");
                    ordersOneUser.append(buyurtma1.getMealName()).append("\n Midori: ");
                    ordersOneUser.append(buyurtma1.getCount()).append("\n Narxi: ");
                    ordersOneUser.append(buyurtma1.getPrice()).append("\n State: ");
                    ordersOneUser.append(buyurtma1.getState()).append("\n");
                }
                sendMessageToAdmin.setText(ordersOneUser.toString());
//                sendMessageToAdmin.setReplyMarkup(CommandHandler.adminOrder(buyurtma.getProductId()));
                botService.executeMessages(sendMessageToAdmin);
            }
            case "orqaga"->{
                sendMessageToAdmin.setText("admin Menu");
                sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(Utils.mainMenuAdmin));
                botService.executeMessages(sendMessageToAdmin);
            }
            default -> {
            }
        }
        switch (adminState){
            case ADMIN_START -> {
                sendMessageToAdmin.setText("Siz admin qilib tayinlandingiz ");
                sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(Utils.mainMenuAdmin));
                userServise.updateState(chatId,AdminState.ADMIN_MENU);
                botService.executeMessages(sendMessageToAdmin);
            }
            case MENU_NAME -> {
                menuType.setTitle(text);
                sendMessageToAdmin.setText("menu photosini kiriting");
                botService.executeMessages(sendMessageToAdmin);
                userServise.updateState(chatId,AdminState.MENU_PHOTO);
            }
            case MEAL_IN_MENU_TITLE -> {
                menuType.setTitle(text);
                sendMessageToAdmin.setText("meal name ni yozing");
                sendMessageToAdmin.setReplyMarkup(null);
                botService.executeMessages(sendMessageToAdmin);
                userServise.updateState(chatId,AdminState.MEAL_NAME);
            }
            case MEAL_NAME -> {
                meal.setTitle(text);
                sendMessageToAdmin.setText("meal description ni yozing");
                botService.executeMessages(sendMessageToAdmin);
                userServise.updateState(chatId,AdminState.MEAL_DESCRIPTION);
            }
            case MEAL_DESCRIPTION -> {
                meal.setDescription(text);
                sendMessageToAdmin.setText("meal narxini kiriting");
                botService.executeMessages(sendMessageToAdmin);
                userServise.updateState(chatId,AdminState.MEAL_PRICE);
            }
            case MEAL_PRICE -> {
                meal.setPrice(Double.valueOf(text));
                sendMessageToAdmin.setText("meal Photosini kiriting");
                botService.executeMessages(sendMessageToAdmin);
                userServise.updateState(chatId,AdminState.MEAL_PHOTO);
            }
            case DELETE_MEAL1 -> {
                menuType.setTitle(text);
                db.getAllMeals().forEach((menuType1, meals) -> {
                    if (menuType1.getTitle().equals(text)){
                        sendMessageToAdmin.setText("O'chirmoqchi bo'lgan mealni tanlang: ");
                        sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(meals));
                        botService.executeMessages(sendMessageToAdmin);
                        userServise.updateState(chatId,AdminState.DELETE_MEAL2);
                    }
                });
            }
            case DELETE_MEAL2 -> {
                db.getAllMeals().forEach((menuType1, meals) -> {
                    if (menuType1.getTitle().equals(menuType.getTitle())) {
                        meals.removeIf(meal1 -> meal1.getTitle().equals(text));
                        sendMessageToAdmin.setText("O'chirildi 👌");
                        sendMessageToAdmin.setReplyMarkup(replyService.keyboardMaker(Utils.delete_meal));
                        botService.executeMessages(sendMessageToAdmin);
                        userServise.updateState(chatId,AdminState.ADMIN_MENU);
                    }
                });
            }
            case EDIT_BASE -> {

            }
            case MENU_PHOTO -> {

            }
        }
    }

//    public void callbackHandler(Update update) {
//
//    }
public String validatePhoneNumber(String phoneNumber) {
    // Regex to match Uzbek phone numbers: +998 followed by 9 digits
    String phoneRegex = "^\\+998[0-9]{9}$";
    if (!phoneNumber.matches(phoneRegex)) {
        return "Telefon raqami +998 bilan boshlanishi va 9 ta raqamdan iborat bo'lishi kerak.";
    }
    return null;
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
