package pers.handong.tensorflow.session.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Model output
 *
 * @author handong
 */
public class ModelOutput {

    private Map<String, Object> output = new HashMap<>();

    public Object getOutput(String tensor) {
        return output.get(tensor);
    }

    public void addOutput(String name, Object out) {
        this.output.put(name, out);
    }
}
