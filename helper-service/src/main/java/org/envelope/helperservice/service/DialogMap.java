package org.envelope.helperservice.service;

import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.event.WaitingCountEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DialogMap implements Map<String, String> {
    private final Map<String, String> dialogSessions = new HashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public Map<String, String> getDialogSessions() {
        return new HashMap<>(dialogSessions);
    }
    public <T> String getSessionIdByValue(T value) {
        return getSessionIdsByValue(value).stream().findFirst()
                .orElse(null);
    }
    public Map.Entry<String, String> getDialogWithHelper(String helperId) {
        return this.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().equals(helperId))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Не найден диалог с помощником: " + helperId));
    }
    public int getWaitingUsersCount() {
        return getSessionIdsByValue(null).size();
    }
    private void publishWaitingCountEvent() {
        var waitingCountEvent = new WaitingCountEvent(this, getWaitingUsersCount());
        eventPublisher.publishEvent(waitingCountEvent);
    }
    private <T> Set<String> getSessionIdsByValue(T value) {
        return dialogSessions.entrySet().stream()
                .filter(e -> e.getValue() == value)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public String put(String key, String value) {
        String added = dialogSessions.put(key, value);
        publishWaitingCountEvent();
        return added;
    }
    @Override
    public String remove(Object key) {
        String removed = dialogSessions.remove(key);
        publishWaitingCountEvent();
        return removed;
    }

    @Override
    public void putAll(Map m) {
        dialogSessions.putAll(m);
    }
    @Override
    public int size() {
        return dialogSessions.size();
    }
    @Override
    public boolean isEmpty() {
        return dialogSessions.isEmpty();
    }
    @Override
    public boolean containsKey(Object key) {
        return dialogSessions.containsKey(key);
    }
    @Override
    public boolean containsValue(Object value) {
        return dialogSessions.containsValue(value);
    }
    @Override
    public String get(Object key) {
        return dialogSessions.get(key);
    }

    @Override
    public void clear() {
        dialogSessions.clear();
    }
    @Override
    public Set<String> keySet() {
        return dialogSessions.keySet();
    }
    @Override
    public Collection<String> values() {
        return dialogSessions.values();
    }
    @Override
    public Set<Entry<String, String>> entrySet() {
        return dialogSessions.entrySet();
    }
}
