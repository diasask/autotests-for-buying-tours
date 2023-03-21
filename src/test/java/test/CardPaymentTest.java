package test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.User;
import interactions.DBInteractions;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.DashboardPage;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.open;


class CardPaymentTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        Configuration.headless = true;
        open("http://localhost:8080");
        DBInteractions.cleanData();
    }


    @Test
    void shouldPayByCardValid() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        User user1 = DataHelper.getAuthInfo(0);//создаем пользователя
        var notificationPageCard1 = Pay.payValid(user1);//производим оплату по данным пользователя картой
        String actualNotificationCard1 = notificationPageCard1.getTextNotificationSuccess();//получаем сообщение
        String expectedNotificationCard1 = "Операция одобрена Банком.";
        Assertions.assertEquals(expectedNotificationCard1, actualNotificationCard1);

        String actualStatusCard1 = DBInteractions.paymentRequest()[0];// получаем статус из БД
        String expectedStatusCard1 = DataHelper.readData(0, "status");
        Assertions.assertEquals(expectedStatusCard1, actualStatusCard1);

        String actualSumCard1 = DBInteractions.paymentRequest()[1];// получаем сумму из БД
        String expectedSumCard1 = "45000";
        Assertions.assertEquals(expectedSumCard1, actualSumCard1);

        DBInteractions.cleanData();// очистка базы данных

        User user2 = DataHelper.getAuthInfo(1);//создаем пользователя
        var notificationPageCard2 = Pay.payValid(user2);//производим оплату по данным пользователя картой
        var actualNotificationCard2 = notificationPageCard2.getTextNotificationSuccess();//получаем сообщение
        var expectedNotificationCard2 = "Операция одобрена Банком.";//"Ошибка! Банк отказал в проведении операции."
        Assertions.assertEquals(expectedNotificationCard2, actualNotificationCard2);

        var actualStatusCard2 = DBInteractions.paymentRequest()[0];// получаем статус из БД
        var expectedStatusCard2 = DataHelper.readData(1, "status");
        Assertions.assertEquals(expectedStatusCard2, actualStatusCard2);

        var actualSumCard2 = DBInteractions.paymentRequest()[1];// получаем сумму из БД
        var expectedSumCard2 = "45000";
        Assertions.assertEquals(expectedSumCard2, actualSumCard2);
    }

    @Test
    void shouldFormEmpty() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        Pay.pressNext();
        Assertions.assertEquals("Неверный формат", Pay.getTextCardNumberField());
        Assertions.assertEquals("Неверный формат", Pay.getTextMonthField());
        Assertions.assertEquals("Неверный формат", Pay.getTextYearField());
        Assertions.assertEquals("Поле обязательно для заполнения", Pay.getTextCardholdersNameField());
        Assertions.assertEquals("Неверный формат", Pay.getTextCvcField());
    }

    @Test
    void shouldOneFieldEmpty() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), "", Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());//пустое поле номера карты
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextCardNumberField());//ловим надпись "Неверный формат"
        var user2 = new User(Data.getValidName(), Data.getValidCardNumber(0), "", Data.getValidYear(), Data.getValidCvc());//пустое поле месяца
        Pay.payInvalid(user2);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextMonthField());
        var user3 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), "", Data.getValidCvc());//пустое поле год
        Pay.payInvalid(user3);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextYearField());
        var user4 = new User("", Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());//пустое поле имя
        Pay.payInvalid(user4);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Поле обязательно для заполнения", Pay.getTextCardholdersNameField());
        var user5 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), "");//пустое поле CVC
        Pay.payInvalid(user5);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextCvcField());
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldCardNumberNumeric() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getInvalidCardNumber(), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        var notificationPageCard1 = Pay.payValid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Ошибка! Банк отказал в проведении операции.", notificationPageCard1.getTextNotificationError());//получаем сообщение
        notificationPageCard1.getSelenideElementNotificationSuccess().shouldNotBe(Condition.visible, Duration.ofSeconds(15));
        //notificationPageCard1.getSelenideElementNotificationSuccess().shouldBe(Condition.visible, Duration.ofSeconds(15));//для проверки, что тест падает или проходит
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldCardNumberCyrillic() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), "НОМЕРКАРТЫ", Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextCardNumberField());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldCardNumberLatin() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), "CardNumber", Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextCardNumberField());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldCardNumberSpecialSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), "$%@#", Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextCardNumberField());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldCardNumberOneNumeric() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), "5", Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextCardNumberField());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldCardNumberTwoNumeric() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), "34", Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextCardNumberField());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldCardNumberFifteenNumeric() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), "345623456789012", Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextCardNumberField());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldCardNumberSeventeenNumeric() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0) + "9", Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        var notificationPage = Pay.payValid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals(Data.getValidCardNumber(0).replaceAll("\\s", ""), Pay.getTextCardNumber());//ловим надпись в поле номера карты
        Assertions.assertEquals("Операция одобрена Банком.", notificationPage.getTextNotificationSuccess());//ловим надпись "Операция одобрена Банком."
        Assertions.assertEquals(Data.readData(0, "status"), DBInteractions.paymentRequest()[0]);// получаем статус из БД
        Assertions.assertEquals("45000", DBInteractions.paymentRequest()[1]);// получаем сумму из БД
    }

    @Test
    void shouldValidMonth() {
        String month = null;
        for (int i = 1; i < 13; i++) {
            month = String.valueOf((i < 10 ? ("0" + i) : (i)));
            var PayByCard = new DashboardPage();//открываем dashboard
            var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
            var Data = new DataHelper(); //создание объекта класса DataHelper
            var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), month, Data.getValidYear(), Data.getValidCvc());
            var notificationPageCard1 = Pay.payValid(user1);//производим оплату по данным пользователя картой
            String actualNotificationCard1 = notificationPageCard1.getTextNotificationSuccess();//получаем сообщение
            String expectedNotificationCard1 = "Операция одобрена Банком.";
            Assertions.assertEquals(expectedNotificationCard1, actualNotificationCard1);
            String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
            String expectedStatusCard1 = DataHelper.readData(0, "status");
            Assertions.assertEquals(expectedStatusCard1, actualStatus);
            String actualSumCard1 = DBInteractions.paymentRequest()[1];// получаем сумму из БД
            String expectedSumCard1 = "45000";
            Assertions.assertEquals(expectedSumCard1, actualSumCard1);
            DBInteractions.cleanData();// очистка базы данных
        }
    }

    @Test
    void shouldInvalidMonthNumeric() {
        String month = null;
        for (int i = 0; i < 14; i = i + 13) {
            month = String.valueOf((i < 10 ? ("0" + i) : (i)));
            var PayByCard = new DashboardPage();//открываем dashboard
            var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
            var Data = new DataHelper(); //создание объекта класса DataHelper
            var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), month, Data.getValidYear(), Data.getValidCvc());
            Pay.payInvalid(user1);//производим оплату по данным пользователя картой
            Assertions.assertEquals("Неверно указан срок действия карты", Pay.getTextInvalidDate());//ловим надпись "Неверно указан срок действия карты"
            String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
            Assertions.assertEquals("null", actualStatus);
            DBInteractions.cleanData();// очистка базы данных
        }
    }

    @Test
    void shouldInvalidMonthCyrillic() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), "МЕСЯЦ", Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidMonthLatin() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), "month", Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidMonthSpecialSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), "$%@#", Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidMonthCurrent() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getPreviousMonth(), Data.getCurrentYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверно указан срок действия карты", Pay.getTextInvalidDate());//ловим надпись "Неверно указан срок действия карты"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidYearNumericUp() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), String.valueOf(Integer.valueOf(Data.getCurrentYear()) + 6),
                Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверно указан срок действия карты", Pay.getTextInvalidDate());//ловим надпись "Неверно указан срок действия карты"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidYearNumericDown() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), String.valueOf(Integer.valueOf(Data.getCurrentYear()) - 1),
                Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Истёк срок действия карты", Pay.getTextExpiredDate());//ловим надпись "Истёк срок действия карты"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidMonthOneNumeric() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), "5", Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidMonthThreeNumeric() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), "052", Data.getValidYear(), Data.getValidCvc());
        var notificationPage = Pay.payValid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("05", Pay.getTextMonth());//ловим надпись в поле месяца
        Assertions.assertEquals("Операция одобрена Банком.", notificationPage.getTextNotificationSuccess());//ловим надпись "Операция одобрена Банком."
        Assertions.assertEquals(Data.readData(0, "status"), DBInteractions.paymentRequest()[0]);// получаем статус из БД
        Assertions.assertEquals("45000", DBInteractions.paymentRequest()[1]);// получаем сумму из БД
    }

    @Test
    void shouldInvalidYearCyrillic() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), "ГОД", Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidYearLatin() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), "year", Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidYearSpecialSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), "$%@#", Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidYearOneNumeric() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), "2", Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidYearThreeNumeric() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), "235", Data.getValidCvc());
        var notificationPage = Pay.payValid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("23", Pay.getTextYear());//ловим надпись в поле года
        Assertions.assertEquals("Операция одобрена Банком.", notificationPage.getTextNotificationSuccess());//ловим надпись "Операция одобрена Банком."
        Assertions.assertEquals(Data.readData(0, "status"), DBInteractions.paymentRequest()[0]);// получаем статус из БД
        Assertions.assertEquals("45000", DBInteractions.paymentRequest()[1]);// получаем сумму из БД
    }

    @Test
    void shouldValidCardholderNameUpperCase() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName().toUpperCase(Locale.ROOT), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        var notificationPageCard1 = Pay.payValid(user1);//производим оплату по данным пользователя картой
        String actualNotificationCard1 = notificationPageCard1.getTextNotificationSuccess();//получаем сообщение
        String expectedNotificationCard1 = "Операция одобрена Банком.";
        Assertions.assertEquals(expectedNotificationCard1, actualNotificationCard1);
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        String expectedStatusCard1 = DataHelper.readData(0, "status");
        Assertions.assertEquals(expectedStatusCard1, actualStatus);
        String actualSumCard1 = DBInteractions.paymentRequest()[1];// получаем сумму из БД
        String expectedSumCard1 = "45000";
        Assertions.assertEquals(expectedSumCard1, actualSumCard1);
    }

    @Test
    void shouldValidCardholderNameLowerCase() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName().toLowerCase(Locale.ROOT), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        var notificationPageCard1 = Pay.payValid(user1);//производим оплату по данным пользователя картой
        String actualNotificationCard1 = notificationPageCard1.getTextNotificationSuccess();//получаем сообщение
        String expectedNotificationCard1 = "Операция одобрена Банком.";
        Assertions.assertEquals(expectedNotificationCard1, actualNotificationCard1);
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        String expectedStatusCard1 = DataHelper.readData(0, "status");
        Assertions.assertEquals(expectedStatusCard1, actualStatus);
        String actualSumCard1 = DBInteractions.paymentRequest()[1];// получаем сумму из БД
        String expectedSumCard1 = "45000";
        Assertions.assertEquals(expectedSumCard1, actualSumCard1);
    }

    @Test
    void shouldValidCardholderNameCyrillic() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getFullName("ru"), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        var notificationPageCard1 = Pay.payValid(user1);//производим оплату по данным пользователя картой
        String actualNotificationCard1 = notificationPageCard1.getTextNotificationSuccess();//получаем сообщение
        String expectedNotificationCard1 = "Операция одобрена Банком.";
        Assertions.assertEquals(expectedNotificationCard1, actualNotificationCard1);
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        String expectedStatusCard1 = DataHelper.readData(0, "status");
        Assertions.assertEquals(expectedStatusCard1, actualStatus);
        String actualSumCard1 = DBInteractions.paymentRequest()[1];// получаем сумму из БД
        String expectedSumCard1 = "45000";
        Assertions.assertEquals(expectedSumCard1, actualSumCard1);
    }

    @Test
    void shouldInvalidCardholderNameNumeric() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User("505", Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidCardholderNameOnlyName() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getOnlyName("en"), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidCardholderNameOnlyLastName() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getOnlyLastName("en"), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidCardholderNameSpecialSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User("$%@#", Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidCardholderNameOneSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User("A", Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Необходимо ввести имя и фамилию", Pay.getTextWrongName());//ловим надпись "Необходимо ввести имя и фамилию"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidCardholderNameTwoSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User("A ", Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Необходимо ввести имя и фамилию", Pay.getTextWrongName());//ловим надпись "Необходимо ввести имя и фамилию"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldValidCardholderNameThreeSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidNameThreeHundred(), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), Data.getValidCvc());
        var notificationPageCard1 = Pay.payValid(user1);//производим оплату по данным пользователя картой
        String actualNotificationCard1 = notificationPageCard1.getTextNotificationSuccess();//получаем сообщение
        String expectedNotificationCard1 = "Операция одобрена Банком.";
        Assertions.assertEquals(expectedNotificationCard1, actualNotificationCard1);
        Assertions.assertEquals(Data.getValidNameThreeHundred(), Pay.getTextCardholdersName());//ловим надпись в поле владельца
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        String expectedStatusCard1 = DataHelper.readData(0, "status");
        Assertions.assertEquals(expectedStatusCard1, actualStatus);
        String actualSumCard1 = DBInteractions.paymentRequest()[1];// получаем сумму из БД
        String expectedSumCard1 = "45000";
        Assertions.assertEquals(expectedSumCard1, actualSumCard1);
    }

    @Test
    void shouldInvalidCVCCyrillic() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), "абв");
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidCVCLatin() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), "ghl");
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidCVCSpecialSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), "@#%");
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidCVCOneSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), "9");
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("9", Pay.getTextCvc());//ловим надпись в поле CVC
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidCVCTwoSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), "98");
        Pay.payInvalid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("98", Pay.getTextCvc());//ловим надпись в поле CVC
        Assertions.assertEquals("Неверный формат", Pay.getTextWrongFormat());//ловим надпись "Неверный формат"
        String actualStatus = DBInteractions.paymentRequest()[0];// получаем статус из БД
        Assertions.assertEquals("null", actualStatus);
    }

    @Test
    void shouldInvalidCVCFourSymbol() {
        var PayByCard = new DashboardPage();//открываем dashboard
        var Pay = PayByCard.PaymentByCard();//открываем страницу оплаты
        var Data = new DataHelper(); //создание объекта класса DataHelper
        var user1 = new User(Data.getValidName(), Data.getValidCardNumber(0), Data.getValidMonth(), Data.getValidYear(), "9878");
        var notificationPage = Pay.payValid(user1);//производим оплату по данным пользователя картой
        Assertions.assertEquals("987", Pay.getTextCvc());//ловим надпись в поле CVC
        Assertions.assertEquals("Операция одобрена Банком.", notificationPage.getTextNotificationSuccess());//ловим надпись "Операция одобрена Банком."
        Assertions.assertEquals(Data.readData(0, "status"), DBInteractions.paymentRequest()[0]);// получаем статус из БД
        Assertions.assertEquals("45000", DBInteractions.paymentRequest()[1]);// получаем сумму из БД
    }
}

