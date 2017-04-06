package io.github.solomkinmv.glossary.web.controller;

import io.github.solomkinmv.glossary.persistence.model.*;
import io.github.solomkinmv.glossary.service.domain.UserDictionaryService;
import io.github.solomkinmv.glossary.service.domain.WordService;
import io.github.solomkinmv.glossary.service.search.SearchService;
import io.github.solomkinmv.glossary.web.MockMvcBase;
import io.github.solomkinmv.glossary.web.dto.WordMetaDto;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.transaction.Transactional;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for {@link WordController}
 */
@Slf4j
@Transactional
public class WordControllerTest extends MockMvcBase {

    private final List<StudiedWord> wordList = new ArrayList<>();
    @Autowired
    private WordService wordService;
    @Autowired
    private UserDictionaryService userDictionaryService;

    @Before
    public void setUp() throws Exception {
        log.info("Setting up test method");

        StudiedWord word1 = new StudiedWord("word1", "слово1");
        StudiedWord word2 = new StudiedWord("word2", "слово2");
        StudiedWord word3 = new StudiedWord("word3", "слово3");
        StudiedWord word4 = new StudiedWord("word4", "слово4");
        StudiedWord word5 = new StudiedWord("word5", "слово5");
        StudiedWord word6 = new StudiedWord("word5", "слово55");
        wordList.add(wordService.save(word1));
        wordList.add(wordService.save(word2));
        wordList.add(wordService.save(word3));
        wordList.add(wordService.save(word4));
        wordList.add(wordService.save(word5));
        wordList.add(wordService.save(word6));

        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        WordSet ws1 = new WordSet("ws1", "desc", Arrays.asList(word1, word2));
        WordSet ws2 = new WordSet("ws2", "desc", Arrays.asList(word3, word4, word5));
        WordSet ws3 = new WordSet("ws3", "desc", Collections.singletonList(word6));

        Set<WordSet> userOneSet = new HashSet<>();
        userOneSet.add(ws1);
        userOneSet.add(ws2);
        Set<WordSet> userTwoSet = new HashSet<>();
        userTwoSet.add(ws3);

        UserDictionary dict1 = new UserDictionary(userOneSet, user1);
        UserDictionary dict2 = new UserDictionary(userTwoSet, user2);

        userDictionaryService.save(dict1);
        userDictionaryService.save(dict2);
    }

    @Test
    public void getAllWords() throws Exception {
        Integer[] integers = wordList.stream().map(word -> word.getId().intValue()).limit(5).toArray(Integer[]::new);
        Matcher<Iterable<? extends Integer>> idMatcher = containsInAnyOrder(integers);

        mockMvc.perform(get("/api/words").with(userToken()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(contentType))
               .andExpect(jsonPath("$._embedded.wordResourceList", hasSize(5)))
               .andExpect(jsonPath("$._embedded.wordResourceList[*].word.id", idMatcher))
               .andExpect(jsonPath("$._embedded.wordResourceList[*].word.text", not(contains(nullValue()))))
               .andExpect(jsonPath("$._embedded.wordResourceList[*].word.translation", not(contains(nullValue()))));
    }

    @Test
    public void getWordById() throws Exception {
        StudiedWord word = wordList.get(0);

        mockMvc.perform(get("/api/words/" + word.getId()).with(userToken()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(contentType))
               .andExpect(jsonPath("$.word.id", is(word.getId().intValue())))
               .andExpect(jsonPath("$.word.text", is(word.getText())))
               .andExpect(jsonPath("$.word.translation", is(word.getTranslation())));
    }

    @Test
    public void getOtherUsersWordById() throws Exception {
        StudiedWord word = wordList.get(wordList.size() - 1);

        mockMvc.perform(get("/api/words/" + word.getId()).with(userToken()))
               .andExpect(status().isNotFound());
    }

    @Test
    public void searchForPresentWord() throws Exception {
        mockMvc.perform(get("/api/words/search")
                                .param("text", "word1")
                                .with(userToken()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(contentType))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(jsonPath("$.result.records", hasSize(1)))
               .andExpect(jsonPath("$.result.records[0].text", is(wordList.get(0).getText())))
               .andExpect(jsonPath("$.result.records[0].translations", hasSize(1)))
               .andExpect(jsonPath("$.result.records[0].translations[0]", is(wordList.get(0).getTranslation())));
    }

    @Test
    public void searchForIllegalWords() throws Exception {
        String illegalWord = "word42";
        mockMvc.perform(get("/api/words/search")
                                .param("text", illegalWord)
                                .with(userToken()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(contentType))
               .andExpect(jsonPath("$.result.records", hasSize(0)));
    }

    @Test
    public void searchForNewWords() throws Exception {
        String word = "word";
        String expectedTranslation = "слово";
        mockMvc.perform(get("/api/words/search")
                                .param("text", word)
                                .with(userToken()))
               .andDo(MockMvcResultHandlers.print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(contentType))
               .andExpect(jsonPath("$.result.records", hasSize(SearchService.SEARCH_LIMIT)))
               .andExpect(jsonPath("$.result.records[0].text", is(word)))
               .andExpect(jsonPath("$.result.records[0].translations", hasSize(1)))
               .andExpect(jsonPath("$.result.records[0].translations[0]", is(expectedTranslation)))
               .andExpect(jsonPath("$.result.records[1].text", is(wordList.get(0).getText())));
    }

    @Test
    public void updateWordMetaInformation() throws Exception {
        WordStage stage = WordStage.LEARNED;
        String image = "image42";
        WordMetaDto metaDto = new WordMetaDto(stage, image);

        mockMvc.perform(patch("/api/words/{wordId}", wordList.get(0).getId())
                                .with(userToken())
                                .contentType(contentType)
                                .content(jsonConverter.toJson(metaDto)))
               .andExpect(status().isOk());

        Optional<StudiedWord> optionalUpdatedWord = wordService.getById(wordList.get(0).getId());

        assertTrue(optionalUpdatedWord.isPresent());

        StudiedWord updatedWord = optionalUpdatedWord.get();
        assertEquals(stage, updatedWord.getStage());
        assertEquals(image, updatedWord.getImage());
    }
}