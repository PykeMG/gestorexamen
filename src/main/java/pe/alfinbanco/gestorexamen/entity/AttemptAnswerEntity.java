package pe.alfinbanco.gestorexamen.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "attempt_answers")
public class AttemptAnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="attempt_id", nullable=false)
    private ExamAttemptEntity attempt;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="question_id", nullable=false)
    private QuestionEntity question;

    @Column(name="selected_option_id")
    private Long selectedOptionId;

    @Column(name="correct_option_id", nullable=false)
    private Long correctOptionId;

    @Column(name="is_correct", nullable=false)
    private boolean correct;

    public AttemptAnswerEntity() {}

    public AttemptAnswerEntity(ExamAttemptEntity attempt, QuestionEntity question, Long correctOptionId) {
        this.attempt = attempt;
        this.question = question;
        this.correctOptionId = correctOptionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExamAttemptEntity getAttempt() {
        return attempt;
    }

    public void setAttempt(ExamAttemptEntity attempt) {
        this.attempt = attempt;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public Long getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Long correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    
}
