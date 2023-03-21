package page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
    private SelenideElement heading = $(withText("Путешествие дня"));
    private SelenideElement button1 = $(withText("Купить"));
    private SelenideElement button2 = $(withText("Купить в кредит"));
    private SelenideElement textControlPayByCard = $(withText("Оплата по карте"));
    private SelenideElement textControlPayByCreditCard = $(withText("Кредит по данным карты"));

    public DashboardPage() {
        heading.shouldBe(visible);
        button1.shouldBe(visible);
        button2.shouldBe(visible);
    }

    public PaymentPage PaymentByCard(){
        button1.click();
        textControlPayByCard.shouldBe(visible);
        return new PaymentPage();
    }
    public PaymentPage PaymentByCredit(){
        button2.click();
        textControlPayByCreditCard.shouldBe(visible);
        return new PaymentPage();
    }

}
