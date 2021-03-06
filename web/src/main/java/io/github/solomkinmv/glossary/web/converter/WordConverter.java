package io.github.solomkinmv.glossary.web.converter;

import io.github.solomkinmv.glossary.persistence.model.StudiedWord;
import io.github.solomkinmv.glossary.web.dto.WordDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class WordConverter {
    public WordDto toDto(StudiedWord studiedWord) {
        return new WordDto(
                studiedWord.getId(),
                studiedWord.getText(),
                studiedWord.getTranslation(),
                studiedWord.getStage(),
                studiedWord.getImage(),
                studiedWord.getSound()
        );
    }

    public StudiedWord toModel(WordDto wordDto) {
        StudiedWord studiedWord = new StudiedWord();
        BeanUtils.copyProperties(wordDto, studiedWord);
        return studiedWord;
    }
}
