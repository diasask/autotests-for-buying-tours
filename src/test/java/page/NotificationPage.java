package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

public class NotificationPage {
    private SelenideElement notificationSuccess = $("div.notification__content");
    private SelenideElement exitSuccess = $("span.icon-button__text");
    private SelenideElement notificationError = $(withText("Ошибка! Банк отказал в проведении операции"));
    private SelenideElement exitError = $("body div[class=\"notification notification_visible " +
            "notification_status_error notification_has-closer notification_stick-to_right notification_theme_alfa-on-white\"] span[class=\"icon-button__text\"]");
    //private SelenideElement exitError = $$("span.icon-button__text").get(1);

    public NotificationPage(){
        notificationSuccess.shouldBe(Condition.visible, Duration.ofSeconds(15));
    }

    public String getTextNotificationSuccess(){
        String textNotification = notificationSuccess.getText();
        exitSuccess.click();
        return textNotification;
    }
    public SelenideElement getSelenideElementNotificationSuccess(){
       return notificationSuccess;
    }

    public String getTextNotificationError(){
        String textNotification = notificationError.getText();
        exitError.click();
        return textNotification;
    }
}
