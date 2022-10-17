package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.data.CardInfo;
import ru.netology.data.DbUtils;
import ru.netology.page.FirstPage;
import ru.netology.page.Payment;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

public class PaymentTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:8080");
        DbUtils.clearTables();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }
    //t
    @Test
    void shouldBuyInPayment() throws SQLException {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkSuccessNotification();
        assertEquals("APPROVED", DbUtils.getPaymentStatus());
    }

    //t
    @Test
    void shouldBuyInPaymentWithNameInRussianLetters() throws SQLException {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getInValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkSuccessNotification();
        assertEquals("APPROVED", DbUtils.getPaymentStatus());
    }

    //f
    @Test
    void shouldNotBuyInPaymentWithDeclinedCardNumber() throws SQLException {
        CardInfo card = new CardInfo(getDeclinedNumber(), getCurrentMonth(), getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkDeclineNotification();
        assertEquals("DECLINED", DbUtils.getPaymentStatus());
    }


    //CardNumberField
    //f
    @Test
    void shouldNotBuyInPaymentWithInvalidCardNumber() throws SQLException {
        CardInfo card = new CardInfo(getInvalidCardNumber(), getCurrentMonth(), getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkDeclineNotification();

    }

    //t
    @Test
    void shouldNotBuyInPaymentWithShortCardNumber() {
        CardInfo card = new CardInfo(getShortCardNumber(), getCurrentMonth(), getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkInvalidFormat();
    }

    //f
    //TODO Изменить надпись под полем на "Поле обязательно для заполнения"
    @Test
    void shouldNotBuyInPaymentWithEmptyCardNumber() {
        CardInfo card = new CardInfo(null, getCurrentMonth(), getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkRequiredField();
    }


    //MonthField
    //f
    //TODO Изменить надпись на "Неверно указан срок действия карты"
    @Test
    void shouldNotBuyInPaymentWithInvalidMonth() {
        CardInfo card = new CardInfo(getApprovedNumber(), "00", getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkInvalidDate();
    }

    //t
    @Test
    void shouldNotBuyInPaymentWithNonExistingMonth() {
        CardInfo card = new CardInfo(getApprovedNumber(), "13", getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkInvalidDate();

    }

    //f
    //TODO Изменить надпись на "Истёк срок действия карты"
    @Test
    void shouldNotBuyInPaymentWithExpiredMonth() {
        CardInfo card = new CardInfo(getApprovedNumber(), getLastMonth(), getCurrentYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkExpiredDate();
    }

    //f
    //TODO Изменить надпись на "Поле обязательно для заполнения"
    @Test
    void shouldNotBuyInPaymentWithEmptyMonth() {
        CardInfo card = new CardInfo(getApprovedNumber(), null, getNextYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkRequiredField();
    }


    //YearField
    //t
    @Test
    void shouldNotBuyInPaymentWithExpiredYear() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getLastYear(), getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkExpiredDate();
    }

    //f
    //TODO Изменить надпись на "Поле обязательно для заполнения"
    @Test
    void shouldNotBuyInPaymentWithEmptyYear() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), null, getValidName(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkRequiredField();
    }


    //NameField

    //f
    @Test
    void shouldNotBuyInPaymentWithOnlySurname() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getOnlySurname(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkInvalidName();
    }

    //f
    @Test
    void shouldNotBuyInPaymentWithNameAndSurnameWithDash() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), "Petro-Stefan", getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkInvalidFormat();
    }

    //f
    //TODO Изменить надпись на "Поле владелец может содержать только буквы и дефис"
    @Test
    void shouldNotBuyInPaymentWithDigitsInName() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getNameWithNumbers(), getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkInvalidDataName();
    }


    //t
    @Test
    void shouldNotBuyInPaymentWithEmptyName() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), null, getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkRequiredField();
    }

    //f
    //TODO Изменить надпись на "Поле владелец может содержать только буквы и дефис"
    @Test
    void shouldNotBuyInPaymentWithSpaceInsteadOfName() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), " ", getValidCvc());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkInvalidDataName();
    }


    //CVC/CVVField
    //f
    //TODO Изменить надпись на "Значение поля должно содержать 3 цифры"
    @Test
    void shouldNotBuyInPaymentWithOneDigitInCvc() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getValidName(), getCvcWithOneDigit());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkInvalidCvc();
    }

    //f
    //TODO Изменить надпись на "Значение поля должно содержать 3 цифры"
    @Test
    void shouldNotBuyInPaymentWithTwoDigitsInCvc() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getValidName(), getCvcWithTwoDigits());
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkInvalidCvc(); //TODO Изменить надпись под полем CVC "Значение поля должно содержать 3 цифры"
    }

    //t
    //TODO Изменить надпись на "Поле обязательно для заполнения"
    @Test
    void shouldNotBuyInPaymentWithEmptyCvc() {
        CardInfo card = new CardInfo(getApprovedNumber(), getCurrentMonth(), getNextYear(), getValidName(), null);
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkRequiredField();
    }


    //f
    @Test
    void shouldNotBuyInPaymentWithAllEmptyFields() {
        CardInfo card = new CardInfo(null, null, null, null, null);
        val firstPage = new FirstPage();
        val payment = firstPage.buy();

        payment.fulfillData(card);
        payment.checkAllFieldsAreRequired(); //TODO Изменить надписи под полями на "Поле обязательно для заполнения"
    }

}



