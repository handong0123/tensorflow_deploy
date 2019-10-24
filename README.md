# tensorflow_deploy
#### 简介

简化与优化tensorflow模型的Java部署，并提供特征抽取相关工具




#### 特性

1. 多线程加载图，提高预测性能50%

2. 提供模型重载，防止模型卡死

3. 封装模型输入输出，使用更加简便，可读性好

4. 提供数据预处理，加载char-encoder，获取文本id等特征抽取常用功能

   

##### Quick Start

构造：

```java
// 使用CPU 更多构造参数请看源码
TensorflowProvider tfp = new TensorflowProvider("frozen.pb","/home/demo");
    
// 使用GPU
TensorflowProvider tfp = new TensorflowProvider(3,"frozen.pb","/home/demo","0,1,2");
```

使用：

```java
// 构造模型输入
ModelInut input = new ModelInput();
// 输出的tensor名和数据类型
input.addExceptedOutput("project/Reshape:0",ModelDataType.FLOAT);
// 输入的tensor名和数据数组（未装箱）
input.addPlaceHolderInput("CharInputs:0",new float[][]{});
// 预测
ModelOutput output = tfp.predict(input);
float[][][] scores = (float[][][]) output.getOutput("project/Reshape:0");
```

数据处理工具：

```
// 提供大小写转换、全角转半角、加载char-id映射、获取char特征、填充列表等方法
TensorflowDataService dataService = new TensorflowDataServiceImpl();

```



#### 如果你有好的想法或建议，可以提issue，也可以联系handong970123@gmail.com，项目会一直维护更新，感谢你的star。

