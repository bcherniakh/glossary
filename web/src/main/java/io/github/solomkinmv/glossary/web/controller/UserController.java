package io.github.solomkinmv.glossary.web.controller;

import io.github.solomkinmv.glossary.persistence.model.Topic;
import io.github.solomkinmv.glossary.persistence.model.UserDictionary;
import io.github.solomkinmv.glossary.service.domain.UserDictionaryService;
import io.github.solomkinmv.glossary.web.security.model.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

/**
 * Endpoint for all operations with user.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserDictionaryService userDictionaryService;


    @Autowired
    public UserController(UserDictionaryService userDictionaryService) {
        this.userDictionaryService = userDictionaryService;
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    String test() {
        return "hello";
    }

    @RequestMapping(value = "/topics", method = RequestMethod.GET)
    @ResponseBody
    Topic topics(Principal principal) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) ((Authentication) principal).getPrincipal();

        String username = authenticatedUser.getUsername();
        Set<Topic> topics = userDictionaryService.getByUsername(username)
                                                 .map(UserDictionary::getTopics)
                                                 .orElseThrow(() -> new UsernameNotFoundException(
                                                         "No such username: " + username));
        Optional<Topic> topic = topics.stream()
                                      .findAny();

        return topic.orElse(null);
    }
}