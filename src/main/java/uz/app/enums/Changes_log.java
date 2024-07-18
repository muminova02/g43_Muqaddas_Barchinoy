package uz.app.enums;


import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Changes_log {
    int id;
    int  user_id;
    Double old_number;
    Double new_number;
    String trigger_ooperations;
    String time;
}
