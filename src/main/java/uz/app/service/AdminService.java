package uz.app.service;

import org.example.db.Db;
import org.example.entity.Meal;
import org.example.entity.MenuType;

import java.util.ArrayList;

public class AdminService {

    private static final Db db = Db.getInstance();
    private final ReplyMarkupService replyMarkupService = new ReplyMarkupService();
    private final InlineMarkupService inlineMarkupService = new InlineMarkupService();


    public void addMenu(MenuType menuType) {
        db.getAllMeals().put(menuType,new ArrayList<>());
    }
    public void addMeal(String menuTitle,Meal meal){
        db.getAllMeals().forEach((menuType, meals) -> {
            if (menuType.getTitle().equals(menuTitle)){
                meals.add(meal);
            }
        });
    }

    private static AdminService adminService;

    public static AdminService getInstance() {
        if (adminService == null) {
            adminService = new AdminService();
        }
        return adminService;
    }

}
