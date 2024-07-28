package uz.app.enums;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transfer {
    int id;
    int user_id;
    String from_card;
    String to_card;
    Double amount;
    boolean active;
}
