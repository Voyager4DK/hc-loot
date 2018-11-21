package dk.loej.hc.loot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import static org.springframework.util.StringUtils.isEmpty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import dk.loej.hc.loot.entity.LoginData;
import dk.loej.hc.loot.entity.Player;
import dk.loej.hc.loot.repository.PlayerRepository;

@Controller
@RequestMapping(value = "api/login")
public class LoginController {
	
	private final PlayerRepository repository;

    @Autowired
    public LoginController(PlayerRepository repository) {
        this.repository = repository;
    }
    
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Player post(@RequestBody(required = true) LoginData loginData) throws LoginFailedException {
    	Player player = repository.findOne(loginData.getPlayerId());
    	
    	if (loginData.getPassword().equals("undefined") && isEmpty(player.getPassword()) || loginData.getPassword().equals(player.getPassword()) ) {
    		return player;
    	} else {
    		throw new LoginFailedException("Wrong password!");
    	}    	
    }
}
