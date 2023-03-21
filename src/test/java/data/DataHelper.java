package data;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;

import java.util.Calendar;
import java.util.Locale;

public class DataHelper {

    public static String address = "artifacts/data.json";

    public static User getAuthInfo(int card) {
        return new User(getValidName(), getValidCardNumber(card), getValidMonth(), getValidYear(), getValidCvc());
    }

    private static final Faker faker = new Faker(new Locale("en"));

    public static String getValidName() {
        return faker.name().fullName();
    }

    public static String getFullName(String locale) {
        Locale loc = new Locale(locale);
        Faker fake = new Faker(loc);
        return fake.name().fullName();
    }

    public static String getOnlyName(String locale) {
        Locale loc = new Locale(locale);
        Faker fake = new Faker(loc);
        return fake.name().firstName();
    }

    public static String getOnlyLastName(String locale) {
        Locale loc = new Locale(locale);
        Faker fake = new Faker(loc);
        return fake.name().lastName();
    }

    @SneakyThrows
    public static String getValidCardNumber(int card) {
        String cardNumber = readData(card, "number");
        return cardNumber;
    }

    public static String readData(int card, String nameField) {
        String result = (String) ReadJSONFile.readJson(card, address).get(nameField);
        return result;
    }

    public static String getValidMonth() {
        int month = (int) (Math.random() * 12 + 1);
        return String.valueOf((month < 10 ? ("0" + month) : (month)));
    }

    public static String getPreviousMonth() {
        Calendar cale = null;
        cale = Calendar.getInstance();
        int month = cale.get(Calendar.MONTH);
        return String.valueOf((month < 10 ? ("0" + month) : (month)));
    }

    public static String getInvalidMonth() {
        int month = (int) (Math.random() * 12 + 1);
        return String.valueOf((month < 10 ? ("0" + month) : (month)));
    }

    public static String getValidYear() {
        Calendar cale = null;
        cale = Calendar.getInstance();
        int year = cale.get(Calendar.YEAR) + 1;
        return String.valueOf(year).substring(2);
    }

    public static String getCurrentYear() {
        Calendar cale = null;
        cale = Calendar.getInstance();
        int year = cale.get(Calendar.YEAR);
        return String.valueOf(year).substring(2);
    }

    public static String getValidCvc() {
        String cvc = null;
        for (int i = 1; i < 4; i++) {
            cvc = cvc + String.valueOf((int) (Math.random() * 10));
        }
        return cvc;
    }

    public static String getInvalidCardNumber() {
        String invalidCardNumber = null;
        for (int i = 1; i < 17; i++) {
            invalidCardNumber = invalidCardNumber + String.valueOf((int) (Math.random() * 10));
        }
        return invalidCardNumber;
    }

    public static String getValidNameThreeHundred() {
        return "nsmxheeanqirhnsyzmdwbpqkdevvvgeqvnbcobvrgdswqabtjufatjglnutlebgsyurrmovppedbrmapmxmfpihgigaymcntstrnlnewnjesaywumrvjfbxotrvrpxjcksfqsuywxgzoejbohqtmlxqbzgxfgivcmtihbfhnkwvtmnadbeldzfpjiczmbyirupchdqxhfhvsdogorqrxnztmkeaososgowpsuekvjkjrbqvfxqqrofnhwpbljrewfgdgcjdjjxigjolyegkznhqwnwnsxlncomunjcqyznim";
    }

}
