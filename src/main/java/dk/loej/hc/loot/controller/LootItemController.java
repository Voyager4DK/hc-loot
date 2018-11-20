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

import dk.loej.hc.loot.entity.LootItem;
import dk.loej.hc.loot.repository.LootItemRepository;

@Controller
@RequestMapping(value = "api/loot_items")
public class LootItemController {

    private final LootItemRepository repository;

    @Autowired
    public LootItemController(LootItemRepository repository) {
        this.repository = repository;
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItem post(@RequestBody(required = false) LootItem lootItem) {
        verifyCorrectPayload(lootItem);

        return repository.save(lootItem);
    }

    @ResponseBody
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItem get(@PathVariable("id") Integer id) {
        verifyLootItemExists(id);

        return repository.findOne(id);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LootItem put(@PathVariable("id") Integer id, @RequestBody(required = false) LootItem lootItem) {
        verifyLootItemExists(id);
        verifyCorrectPayload(lootItem);

        lootItem.setId(id);
        return repository.save(lootItem);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        verifyLootItemExists(id);

        repository.delete(id);
    }

    private void verifyLootItemExists(Integer id) {
        if (!repository.exists(id)) {
            throw new RuntimeException(String.format("LootItem with id=%d was not found", id));
        }
    }

    private void verifyCorrectPayload(LootItem lootItem) {
        if (Objects.isNull(lootItem)) {
            throw new RuntimeException("LootItem cannot be null");
        }

        if (!Objects.isNull(lootItem.getId())) {
            throw new RuntimeException("Id field must be generated");
        }
    }
}