package com.github.handong0123.tensorflow.deploy.provider;

import com.github.handong0123.tensorflow.deploy.session.entity.ModelInput;
import com.github.handong0123.tensorflow.deploy.session.model.TensorflowModelService;
import com.github.handong0123.tensorflow.deploy.session.model.TensorflowModelServiceImpl;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.handong0123.tensorflow.deploy.session.entity.ModelOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Tensorflow Provider
 * <p>
 * This class provides prediction, model overloading methods
 *
 * @author handong
 */
public class TensorflowProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TensorflowProvider.class);

    private static final String DEFAULT_GPU_ID = "-1";
    private static final int DEFAULT_TIMEOUT = 300000;
    private static final int DEFAULT_QUEUE_SIZE = 5000;
    private static final int DEFAULT_THREAD_NUM = 3;
    private int timeout;
    private List<TensorflowProviderThread> tensorflowProviderThreads;
    private final LinkedBlockingQueue<ModelInput> queue;
    private final ConcurrentHashMap<Object, ModelOutput> result;
    private ExecutorService executorService;


    public TensorflowProvider(String modelFile, String modelPath) {
        this(DEFAULT_THREAD_NUM, modelFile, modelPath, null);
    }


    public TensorflowProvider(int threadNum, String modelFile, String modelPath) {
        this(threadNum, modelFile, modelPath, null);
    }

    public TensorflowProvider(int threadNum, String modelFile, String modelPath, String gpuIdStr) {
        this(threadNum, modelFile, modelPath, gpuIdStr, DEFAULT_TIMEOUT);
    }

    /**
     * TensorflowProvider Construction method
     *
     * @param threadNum Load {threadNum} sessions
     * @param modelFile Model name
     * @param modelPath Model directory
     * @param gpuIdStr  Gpu number, split by ','
     * @param timeout   timeout, If timed out, the model output will no longer be obtained
     */
    public TensorflowProvider(int threadNum, String modelFile, String modelPath, String gpuIdStr, int timeout) {
        this.timeout = timeout;
        this.queue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_SIZE);
        this.result = new ConcurrentHashMap<>();
        this.tensorflowProviderThreads = new ArrayList<>();
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("tensorflow-provider-pool-%d").build();
        this.executorService = new ThreadPoolExecutor(10, 20, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1), namedThreadFactory);
        String[] gpuIds = StringUtils.isBlank(gpuIdStr) ? new String[]{} : gpuIdStr.split(",");
        boolean cpu = 0 == gpuIds.length;
        threadNum = threadNum <= 0 ? DEFAULT_THREAD_NUM : threadNum;
        threadNum = cpu ? threadNum : gpuIds.length;
        for (int i = 0; i < threadNum; i++) {
            String gpuId = cpu ? DEFAULT_GPU_ID : gpuIds[i];
            TensorflowModelService tensorflowModelService = new TensorflowModelServiceImpl(modelFile, modelPath, gpuId);
            TensorflowProviderThread tensorflowProviderThread = new TensorflowProviderThread(tensorflowModelService, timeout);
            this.executorService.execute(tensorflowProviderThread);
            this.tensorflowProviderThreads.add(tensorflowProviderThread);
        }
    }

    /**
     * Put model input to queue
     *
     * @param input model input
     * @return success
     */
    private boolean putMessage(ModelInput input) {
        input.setReqUuid(new Object());
        return this.queue.offer(input);
    }

    /**
     * Get model output from result map
     *
     * @param messageId message id
     * @return model output
     * @throws InterruptedException Response interrupt
     */
    private ModelOutput getMessage(Object messageId) throws InterruptedException {
        ModelOutput outPut;
        long startTime = System.currentTimeMillis();
        while (true) {
            outPut = this.result.get(messageId);
            if (outPut != null) {
                this.result.remove(messageId);
                break;
            }
            if (System.currentTimeMillis() - startTime > this.timeout) {
                LOG.info("Get Message Timeout");
                break;
            }
            Thread.sleep(1);
        }
        return outPut;
    }

    /**
     * predict
     *
     * @param input model input
     * @return model output
     * @throws InterruptedException Response interrupt
     */
    public ModelOutput predict(ModelInput input) throws InterruptedException {
        if (!this.putMessage(input)) {
            LOG.error("Put message failed, maybe queue is full");
            return null;
        }
        return this.getMessage(input.getReqUuid());
    }

    /**
     * reload model - reload graph and session
     */
    public void modelReload() {
        this.tensorflowProviderThreads.forEach(t -> t.getModelService().modelReload());
    }

    class TensorflowProviderThread extends Thread {
        private TensorflowModelService modelService;
        private int timeout;

        TensorflowProviderThread(TensorflowModelService modelService, int timeout) {
            this.modelService = modelService;
            this.timeout = timeout;
        }

        TensorflowModelService getModelService() {
            return modelService;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        ModelInput input = TensorflowProvider.this.queue.take();
                        long startTime = System.currentTimeMillis();
                        ModelOutput outPut = this.modelService.predict(input);
                        if (null == outPut || System.currentTimeMillis() - startTime > this.timeout) {
                            continue;
                        }
                        TensorflowProvider.this.result.put(input.getReqUuid(), outPut);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}


