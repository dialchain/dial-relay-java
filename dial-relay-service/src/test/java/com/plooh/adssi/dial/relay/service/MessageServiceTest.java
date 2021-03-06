package com.plooh.adssi.dial.relay.service;

import static org.junit.jupiter.api.Assertions.*;

import com.plooh.adssi.store.api.StringStore;
import com.plooh.adssi.store.map.MapStringStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MessageServiceTest {

    private Long expSec = 172800L;
    private StringStore store = new MapStringStore();

    private MessageService uut;

    @BeforeAll
    void setUp() {
        uut = new MessageService(store, expSec);
    }

    @Test
    public void testSetSingleEntry() {
        String message = RandomStringUtils.randomAlphabetic(100);
        String messageId = UUID.randomUUID().toString();
        Boolean result = uut.set("+61-455-5248-21", "+43-664-5554-8121", messageId, message);
        assertTrue(result);
    }

    @Test
    public void testReadMessage() {
        String sender = "+32-456-5558-15";
        String recipient = "+32-456-5554-98";
        String message = RandomStringUtils.randomAlphabetic(100);
        String messageId = UUID.randomUUID().toString();
        Boolean result = uut.set(sender, recipient, messageId, message);
        assertTrue(result);
        String messageReceived = uut.get(sender, recipient, messageId);
        assertEquals(message, messageReceived);
    }

    @Test
    public void testReadMultipleMessages() {
        String sender = "+32-456-5558-15";
        String recipient = "+32-456-5554-98";
        String messageOne = RandomStringUtils.randomAlphabetic(100);
        String messageTwo = RandomStringUtils.randomAlphabetic(100);
        String messageOneId = UUID.randomUUID().toString();
        String messageTwoId = UUID.randomUUID().toString();
        uut.set(sender, recipient, messageOneId, messageOne);
        uut.set(sender, recipient, messageTwoId, messageTwo);

        Map<String, String> expectedMessages = new HashMap<>();
        expectedMessages.put(messageOneId, messageOne);
        expectedMessages.put(messageTwoId, messageTwo);

        Map<String, String> receivedMessages = uut.get(sender, recipient, List.of(messageOneId, messageTwoId));
        assertEquals(receivedMessages, expectedMessages);
    }

    @Test
    public void testDeleteSingleEntry() {
        String sender = "+1-416-5558-841";
        String recipient = "+1-416-5553-187";
        String message = RandomStringUtils.randomAlphabetic(100);
        String messageId = UUID.randomUUID().toString();
        Boolean result = uut.set(sender, recipient, messageId, message);
        assertTrue(result);
        String messageReceived = uut.get(sender, recipient, messageId);
        assertEquals(message, messageReceived);
        Boolean deletedFlag = uut.delete(sender, recipient, messageId);
        assertTrue(deletedFlag);
        String noMessageReceived = uut.get(sender, recipient, messageId);
        assertNull(noMessageReceived);
    }

}
