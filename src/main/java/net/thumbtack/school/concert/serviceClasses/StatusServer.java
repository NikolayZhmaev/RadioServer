package net.thumbtack.school.concert.serviceClasses;

public enum StatusServer {
    ACTIVE,
    INACTIVE;

    public static StatusServer fromString(String statusString) {
        if (statusString == null) {
            return null;
        }

        switch (statusString) {
            case "ACTIVE":
                return StatusServer.ACTIVE;
            case "INACTIVE":
                return StatusServer.INACTIVE;
            default:
                return null;
        }
    }
}