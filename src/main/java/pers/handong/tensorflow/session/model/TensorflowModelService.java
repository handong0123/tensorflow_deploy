package pers.handong.tensorflow.session.model;

import pers.handong.tensorflow.session.entity.ModelInput;
import pers.handong.tensorflow.session.entity.ModelOutput;

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
