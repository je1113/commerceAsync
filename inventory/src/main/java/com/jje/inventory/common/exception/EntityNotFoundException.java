package com.jje.inventory.common.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + "을(를) 찾을 수 없습니다. id=" + id);
    }

    public EntityNotFoundException(String entityName, String field, Object value) {
        super(entityName + "을(를) 찾을 수 없습니다. " + field + "=" + value);
    }
}
