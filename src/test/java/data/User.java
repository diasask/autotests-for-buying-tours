package data;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class User {
    String name;
    String cardNumber;
    String month;
    String year;
    String cvc;
}
