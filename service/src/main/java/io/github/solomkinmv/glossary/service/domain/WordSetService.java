package io.github.solomkinmv.glossary.service.domain;

import io.github.solomkinmv.glossary.persistence.model.StudiedWord;
import io.github.solomkinmv.glossary.persistence.model.WordSet;

import java.util.List;
import java.util.Optional;

/**
 * Describes methods to interact with {@link StudiedWord}.
 */
public interface WordSetService extends CRUDService<WordSet, Long> {
    List<WordSet> listByUsername(String username);

    Optional<WordSet> getByIdAndUsername(Long id, String username);

    WordSet saveForUser(String username, WordSet wordSet);

    void deleteByIdAndUsername(Long wordSetId, String username);

    void deleteWordFromWordSetByIdAndUsername(Long wordId, Long wordSetId, String username);

    StudiedWord addWordToWordSet(StudiedWord word, Long wordSetId, String username);

    void updateWordSetMeta(WordSet wordSet, String name, String description);
}
