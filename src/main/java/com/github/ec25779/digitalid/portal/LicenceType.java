package com.github.ec25779.digitalid.portal;

public enum LicenceType {

    PROVISIONAL(16),
    FULL(17);

    private final int minimumAge;

    LicenceType(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

}
