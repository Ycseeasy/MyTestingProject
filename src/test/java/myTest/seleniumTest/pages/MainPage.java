package myTest.seleniumTest.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$x;

/**
 * Главная странница сайта demoqa.com
 */
public class MainPage {

    private final ElementsCollection cardCollection = $$x("//div[@class='card-body']");

    public void clickOnCard(String cardName) {
        SelenideElement element = cardCollection.filterBy(text(cardName))
                .first();
        element.click();
    }

    public void openSite() {
        Selenide.open("https://demoqa.com/");
    }
}
