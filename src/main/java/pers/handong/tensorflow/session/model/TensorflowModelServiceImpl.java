package pers.handong.tensorflow.session.model;

import com.google.common.primitives.Longs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.framework.ConfigProto;
import org.tensorflow.framework.GPUOptions;
import pers.handong.tensorflow.session.entity.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Tensorflow model service achieve
 * <p>
 * Provide some operations on the model
 *
 * @author handong
 */
public class TensorflowModelServiceImpl implements TensorflowModelService {
    private static final Logger LOG = LoggerFactory.getLogger(TensorflowModelServiceImpl.class);
    private static final String DEFAULT_GPU_ID = "-1";
    private static final float DEFAULT_PER_GPU_MEMORY_FRACTION = 0.95f;

    private String modelFile;
    private String modelPath;
    private String gpuId;
    private float perGpuMemoryFraction;
    private Session session;
    private Graph graph;

    public TensorflowModelServiceImpl(String modelFile, String modelPath) {
        this(modelFile, modelPath, DEFAULT_GPU_ID, DEFAULT_PER_GPU_MEMORY_FRACTION);
    }

    public TensorflowModelServiceImpl(String modelFile, String modelPath, String gpuId) {
        this(modelFile, modelPath, gpuId, DEFAULT_PER_GPU_MEMORY_FRACTION);
    }

    public TensorflowModelServiceImpl(String modelFile, String modelPath, String gpuId, float perGpuMemoryFraction) {
        this.modelFile = modelFile;
        this.modelPath = modelPath;
        this.perGpuMemoryFraction = perGpuMemoryFraction;
        this.gpuId = gpuId;
        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() throws IOException {
        byte[] graphDef = Files.readAllBytes(Paths.get(this.modelPath, this.modelFile));
        graph = new Graph();
        if (DEFAULT_GPU_ID.equals(this.gpuId)) {
            graph.importGraphDef(graphDef);
            this.session = new Session(graph);
            LOG.info("CPU:model init success,{}/{}", this.modelPath, this.modelFile);
        } else {
            GPUOptions gpuOptions = GPUOptions.newBuilder()
                    .setVisibleDeviceList(this.gpuId)
                    .setPerProcessGpuMemoryFraction(this.perGpuMemoryFraction)
                    .setAllowGrowth(true)
                    .build();
            ConfigProto configProto = ConfigProto.newBuilder()
                    .setGpuOptions(gpuOptions)
                    .setAllowSoftPlacement(true)
                    .build();
            this.session = new Session(graph, configProto.toByteArray());
            LOG.info("GPU:model init success,{}/{}", this.modelPath, this.modelFile);
        }
    }

    @Override
    public ModelOutput predict(ModelInput modelInput) {
        if (null == modelInput) {
            return null;
        }
        ModelOutput outPut = new ModelOutput();
        long startTime = System.currentTimeMillis();
        List<Tensor> inputTensorList = new ArrayList<>();
        List<Tensor<?>> outTensorList = new ArrayList<>();
        try {
            Session.Runner runner = this.session.runner();
            for (ModelParam placeHolder : modelInput.getPlaceHolderInput()) {
                Object data = placeHolder.getData();
                Tensor tensorInput = Tensor.create(data);
                runner = runner.feed(placeHolder.getPlaceHolderName(), tensorInput);
                inputTensorList.add(tensorInput);
            }
            List<String> tensorNameList = new ArrayList<>();
            for (String name : modelInput.getExpectedOutput().keySet()) {
                runner = runner.fetch(name);
                tensorNameList.add(name);
            }
            outTensorList = runner.run();
            LOG.info("Model Run Cost Time: {}", System.currentTimeMillis() - startTime);
            if (tensorNameList.size() != outTensorList.size()) {
                throw new Exception("Model Run Error: OutTensor Size Error");
            }
            for (int i = 0; i < outTensorList.size(); i++) {
                Tensor tensor = outTensorList.get(i);
                String outTensorName = tensorNameList.get(i);
                int[] shape = Longs.asList(tensor.shape()).stream().mapToInt(Long::intValue).toArray();
                ModelDataType type = modelInput.getExpectedOutput().get(outTensorName);
                Object array = Array.newInstance(type.getType(), shape);
                tensor.copyTo(array);
                outPut.addOutput(outTensorName, array);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputTensorList.forEach(Tensor::close);
            outTensorList.forEach(Tensor::close);
        }
        return outPut;
    }

    @Override
    public void modelReload() {
        try {
            Graph graph = new Graph();
            byte[] graphDef = Files.readAllBytes(Paths.get(this.modelPath, this.modelFile));
            graph.importGraphDef(graphDef);
            Session session = new Session(graph);
            synchronized (this) {
                LOG.info("Start Model Reload...");
                this.session.close();
                this.session = session;
                this.graph.close();
                this.graph = graph;
                LOG.info("Finish Model Reload");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
