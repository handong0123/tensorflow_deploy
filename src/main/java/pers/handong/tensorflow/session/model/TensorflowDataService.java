package pers.handong.tensorflow.session.model;

import pers.handong.tensorflow.session.entity.PreProcessModeType;

import java.util.List;
import java.util.Map;

/**
 * Tensorflow data service
 * <p>
 * Provide data preprocessing, load char_encoder, get id, padding, etc.
 *
 * @author handong
 */
public interface TensorflowDataService {
    /**
     * Data pre process
     *
     * @param text text
     * @param type process type
     * @return processed text
     */
    String preProcessing(String text, PreProcessModeType type);


    /**
     * Load str-id map
     *
     * @param file  str—id file path
     * @param split Separator
     * @return str-id map
     */
    Map<String, Integer> loadStr2Int(String file, String split);

    /**
     * Load id-str map
     *
     * @param file  id-str file path
     * @param split Separator
     * @return id-str
     */
    List<String> loadInt2Str(String file, String split);

    /**
     * padding
     *
     * @param ids          List
     * @param paddingToLen Need length
     * @param paddingVal   Padding value
     * @return list
     */
    List<Integer> padding(List<Integer> ids, int paddingToLen, int paddingVal);

    /**
     * Get id by word
     *
     * @param word2IdMap Word-id map
     * @param words      Words
     * @param unknownTag  Unknown tag name
     * @return Id list
     */
    List<Integer> getWord2Ids(Map<String, Integer> word2IdMap, List<String> words, String unknownTag);

    /**
     * Get id by char
     *
     * @param char2IdMap char-id map
     * @param text       string
     * @param unknownTag Unknown tag name
     * @return Id list
     */
    List<Integer> getChar2Ids(Map<String, Integer> char2IdMap, String text, String unknownTag);
}
