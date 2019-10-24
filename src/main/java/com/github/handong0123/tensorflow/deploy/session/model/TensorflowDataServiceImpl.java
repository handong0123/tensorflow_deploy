package com.github.handong0123.tensorflow.deploy.session.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.handong0123.tensorflow.deploy.session.entity.PreProcessModeType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Tensorflow data service achieve
 *
 * @author handong
 */
public class TensorflowDataServiceImpl implements TensorflowDataService {
    private static final Logger LOG = LoggerFactory.getLogger(TensorflowDataService.class);

    @Override
    public String preProcessing(String text, PreProcessModeType type) {
        if (PreProcessModeType.HALF_ANGLE.equals(type)) {
            char[] c = text.toCharArray();
            for (int i = 0; i < c.length; i++) {
                if (12288 == c[i]) {
                    c[i] = ' ';
                } else if (c[i] > '\uff00' && c[i] < '\uff5f') {
                    c[i] -= '\ufee0';
                }
            }
            text = new String(c);
        } else if (PreProcessModeType.UPPER.equals(type)) {
            text = text.toUpperCase();
        } else if (PreProcessModeType.LOWER.equals(type)) {
            text = text.toLowerCase();
        }
        return text;
    }

    @Override
    public Map<String, Integer> loadStr2Int(String file, String split) {
        Map<String, Integer> resultMap = new HashMap<>(4096);
        try {
            Files.readAllLines(Paths.get(file)).forEach(line -> {
                line = line.replace("\n", "");
                String[] items = line.split(split);
                resultMap.put(items[0], Integer.parseInt(items[1]));
            });
        } catch (Exception e) {
            LOG.error("load file {} failed", file);
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public List<String> loadInt2Str(String file, String split) {
        List<String> resultList = new ArrayList<>();
        try {
            Files.readAllLines(Paths.get(file)).forEach(line -> {
                line = line.replace("\n", "");
                String[] items = line.split(split);
                resultList.set(Integer.parseInt(items[1]), items[0]);
            });
        } catch (Exception e) {
            LOG.error("load file {} failed", file);
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public List<Integer> padding(List<Integer> ids, int paddingToLen, int paddingVal) {
        for (int i = ids.size(); i < paddingToLen; i++) {
            ids.add(paddingVal);
        }
        return ids.subList(0, paddingToLen);
    }

    @Override
    public List<Integer> getWord2Ids(Map<String, Integer> word2IdMap, List<String> words, String unknownTag) {
        List<Integer> retList = new ArrayList<>();
        words.forEach(w -> retList.add(word2IdMap.getOrDefault(w, word2IdMap.get(unknownTag))));
        return retList;
    }

    @Override
    public List<Integer> getChar2Ids(Map<String, Integer> char2IdMap, String text, String unknownTag) {
        List<Integer> retList = new ArrayList<>();
        char[] chars = text.toCharArray();
        for (char c : chars) {
            retList.add(char2IdMap.getOrDefault(String.valueOf(c), char2IdMap.get(unknownTag)));
        }
        return retList;
    }
}
