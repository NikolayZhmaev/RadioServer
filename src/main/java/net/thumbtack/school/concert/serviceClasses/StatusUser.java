package net.thumbtack.school.concert.serviceClasses;

// Напишем класс в котором будут хранится состояния зарегистрированного пользователя.

public enum StatusUser {
    ACTIVE,
    INACTIVE,
    REMOVED;

    public static StatusUser fromString(String statusString) {
        if (statusString == null) {
            return null;
        }
        switch (statusString) {
            case "ACTIVE":
                return StatusUser.ACTIVE;
            case "INACTIVE":
                return StatusUser.INACTIVE;
            case "REMOVED":
                return StatusUser.REMOVED;
            default:
                return null;
        }
    }
}