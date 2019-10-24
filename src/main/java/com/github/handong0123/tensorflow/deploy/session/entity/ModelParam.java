package com.github.handong0123.tensorflow.deploy.session.entity;


/**
 * Model param
 *
 * @author handong
 */
public class ModelParam {
    /**
     * Tensor name
     */
    private String placeHolderName;
    /**
     * array with no boxed
     */
    private Object data;

    public ModelParam() {
    }

    public ModelParam(String placeHolderName, Object data) {
        this.placeHolderName = placeHolderName;
        this.data = data;
    }

    public String getPlaceHolderName() {
        return placeHolderName;
    }

    public void setPlaceHolderName(String placeHolderName) {
        this.placeHolderName = placeHolderName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
