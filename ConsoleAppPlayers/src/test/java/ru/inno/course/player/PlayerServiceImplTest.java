package ru.inno.course.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.inno.course.player.data.DataProvider;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlayerServiceImplTest {

    private PlayerServiceImpl playerService;
    private DataProvider mockDataProvider;

    @BeforeEach
    public void setUp() {
        mockDataProvider = mock(DataProvider.class);
        playerService = new PlayerServiceImpl(mockDataProvider);
    }

    @Test
    public void testAddPlayer_CheckInList() {
        Player player = new Player("John", "Doe");
        playerService.addPlayer(player);

        verify(mockDataProvider).addPlayer(player);
        List<Player> players = playerService.getAllPlayers();
        assertTrue(players.contains(player));
    }

    @Test
    public void testAddAndRemovePlayer_CheckAbsence() {
        Player player = new Player("John", "Doe");
        playerService.addPlayer(player);

        playerService.removePlayer(player.getId());

        List<Player> players = playerService.getAllPlayers();
        assertFalse(players.contains(player));
    }

    @Test
    public void testAddPlayer_NoJsonFile() {
        when(mockDataProvider.getPlayers()).thenReturn(Collections.emptyList());
        Player player = new Player("NewPlayer", "Doe");

        playerService.addPlayer(player);

        verify(mockDataProvider).addPlayer(player);
        assertEquals(1, playerService.getAllPlayers().size());
    }

    @Test
    public void testAddPlayer_WithJsonFile() {
        Player player = new Player("Player", "Doe");
        when(mockDataProvider.getPlayers()).thenReturn(Collections.singletonList(player));

        playerService.addPlayer(new Player("NewPlayer", "Doe"));

        verify(mockDataProvider, times(2)).addPlayer(any(Player.class));
        assertEquals(2, playerService.getAllPlayers().size());
    }

    @Test
    public void testAddPointsToExistingPlayer() {
        Player player = new Player("John", "Doe");
        playerService.addPlayer(player);
        playerService.addPoints(player.getId(), 10);

        assertEquals(10, playerService.getPlayerById(player.getId()).getPoints());
    }

    @Test
    public void testAddPointsToExistingPlayer_AccumulatePoints() {
        Player player = new Player("John", "Doe");
        playerService.addPlayer(player);
        playerService.addPoints(player.getId(), 10);
        playerService.addPoints(player.getId(), 5);

        assertEquals(15, playerService.getPlayerById(player.getId()).getPoints());
    }

    @Test
    public void testGetPlayerById() {
        Player player = new Player("John", "Doe");
        playerService.addPlayer(player);

        Player retrievedPlayer = playerService.getPlayerById(player.getId());
        assertEquals(player.getId(), retrievedPlayer.getId());
    }

    @Test
    public void testSaveToFile_Correctness() {
        // Здесь нужно проверить, что данные корректно сохраняются
        // Это может потребовать замокать файловую систему
    }

    @Test
    public void testLoadJsonFile_Correctness() {
        // Тестирование загрузки из json файла
    }

    @Test
    public void testUniqueId() {
        for (int i = 0; i < 5; i++) {
            playerService.addPlayer(new Player("Player" + i, "Doe"));
        }
        playerService.removePlayer(3); // Удаляем игрока с id 3
        Player newPlayer = new Player("NewPlayer", "Doe");
        playerService.addPlayer(newPlayer);

        int newId = newPlayer.getId();
        assertEquals(6, newId); // Проверяем, что id нового игрока равен 6
    }

    @Test
    public void testGetPlayers_NoJsonFile() {
        when(mockDataProvider.getPlayers()).thenReturn(Collections.emptyList());
        List<Player> players = playerService.getAllPlayers();
        assertTrue(players.isEmpty());
    }

    @Test
    public void testCreatePlayer_MaxCharacters() {
        assertThrows(IllegalArgumentException.class, () -> {
            playerService.addPlayer(new Player("A very long nickname", "Doe"));
        });
    }

    // Негативные тесты

    @Test
    public void testRemoveNonExistentPlayer() {
        assertThrows(IllegalArgumentException.class, () -> {
            playerService.removePlayer(10); // Предполагается, что id 10 не существует
        });
    }

    @Test
    public void testDuplicatePlayerNickname() {
        playerService.addPlayer(new Player("John", "Doe"));
        assertThrows(IllegalArgumentException.class, () -> {
            playerService.addPlayer(new Player("John", "Smith")); // Дубликат ника
        });
    }

    @Test
    public void testGetNonExistentPlayerById() {
        assertThrows(IllegalArgumentException.class, () -> {
            playerService.getPlayerById(999); // id 999 не существует
        });
    }
}