package uz.app.enums;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Card {
    int id;
    String number;
    Double balance;
    User user;
}
