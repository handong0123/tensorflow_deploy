package pers.handong.tensorflow.session.entity;


/**
 * Model param
 *
 * @author handong
 */
public class ModelParam {
    private String placeHolderName;
    private Object data;

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
