package pe.alfinbanco.gestorexamen.dto;

public record ReviewRowDto (
    Long questionId,
    String statement,
    Long selectedOptionId,
    String selectedOptionText,
    Long correctOptionId,
    String correctOptionText,
    boolean correct
){}
