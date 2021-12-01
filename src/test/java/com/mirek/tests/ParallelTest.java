package com.mirek.tests;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_METHOD)
public class ParallelTest {

    private static final SelenideElement logo = $("#logo");
    private static final SelenideElement link = $(withText("More information"));
    private static final int testCount = Integer.parseInt(Optional.ofNullable(System.getenv("TEST_COUNT")).orElse("10"));
    private static final int testCycles = Integer.parseInt(Optional.ofNullable(System.getenv("TEST_CYCLES")).orElse("10"));

    static {
        System.out.printf("Running %d test(s) with %d cycles each%n", testCount, testCycles);
    }

    public static void clickThroughPage(int testNo) {
        open("https://example.org/?firstTestNo=" + testNo);

        Stream.generate(() -> null)
            .limit(testCycles)
            .forEach($ -> {
                         link.shouldBe(visible).scrollIntoView(true).click();
                         logo.shouldBe(visible);
                         Selenide.back();
                     }
            );
    }

    @TestFactory
    Collection<DynamicTest> dynamicTest() {
        return IntStream.range(0, testCount)
            .mapToObj(i -> DynamicTest.dynamicTest(String.format("%04d", i), () -> ParallelTest.clickThroughPage(i)))
            .collect(Collectors.toList());
    }
}
