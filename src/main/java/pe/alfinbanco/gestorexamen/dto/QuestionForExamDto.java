package pe.alfinbanco.gestorexamen.dto;

import java.util.List;

public record QuestionForExamDto(
    Long questionId,
    String statement,
    List<OptionDto> options
) {
    public record OptionDto(
        Long optionId,
        String text
    ) {}
}
