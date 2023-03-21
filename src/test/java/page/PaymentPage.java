package page;

import com.codeborne.selenide.SelenideElement;
import data.User;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {
    private SelenideElement cardNumberField = $$("input.input__control").get(0);
    private SelenideElement monthField = $$("input.input__control").get(1);
    private SelenideElement yearField = $$("input.input__control").get(2);
    private SelenideElement cardholdersNameField = $$("input.input__control").get(3);
    private SelenideElement cvcField = $$("input.input__control").get(4);
    private SelenideElement buttonNext = $(withText("Продолжить"));
    private SelenideElement textCardNumberField = $$("span.input__sub").get(0);
    //    private SelenideElement textMonthField = $("body span[class=\"input input_type_text input_view_default inp" +
//            "ut_size_m input_width_available input_has-label input_has-value input_invalid input_theme_alfa-on-white\"] span[class=\"input__sub\"]");
    //private SelenideElement textMonthField = $$("span.input__sub").get(1);
    private SelenideElement textMonthField = $$("span.input__sub").get(1);
    private SelenideElement textYearField = $$("span.input__sub").get(2);
    private SelenideElement textCardholdersNameField = $$("span.input__sub").get(3);
    private SelenideElement textCvcField = $$("span.input__sub").get(4);

    private SelenideElement textInvalidDate = $(withText("Неверно указан срок действия карты"));
    private SelenideElement textExpiredDate = $(withText("Истёк срок действия карты"));
    private SelenideElement textWrongFormat = $(withText("Неверный формат"));
    private SelenideElement textWrongName = $(withText("Необходимо ввести имя и фамилию"));


    public PaymentPage() {
        cardNumberField.shouldBe(visible);
        monthField.shouldBe(visible);
        yearField.shouldBe(visible);
        cardholdersNameField.shouldBe(visible);
        cvcField.shouldBe(visible);
        buttonNext.shouldBe(visible);
    }

    public void inputForPay(User user){
        cardNumberField.sendKeys(Keys.CONTROL + "a");
        cardNumberField.sendKeys(Keys.DELETE);
        cardNumberField.sendKeys(user.getCardNumber());
        monthField.sendKeys(Keys.CONTROL + "a");
        monthField.sendKeys(Keys.DELETE);
        monthField.sendKeys(user.getMonth());
        yearField.sendKeys(Keys.CONTROL + "a");
        yearField.sendKeys(Keys.DELETE);
        yearField.sendKeys(user.getYear());
        cardholdersNameField.sendKeys(Keys.CONTROL + "a");
        cardholdersNameField.sendKeys(Keys.DELETE);
        cardholdersNameField.sendKeys(user.getName());
        cvcField.sendKeys(Keys.CONTROL + "a");
        cvcField.sendKeys(Keys.DELETE);
        cvcField.sendKeys(user.getCvc());
    }

    public NotificationPage payValid(User user) {
        inputForPay(user);
        buttonNext.click();
        return new NotificationPage();
    }

    public void payInvalid(User user) {
        inputForPay(user);
        buttonNext.click();
    }

    public void pressNext() {
        buttonNext.click();
    }

    public String getTextCardNumberField() {
        textCardNumberField.shouldBe(visible);
        return textCardNumberField.getText();
    }

    public String getTextCardNumber() {
        return cardNumberField.getValue().replaceAll("\\s","");
    }

    public String getTextMonthField() {
        textMonthField.shouldBe(visible);
        return textMonthField.getText();
    }

    public String getTextMonth() {
        return monthField.getValue();
    }

    public String getTextInvalidDate() {
        textInvalidDate.shouldBe(visible);
        return textInvalidDate.getText();
    }

    public String getTextExpiredDate() {
        textExpiredDate.shouldBe(visible);
        return textExpiredDate.getText();
    }

    public String getTextYearField() {
        textYearField.shouldBe(visible);
        return textYearField.getText();
    }

    public String getTextYear() {
        return yearField.getValue();
    }

    public String getTextCardholdersNameField() {
        textCardholdersNameField.shouldBe(visible);
        return textCardholdersNameField.getText();
    }

    public String getTextCardholdersName() {
        cardholdersNameField.shouldBe(visible);
        return cardholdersNameField.getValue();
    }

    public String getTextCvcField() {
        textCvcField.shouldBe(visible);
        return textCvcField.getText();
    }

    public String getTextCvc() {
        cvcField.shouldBe(visible);
        return cvcField.getValue();
    }

    public String getTextWrongFormat() {
        textWrongFormat.shouldBe(visible);
        return textWrongFormat.getText();
    }

    public String getTextWrongName() {
        textWrongName.shouldBe(visible);
        return textWrongName.getText();
    }

}
