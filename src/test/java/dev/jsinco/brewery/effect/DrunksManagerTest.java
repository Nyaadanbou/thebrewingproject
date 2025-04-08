package dev.jsinco.brewery.effect;

import dev.jsinco.brewery.effect.event.CustomEventRegistry;
import dev.jsinco.brewery.effect.event.NamedDrunkEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DrunksManagerTest {

    private DrunksManager drunksManager;
    private UUID playerUuid = UUID.randomUUID();

    @BeforeEach
    void setup() {
        this.drunksManager = new DrunksManager(20, new CustomEventRegistry(), Arrays.stream(NamedDrunkEvent.values())
                .map(NamedDrunkEvent::key)
                .collect(Collectors.toSet())
        );
    }

    @Test
    void consume() {
        drunksManager.consume(playerUuid, 10, 0, 0);
        assertEquals(new DrunkState(10, 0, 0, 0), drunksManager.getDrunkState(playerUuid));
        drunksManager.consume(playerUuid, 0, 0, 20);
        assertEquals(new DrunkState(9, 0, 0, 20), drunksManager.getDrunkState(playerUuid));
        drunksManager.consume(playerUuid, -9, 0, 0);
        assertNull(drunksManager.getDrunkState(playerUuid));
        drunksManager.consume(playerUuid, -10, 20, 0);
        assertNull(drunksManager.getDrunkState(playerUuid));
    }

    @Test
    void consume_negativeValued() {
        drunksManager.consume(playerUuid, -10, 20);
        assertNull(drunksManager.getDrunkState(playerUuid));
    }

    @Test
    void consume_appliesEvent() {
        drunksManager.consume(playerUuid, 100, 0);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        for (int i = 0; i < 100000; i++) {
            drunksManager.tick((uuid, event) -> {
                atomicBoolean.set(true);
            });
        }
        assertTrue(atomicBoolean.get());
    }

    @Test
    void consume_doesNotApplyEvent() {
        drunksManager.consume(playerUuid, 1, 0);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        for (int i = 0; i < 100000; i++) {
            drunksManager.tick((uuid, event) -> {
                atomicBoolean.set(true);
            });
        }
        assertFalse(atomicBoolean.get());
    }

    @Test
    void clear() {
        drunksManager.consume(playerUuid, 100, 0);
        drunksManager.clear(playerUuid);
        assertNull(drunksManager.getDrunkState(playerUuid));
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        for (int i = 0; i < 100000; i++) {
            drunksManager.tick((uuid, event) -> {
                atomicBoolean.set(true);
            });
        }
        assertFalse(atomicBoolean.get());

    }

}