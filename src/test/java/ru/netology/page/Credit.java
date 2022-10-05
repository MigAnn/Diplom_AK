package ru.netology.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.CardInfo;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Credit {
    private SelenideElement heading = $$("h3").findBy(Condition.text("Кредит по данным карты"));
    private SelenideElement cardNumberField = $("input[placeholder='0000 0000 0000 0000']");
    private SelenideElement monthField = $("input[placeholder='08']");
    private SelenideElement yearField = $("input[placeholder='22']");
    private SelenideElement holderField = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
    private SelenideElement cvcField = $("input[placeholder='999']");
    private SelenideElement continueButton = $$("button").findBy(Condition.text("Продолжить"));


    public Credit() {
        heading.shouldBe(visible);
    }

    public void fulfillData(CardInfo card) {
        cardNumberField.setValue(card.getNumber());
        monthField.setValue(card.getMonth());
        yearField.setValue(card.getYear());
        holderField.setValue(card.getHolderName());
        cvcField.setValue(card.getCvc());
        continueButton.click();

    }

    public void withoutFulfilling() {
        continueButton.click();
    }

    public void checkSuccessNotification() {
        $(".notification_status_ok").shouldBe(visible, Duration.ofMillis(15000));
    }

    public void checkDeclineNotification() {
        $("notification_status_error").shouldBe(visible, Duration.ofMillis(15000));
    }

    public void checkInvalidFormat() {
        $(".input__sub").shouldHave(exactText("Неверный формат")).shouldBe(visible);
    }

    public void checkRequiredField() {
        $(".input__sub").shouldHave(exactText("Поле обязательно для заполнения")).shouldBe(visible);

    }

    public void checkInvalidDate() {
        $(".input__sub").shouldHave(exactText("Неверно указан срок действия карты")).shouldBe(visible);
    }

    public void checkExpiredDate() {
        $(".input__sub").shouldHave(exactText("Истёк срок действия карты")).shouldBe(visible);
    }

    public void checkInvalidName() {
        $(".input__sub").shouldHave(exactText("Введите полное имя и фамилию")).shouldBe(visible);
    }

    public void checkLongName() {
        $(".input__sub").shouldHave(exactText("Значение поля не может содержать более 100 символов")).shouldBe(visible);
    }

    public void checkInvalidDataName() {
        $(".input__sub").shouldHave(exactText("Значение поля может содержать только буквы и дефис")).shouldBe(visible);
    }

    public void checkShortName() {
        $(".input__sub").shouldHave(exactText("Значение поля должно содержать больше одной буквы")).shouldBe(visible);
    }

    public void checkInvalidCvc() {
        $(".input__sub").shouldHave(exactText("Значение поля должно содержать 3 цифры")).shouldBe(visible);
    }

    public void checkAllFieldsAreRequired() {
        $$(".input__sub").shouldHave(CollectionCondition.size(5)).shouldHave(CollectionCondition.texts("Поле обязательно для заполнения"));
    }
}

