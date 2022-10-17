package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.data.CardInfo;
import ru.netology.data.DbUtils;
import ru.netology.page.Credit;
import ru.netology.page.FirstPage;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

public class CreditTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        DbUtils.clearTables();
        String url = System.getProperty("sut.url");
        open(url);

    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    //t
    @Test
    void shouldBuyInCredit() throws SQLException {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();
        credit.fulfillData(card);
        credit.checkSuccessNotification();
        assertEquals("APPROVED", DbUtils.getCreditStatus());
    }
    //t
    @Test
    void shouldBuyInCreditWithNameInRussianLetters() throws SQLException {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getInValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkSuccessNotification();
        assertEquals("APPROVED", DbUtils.getCreditStatus());
    }

    //t
    @Test
    void shouldNotBuyInCreditWithDeclinedCardNumber() throws SQLException {
        CardInfo card = new CardInfo(getDeclinedNumber(), getCurrentMonth(), getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkDeclineNotification();

    }

    //f
    @Test
    void shouldNotBuyInCreditWithInvalidCardNumber() throws SQLException {
        CardInfo card = new CardInfo(getInvalidCardNumber(), getCurrentMonth(), getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkDeclineNotification();

    }

    //t
    @Test
    void shouldNotBuyInCreditWithShortCardNumber() {
        CardInfo card = new CardInfo(getShortCardNumber(), getCurrentMonth(), getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkInvalidFormat();
    }

    //f
    //TODO Изменить надпись на "Поле обязательно для заполнения"
    @Test
    void shouldNotBuyInCreditWithEmptyCardNumber() {
        CardInfo card = new CardInfo(null, getCurrentMonth(), getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkRequiredField();
    }

    //MonthField
    //f
    //TODO Изменить надпись на "Неверно указан срок действия карты"
    @Test
    void shouldNotBuyInCreditWithInvalidMonth() {
        CardInfo card = new CardInfo(getApprovedNumber(), "00", getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkInvalidDate();
    }

    //t
    @Test
    void shouldNotBuyInCreditWithNonExistingMonth() {
        CardInfo card = new CardInfo(getApprovedNumber(), "13", getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkInvalidDate();

    }

    //f
    //TODO Изменить надпись на "Истёк срок годности"
    @Test
    void shouldNotBuyInCreditWithExpiredMonth() {
        CardInfo card = new CardInfo(getApprovedNumber(), getLastMonth(), getCurrentYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkExpiredDate();
    }

    //f
    //TODO Изменить надпись  на "Поле обязательно для заполнения"
    @Test
    void shouldNotBuyInCreditWithEmptyMonth() {
        CardInfo card = new CardInfo(getApprovedNumber(), null, getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkRequiredField();
    }

    //YearField
    //t
    @Test
    void shouldNotBuyInCreditGateWithExpiredYear() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getLastYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkExpiredDate();
    }

    //f
    //TODO Изменить надпись на "Поле обязательно для заполнения"
    @Test
    void shouldNotBuyInCreditWithEmptyYear() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), null, getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkRequiredField();
    }

    //NameField
    //f
    //TODO Изменить надпись на "Введите полное имя и фамилию"
    @Test
    void shouldNotBuyInCreditWithOnlySurname() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getOnlySurname(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkInvalidName();
    }

    //f
    @Test
    void shouldNotBuyInCreditWithNameAndSurnameWithDash() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), "Иван-Иванов", getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkInvalidFormat();
    }

    //f
    //TODO Изменить надпись на "Значение поля может содержать только буквы и дефис"
    @Test
    void shouldNotBuyInCreditWithDigitsInName() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getNameWithNumbers(), getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkInvalidDataName();
    }


    //t
    @Test
    void shouldNotBuyInCreditWithEmptyName() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), null, getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkRequiredField();
    }

    //f
    //TODO Изменить надпись на "Поле обязательно для заполнения"
    @Test
    void shouldNotBuyInCreditWithSpaceInsteadOfName() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), " ", getValidCvc());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkInvalidDataName();
    }

    //f
    //TODO Изменить надпись на "Значение поля должно содержать 3 цифры"
    @Test
    void shouldNotBuyInCreditWithOneDigitInCvc() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getValidName(), getCvcWithOneDigit());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkInvalidCvc();
    }

    //f
    @Test
    void shouldNotBuyInCreditWithTwoDigitsInCvc() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getValidName(), getCvcWithTwoDigits());
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkInvalidCvc(); //TODO Изменить надпись под полем CVC "Значение поля должно содержать 3 цифры"
    }

    //f
    //TODO Изменить надпись на "Поле обязательно для заполнения"
    @Test
    void shouldNotBuyInCreditWithEmptyCvc() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getValidName(), null);
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkRequiredField();
    }

    //f
    //TODO Изменить надписи на "Поле обязательно для заполнения"
    @Test
    void shouldNotBuyInCreditWithAllEmptyFields() {
        CardInfo card = new CardInfo(null, null, null, null, null);
        val firstPage = new FirstPage();
        val credit = firstPage.buyInCredit();

        credit.fulfillData(card);
        credit.checkAllFieldsAreRequired();

    }
}