package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.val;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$$;

public class FirstPage {
    private final SelenideElement heading = $$("h2").findBy(Condition.text("Путешествие дня"));
    private final SelenideElement buyButton = $$("button").findBy(Condition.text("Купить"));
    private final SelenideElement creditButton = $$("button").findBy(Condition.text("Купить в кредит"));

    public FirstPage() {
        heading.shouldBe(Condition.visible);
    }

    public Payment buy() {
        buyButton.click();
        return new Payment();

    }

    public Credit buyInCredit() {
        creditButton.click();
        return new Credit();
    }
}