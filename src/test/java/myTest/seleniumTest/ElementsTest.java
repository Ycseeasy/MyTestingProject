package myTest.seleniumTest;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ElementsTest extends BaseTest {

    private final MainPage mainPage = new MainPage();
    private final ElementsPage elementsPage = new ElementsPage();

    /**
     * Проверка видимости Функций Elements, после нажатия на карточку
     */
    @Test
    public void testClickOnElementsButton() {
        mainPage.openSite();
        mainPage.clickOnCard("Elements");
        Assertions.assertTrue(elementsPage.checkDirection());
    }

    /**
     * Проверка функционала Test Box
     */
    @Test
    public void testTextBox() {
        mainPage.openSite();
        mainPage.clickOnCard("Elements");
        List<String> date = List.of("Jhon", "jhon@jhon.ru",
                "street 12", "street 13");
        SelenideElement result = elementsPage.checkBoxVisual("Text Box");

        Assertions.assertEquals(result.getText(), "Text Box");
        Assertions.assertTrue(result.isDisplayed());

        List<String> list = elementsPage.checkTestBoxFunctional(date)
                .stream()
                .map(x -> x.replaceAll(".*:(.*)", "$1").trim())
                .toList();

        for (int i = 0; i < list.size(); i++) {
            Assertions.assertEquals(list.get(i), (date.get(i)), list.get(i) + " не равно " + list.get(i));
        }
    }

    /**
     * Проверка функционала Check Box
     */
    @Test
    public void testCheckBox() {
        mainPage.openSite();
        mainPage.clickOnCard("Elements");
        SelenideElement result = elementsPage.checkBoxVisual("Check Box");

        Assertions.assertEquals(result.getText(), "Check Box");
        Assertions.assertTrue(result.isDisplayed());

        List<SelenideElement> listExpand = elementsPage.checkExpandAll();
        for (SelenideElement selenideElement : listExpand) {
            Assertions.assertTrue(selenideElement.isDisplayed(), selenideElement.getText()
                    + " не отображается на страннице.");
        }
        Assertions.assertTrue(elementsPage.checkCollapseAll());

        List<SelenideElement> listToggle = elementsPage.checkToggle();
        for (SelenideElement selenideElement : listToggle) {
            Assertions.assertTrue(selenideElement.isDisplayed(), selenideElement.getText()
                    + " не отображается на страннице.");
        }
        Assertions.assertTrue(elementsPage.checkCheckBox("General").contains("general"));
        Assertions.assertTrue(elementsPage.checkCheckBox("Office").contains("office"));
        Assertions.assertTrue(elementsPage.checkCheckBox("Home").contains("home"));
    }

    /**
     * Проверка функционала Radio Button
     */
    @Test
    public void testRadioButton() {
        mainPage.openSite();
        mainPage.clickOnCard("Elements");
        SelenideElement result = elementsPage.checkBoxVisual("Radio Button");

        Assertions.assertEquals(result.getText(), "Radio Button");
        Assertions.assertTrue(result.isDisplayed());

        String yesRequest = elementsPage.checkYesButton();
        Assertions.assertTrue(yesRequest.contains("Yes"), "Текст " + yesRequest + " не содержит Yes");
        Assertions.assertTrue(elementsPage.checkDisabledElement());
    }

    /**
     * Проверка функционала Web Tables
     */
    @Test
    public void testWebTables() {
        mainPage.openSite();
        mainPage.clickOnCard("Elements");
        SelenideElement result = elementsPage.checkBoxVisual("Web Tables");

        Assertions.assertEquals(result.getText(), "Web Tables");
        Assertions.assertTrue(result.isDisplayed());

        Map<String, String> data = new LinkedHashMap<>();
        data.put("name", "Lazarus");
        data.put("lastName", "Doe");
        data.put("age", "47");
        data.put("email", "lazarus.doe@example.ru");
        data.put("salary", "199");
        data.put("department", "management");

        Map<String, String> resultMap = elementsPage.checkRegistrationForm(data);

        Assertions.assertEquals(data.get("name"), resultMap.get("name"));
        Assertions.assertEquals(data.get("lastName"), resultMap.get("lastName"));
        Assertions.assertEquals(data.get("age"), resultMap.get("age"));
        Assertions.assertEquals(data.get("email"), resultMap.get("email"));
        Assertions.assertEquals(data.get("salary"), resultMap.get("salary"));
        Assertions.assertEquals(data.get("department"), resultMap.get("department"));

        Assertions.assertTrue(elementsPage.checkVisualInTable("lazarus.doe@example.ru"));
    }
}
