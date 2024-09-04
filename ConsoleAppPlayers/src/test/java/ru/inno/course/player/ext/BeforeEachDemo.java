package ru.inno.course.player.ext;

import org.junit.jupiter.api.extension.*;

public class BeforeEachDemo implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        System.out.println("Конец тестов");
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        System.out.println("Погнали тесты");
    }
}
