import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

class CardDeliveryTest {

    private static final Duration WAIT = Duration.ofSeconds(15);

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    private void clearDate(SelenideElement dateInput) {
        var selectAll = System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL;
        dateInput.sendKeys(Keys.chord(selectAll, "a"));
        dateInput.sendKeys(Keys.BACK_SPACE);
    }

    @Test
    @DisplayName("Должен успешно запланировать, а затем перепланировать встречу")
    void shouldPlanAndReplanMeeting() {
        var user = DataGenerator.Registration.generateUser("ru");
        var firstMeetingDate = DataGenerator.generateDate(3);   // сегодня + 3 дня
        var secondMeetingDate = DataGenerator.generateDate(4);  // сегодня + 4 дня

        var dateInput = $("[data-test-id=date] input");

        $("[data-test-id=city] input").setValue(user.getCity());
        clearDate(dateInput);
        dateInput.setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $$("button").find(exactText("Запланировать")).click();

        $("[data-test-id=success-notification] .notification__content")
                .shouldBe(visible, WAIT)
                .shouldHave(text("Встреча успешно запланирована на " + firstMeetingDate));

        clearDate(dateInput);
        dateInput.setValue(secondMeetingDate);
        $$("button").find(exactText("Запланировать")).click();

        $("[data-test-id=replan-notification]")
                .shouldBe(visible, WAIT)
                .shouldHave(text("Необходимо подтверждение"))
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $("[data-test-id=replan-notification] button").click();

        $("[data-test-id=success-notification] .notification__content")
                .shouldBe(visible, WAIT)
                .shouldHave(text("Встреча успешно запланирована на " + secondMeetingDate));
    }
}
