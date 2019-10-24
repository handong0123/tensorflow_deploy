package com.github.handong0123.tensorflow.deploy.session.model;

import com.github.handong0123.tensorflow.deploy.session.entity.ModelInput;
import com.github.handong0123.tensorflow.deploy.session.entity.ModelOutput;

/**
 * Tensorflow model service
 *
 * @author handong
 */
public interface TensorflowModelService {
    /**
     * model predict
     *
     * @param modelInput model input
     * @return model output
     */
    ModelOutput predict(ModelInput modelInput);

    /**
     * model reload
     */
    void modelReload();
}
