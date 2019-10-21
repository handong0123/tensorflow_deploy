package pers.handong.tensorflow.session.entity;

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
