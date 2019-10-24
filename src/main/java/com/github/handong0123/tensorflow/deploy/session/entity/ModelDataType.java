package com.github.handong0123.tensorflow.deploy.session.entity;

/**
 * Model data type
 *
 * @author handong
 */
public enum ModelDataType {
    /**
     * int
     */
    INT32(Integer.TYPE, 0),
    /**
     * long
     */
    INT64(Long.TYPE, 1),
    /**
     * float
     */
    FLOAT(Float.TYPE, 2),
    /**
     * double
     */
    DOUBLE(Double.TYPE, 3),
    /**
     * boolean
     */
    BOOLEAN(Boolean.TYPE, 4),
    /**
     * char
     */
    CHARACTER(Character.TYPE, 5);

    private Class<?> type;
    private int value;

    ModelDataType(Class<?> type, int value) {
        this.type = type;
        this.value = value;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
