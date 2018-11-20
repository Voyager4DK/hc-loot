package dk.loej.hc.loot.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import dk.loej.hc.loot.entity.Fruit;
import dk.loej.hc.loot.entity.Player;
import dk.loej.hc.loot.repository.FruitRepository;
import dk.loej.hc.loot.repository.PlayerRepository;

@Controller
@RequestMapping(value = "api/players")
public class PlayerController {

    private final PlayerRepository repository;

    @Autowired
    public PlayerController(PlayerRepository repository) {
        this.repository = repository;
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAll() {
        Iterable<Player> players = repository.findAllByOrderByGloryPointsDesc();
        for (Player player : players) {
            player.setPassword(null);
        }

        return StreamSupport
                .stream(players.spliterator(), false)
                .collect(Collectors.toList());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Player post(@RequestBody(required = false) Player player) {
        verifyCorrectPayload(player);

        return repository.save(player);
    }

    @ResponseBody
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Player get(@PathVariable("id") Integer id) {
        verifyPlayerExists(id);

        return repository.findOne(id);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Player put(@PathVariable("id") Integer id, @RequestBody(required = false) Player player) {
        verifyPlayerExists(id);
        verifyCorrectPayload(player);

        player.setId(id);
        return repository.save(player);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        verifyPlayerExists(id);

        repository.delete(id);
    }

    private void verifyPlayerExists(Integer id) {
        if (!repository.exists(id)) {
            throw new RuntimeException(String.format("Player with id=%d was not found", id));
        }
    }

    private void verifyCorrectPayload(Player player) {
        if (Objects.isNull(player)) {
            throw new RuntimeException("Player cannot be null");
        }

        if (!Objects.isNull(player.getId())) {
            throw new RuntimeException("Id field must be generated");
        }
    }
}
