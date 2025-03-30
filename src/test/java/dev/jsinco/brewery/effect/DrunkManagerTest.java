package dev.jsinco.brewery.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class DrunkManagerTest {

    private DrunkManager drunkManager;
    private UUID playerUuid = UUID.randomUUID();

    @BeforeEach
    void setup() {
        this.drunkManager = new DrunkManager(20);
    }

    @Test
    void consume() {
        drunkManager.consume(playerUuid, 10, 0, 0);
        assertEquals(new DrunkState(10, 0, 0), drunkManager.getDrunkState(playerUuid));
        drunkManager.consume(playerUuid, 0, 0, 20);
        assertEquals(new DrunkState(9, 0, 20), drunkManager.getDrunkState(playerUuid));
        drunkManager.consume(playerUuid, -9, 0, 0);
        assertNull(drunkManager.getDrunkState(playerUuid));
        drunkManager.consume(playerUuid, -10, 20, 0);
        assertNull(drunkManager.getDrunkState(playerUuid));
    }

    @Test
    void consume_negativeValued() {
        drunkManager.consume(playerUuid, -10, 20);
        assertNull(drunkManager.getDrunkState(playerUuid));
    }

    @Test
    void consume_appliesEvent() {
        drunkManager.consume(playerUuid, 100, 0);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        for (int i = 0; i < 100000; i++) {
            drunkManager.tick((uuid, event) -> {
                atomicBoolean.set(true);
            });
        }
        assertTrue(atomicBoolean.get());
    }

    @Test
    void consume_doesNotApplyEvent() {
        drunkManager.consume(playerUuid, 1, 0);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        for (int i = 0; i < 100000; i++) {
            drunkManager.tick((uuid, event) -> {
                atomicBoolean.set(true);
            });
        }
        assertFalse(atomicBoolean.get());
    }

    @Test
    void clear() {
        drunkManager.consume(playerUuid, 100, 0);
        drunkManager.clear(playerUuid);
        assertNull(drunkManager.getDrunkState(playerUuid));
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        for (int i = 0; i < 100000; i++) {
            drunkManager.tick((uuid, event) -> {
                atomicBoolean.set(true);
            });
        }
        assertFalse(atomicBoolean.get());

    }

}