package pe.alfinbanco.gestorexamen.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_attempts")
public class ExamAttemptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private UserEntity user;

    @Column(name="total_questions", nullable=false)
    private int totalQuestions;

    @Column(name="time_limit_minutes", nullable=false)
    private int timeLimitMinutes;

    @Column(name="started_at", nullable=false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name="finished_at")
    private LocalDateTime finishedAt;

    @Column
    private Double score;

    @Column(name="correct_count")
    private Integer correctCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    public ExamAttemptEntity() {}

    public ExamAttemptEntity(UserEntity user, int totalQuestions, int timeLimitMinutes) {
        this.user = user;
        this.totalQuestions = totalQuestions;
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(int timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    
}
