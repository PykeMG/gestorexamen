package pe.alfinbanco.gestorexamen.entity;

public enum AttemptStatus {
    IN_PROGRESS, FINISHED, CANCELED;

    public String getDisplayName() {
        return switch (this) {
            case IN_PROGRESS -> "en progreso";
            case FINISHED -> "terminado";
            case CANCELED -> "cancelado";
        };
    }
}
