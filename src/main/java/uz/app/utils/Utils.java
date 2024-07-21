package uz.app.utils;

import java.util.Calendar;
import java.util.Map;

public interface Utils {

    String SHOW_CARDS = "SHow Cards";
    String ADD_CARD = "add Card";
    String TRANSFER = "transfer";
    String HISTORY = "history";
    String DEPOSIT = "DEPOSIT";
    String AGREE = "O'TKAZISH";
    String CANCEL = "CANCEL";
    String [][] MENU={
            {SHOW_CARDS,ADD_CARD},
            {TRANSFER,HISTORY},
            {DEPOSIT}
    };

    String [][] TRANSFER_AGREE={
            {AGREE, CANCEL}
    };



}
