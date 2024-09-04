package ru.inno.course.player.ext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.helpers.AnnotationHelper;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class PlayerServiceResolver implements ParameterResolver, AfterAllCallback {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(PlayerService.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        // спрашиваем аннотацию Players
        Players annotation = AnnotationHelper.findAnnotation(parameterContext.getAnnotatedElement(), Players.class);
        // забираем значение из скобок @Players(0)
        int num = annotation.playerNumber();

        // генерим Х игроков
        Set<Player> generated = PlayerGenerator.generate(num);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Path.of("./data.json").toFile(), generated);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new PlayerServiceImpl();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        Files.deleteIfExists(Path.of("./data.json"));
    }
}