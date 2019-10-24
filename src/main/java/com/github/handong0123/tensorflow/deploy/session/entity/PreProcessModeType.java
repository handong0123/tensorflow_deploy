package com.github.handong0123.tensorflow.deploy.session.entity;

/**
 * Data pre process type
 *
 * @author handong
 */
public enum PreProcessModeType {
    /**
     * half angle ａ -> a
     */
    HALF_ANGLE,
    /**
     * upper a -> A
     */
    UPPER,
    /**
     * lower A -> a
     */
    LOWER;

    PreProcessModeType() {
    }
}
