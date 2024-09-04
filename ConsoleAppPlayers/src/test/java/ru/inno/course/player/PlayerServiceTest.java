package ru.inno.course.player;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.inno.course.player.ext.MyTestWatcher;
import ru.inno.course.player.ext.PlayersAndPointsProvider;
import ru.inno.course.player.ext.PointsProvider;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

// 1. Тест не должен настраивать свое окружение.
// 2. Главный код ничего не знает про тесты.
// 3. В тестах не должно быть if'ов.

@ExtendWith(MyTestWatcher.class)
public class PlayerServiceTest {
    private PlayerService service;
    private static final String NICKNAME = "Nikita";

    // hooks - хуки
    @BeforeEach
    public void setUp() {
        service = new PlayerServiceImpl();
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));
    }

    @Test
    @Tag("позитивные")
    @Disabled("Jira-123")
    @DisplayName("Создаем игрока и проверяем его значения по дефолту")
    public void iCanAddNewPlayer() {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());

        int nikitaId = service.createPlayer(NICKNAME);
        Player playerById = service.getPlayerById(nikitaId);

        assertEquals(nikitaId, playerById.getId());
        assertEquals(0, playerById.getPoints());
        assertEquals(NICKNAME, playerById.getNick());
        assertTrue(playerById.isOnline());
    }

    @Test
    @Tag("негативный")
    @DisplayName("Нельзя создать дубликат игрока")
    public void iCannotCreateADuplicate() {
        service.createPlayer(NICKNAME);
        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(NICKNAME));
    }

    @Test
    @Tags({@Tag("негативный"), @Tag("CRITICAL")})
    @DisplayName("Нельзя получить несуществующего пользователя")
    public void iCannotGetEmptyUser() {
        assertThrows(IllegalArgumentException.class, () -> service.getPlayerById(9999));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, -50, 0, 100, -5000000})
    @DisplayName("Добавление очков игроку")
    public void iCanAddPoints(int points) {
        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, points);
        Player playerById = service.getPlayerById(playerId);
        assertEquals(points, playerById.getPoints());
    }

    @ParameterizedTest
    @ArgumentsSource(PointsProvider.class)
    @DisplayName("Добавление очков игроку")
    public void iCanAddPoints2(int pointsToAdd, int pointsToBe) {
        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, pointsToAdd);
        Player playerById = service.getPlayerById(playerId);
        assertEquals(pointsToBe, playerById.getPoints());
    }

    @ParameterizedTest
    @ArgumentsSource(PlayersAndPointsProvider.class)
    @DisplayName("Добавление очков игроку c ненулевым балансом")
    public void iCanAddPoints3(Player player, int pointsToAdd, int pointsToBe) {
        int id = service.createPlayer(player.getNick());
        service.addPoints(id, player.getPoints());

        service.addPoints(id, pointsToAdd);
        Player playerById = service.getPlayerById(id);
        assertEquals(pointsToBe, playerById.getPoints());
    }

}
