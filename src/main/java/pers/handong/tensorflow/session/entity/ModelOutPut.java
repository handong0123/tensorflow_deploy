package pers.handong.tensorflow.session.entity;

import org.tensorflow.Tensor;

import java.util.HashMap;
import java.util.Map;

/**
 * Model output
 *
 * @author handong
 */
public class ModelOutPut {

    private Map<String, Object> output = new HashMap<>();

    public Map<String, Object> getOutput() {
        return output;
    }

    public void addOutput(String name, Object out) {
        this.output.put(name, out);
    }
}
