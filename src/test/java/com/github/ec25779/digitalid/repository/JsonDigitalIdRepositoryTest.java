package com.github.ec25779.digitalid.repository;

import com.github.ec25779.digitalid.model.DigitalId;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonDigitalIdRepositoryTest extends AbstractDigitalIdRepositoryTest {

    @TempDir
    private Path tempDir;

    @Override
    protected @NotNull DigitalIdRepository createRepository() {
        return new JsonDigitalIdRepository(tempDir.resolve("ids.json").toFile());
    }

    @Test
    public void testDigitalIdPersists() {
        DigitalIdRepository repo = createRepository();
        DigitalId digitalId = createNewId();
        repo.save(digitalId);

        repo = createRepository();
        Optional<DigitalId> result = repo.find(digitalId.getId());
        assertTrue(result.isPresent());
        assertEquals(digitalId, result.get());
    }

}
