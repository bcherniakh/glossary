package io.github.solomkinmv.glossary.words.service.practice.repetition;

import io.github.solomkinmv.glossary.words.persistence.domain.Word;
import io.github.solomkinmv.glossary.words.persistence.domain.WordStage;
import io.github.solomkinmv.glossary.words.service.practice.AbstractPracticeService;
import io.github.solomkinmv.glossary.words.service.practice.handler.PracticeResultsHandler;
import io.github.solomkinmv.glossary.words.service.word.WordService;
import io.github.solomkinmv.glossary.words.service.wordset.WordSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepetitionPracticeServiceImpl extends AbstractPracticeService<RepetitionTest> implements RepetitionPracticeService {

    @Autowired
    protected RepetitionPracticeServiceImpl(WordSetService wordSetService, WordService wordService, PracticeResultsHandler practiceResultsHandler) {
        super(wordSetService, wordService);
    }

    @Override
    protected RepetitionTest generateTest(List<Word> words, boolean originQuestions) {
        List<Word> learnedWords = words.stream()
                                       .filter(word -> WordStage.LEARNED.equals(word.getStage()))
                                       .collect(Collectors.toList());
        return new RepetitionTest(learnedWords);
    }
}