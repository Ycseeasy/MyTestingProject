package myTest.seleniumTest;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ElementsPage {

    private final ElementsCollection boxCollection = $$("li[class='btn btn-light ']");

    /**
     * Проверка отображения поля Text Box на страничке сайта
     */
    public Boolean checkDirection() {
        SelenideElement element = boxCollection.filterBy(text("Text Box"))
                .first();
        return element.isDisplayed();
    }

    /**
     * Проверка отображения клетки заданного поля
     *
     * @param boxName Название поля
     * @return Название отображенной клетки
     */
    public SelenideElement checkBoxVisual(String boxName) {
        SelenideElement element = boxCollection.filterBy(text(boxName))
                .first();
        element.click();
        return $("h1[class='text-center']");
    }

    /**
     * Ввод данных в поля Text Box с последующей записью ответа.
     *
     * @param data Данные, которые были записаны в поля
     * @return Данные, которые возвращает метод
     */
    public List<String> checkTestBoxFunctional(List<String> data) {
        SelenideElement nameBar = $("input[id='userName']");
        SelenideElement emailBar = $("input[id='userEmail']");
        SelenideElement addressBar = $("textarea[id='currentAddress']");
        SelenideElement permanentAddressBar = $("textarea[id='permanentAddress']");
        nameBar.setValue(data.get(0));
        emailBar.setValue(data.get(1));
        addressBar.setValue(data.get(2));
        permanentAddressBar.setValue(data.get(3));

        SelenideElement submit = $("button[id='submit']");
        submit.click();

        ElementsCollection result = $$("p[class='mb-1']");

        return result.stream()
                .map(SelenideElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Запрос видимых директорий в Check box при нажатии клавиши "Открыть все"
     */
    public List<SelenideElement> checkExpandAll() {
        $("button[aria-label='Expand all']").click();

        List<SelenideElement> listElement = new ArrayList<>();
        listElement.add($x("//span[text()='Commands']"));
        listElement.add($x("//span[text()='Veu']"));
        listElement.add($x("//span[text()='General']"));
        listElement.add($x("//span[text()='Excel File.doc']"));

        return listElement;
    }

    /**
     * Проверка видимого отображения директорий в Check box при нажатии клавиши "Скрыть все"
     */
    public Boolean checkCollapseAll() {
        $("button[aria-label='Collapse all']").click();

        SelenideElement arg0 = $x("//span[text()='Commands']");
        SelenideElement arg1 = $x("//span[text()='Veu']");
        SelenideElement arg2 = $x("//span[text()='General']");
        SelenideElement arg3 = $x("//span[text()='Excel File.doc']");

        return !arg0.isDisplayed() && !arg1.isDisplayed() && !arg2.isDisplayed() && !arg3.isDisplayed();
    }

    /**
     * Проверка видимого отображения директорий в Check box при нажатии клавиши "Раскрыть список" рядом с Home
     */
    public List<SelenideElement> checkToggle() {
        $("button[aria-label='Toggle']").click();

        List<SelenideElement> listElement = new ArrayList<>();
        listElement.add($x("//span[text()='Desktop']"));
        listElement.add($x("//span[text()='Documents']"));
        listElement.add($x("//span[text()='Downloads']"));

        return listElement;
    }

    /**
     * Проверка выделения и поиска директорий в Check Box
     */
    public List<String> checkCheckBox(String directoryName) {
        $("button[aria-label='Expand all']").click();
        $x("//span[text()='" + directoryName + "']").click();

        return $$("span[class='text-success']").stream()
                .map(SelenideElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Проверка, что кнопка "Yes" доступна для нажатия
     *
     * @return Ответ с сайта с названием нажатой кнопки
     */
    public String checkYesButton() {
        $("label[for='yesRadio']").click();
        return $("span[class='text-success']").getText();
    }

    /**
     * Проверка, что кнопка "No" недоступна для нажатия
     */
    public Boolean checkDisabledElement() {
        return $("#noRadio").is(disabled) ||
                $("label[for='noRadio']").has(cssClass("disabled")) ||
                $("#noRadio").parent().has(cssClass("disabled"));
    }

    /**
     * Проверка, что после регистрации в системе возвращается пользователь с веденными данными
     *
     * @param data Данные пользователя
     * @return Ответ с таблицы на сайте
     */
    public Map<String, String> checkRegistrationForm(Map<String, String> data) {
        $("button[id='addNewRecordButton']").click();
        SelenideElement nameBar = $("input[id='firstName']");
        SelenideElement lastNameBar = $("input[id='lastName']");
        SelenideElement emailBar = $("input[id='userEmail']");
        SelenideElement ageBar = $("input[id='age']");
        SelenideElement salaryBar = $("input[id='salary']");
        SelenideElement departmentBar = $("input[id='department']");

        nameBar.setValue(data.get("name"));
        lastNameBar.setValue(data.get("lastName"));
        emailBar.setValue(data.get("email"));
        ageBar.setValue(data.get("age"));
        salaryBar.setValue(data.get("salary"));
        departmentBar.setValue(data.get("department"));
        $("button[id='submit']").click();

        ElementsCollection rows = $$(".rt-tr-group .rt-tr");
        Map<String, String> rowData = new LinkedHashMap<>();

        for (SelenideElement row : rows) {
            ElementsCollection cells = row.$$(".rt-td");
            boolean containText = cells.texts().stream()
                    .anyMatch(cellText -> cellText.contains(data.get("name")));
            if (containText) {
                rowData.put("name", cells.get(0).text());
                rowData.put("lastName", cells.get(1).text());
                rowData.put("age", cells.get(2).text());
                rowData.put("email", cells.get(3).text());
                rowData.put("salary", cells.get(4).text());
                rowData.put("department", cells.get(5).text());
            }
        }
        return rowData;
    }

    /**
     * Проверка строки поиска
     * @param data слово по которому мы ищем пользователя
     */
    public boolean checkVisualInTable(String data) {
        $("input[placeholder='Type to search']").setValue(data);
        return $x("//div[text()='" + data + "']").isDisplayed();
    }
}
