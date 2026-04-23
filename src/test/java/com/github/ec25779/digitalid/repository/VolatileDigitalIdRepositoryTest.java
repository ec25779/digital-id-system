package com.github.ec25779.digitalid.repository;

import org.jetbrains.annotations.NotNull;

public class VolatileDigitalIdRepositoryTest extends AbstractDigitalIdRepositoryTest {

    @Override
    protected @NotNull DigitalIdRepository createRepository() {
        return new VolatileDigitalIdRepository();
    }

}
