### KDD CUP99数据预处理

KDD Cup 1999数据集是与KDD-99第五届知识发现和数据挖掘国际会议同时举行的第三届国际知识发现和数据挖掘工具竞赛使用的数据集。竞争任务是建立一个网络入侵检测器，这是一种能够区分称为入侵或攻击的“不良”连接和“良好”的正常连接的预测模型。该数据集包含一组要审核的标准数据，其中包括在军事网络环境中模拟的多种入侵。

数据集下载地址：http://kdd.ics.uci.edu/databases/kddcup99/kddcup99.html

<img src="deal_data/photo/截屏2024-12-05 下午2.43.36.png" style="zoom:20%;" />

选择下载10%的子数据集作为我们此次分布式处理的数据：**kddcup.data_10_percent.gz**

- 解压文件后，文件总共有42项特征，最后一列是标记特征（Label），其他前41项特征共分为四大类。

  TCP连接基本特征（共9种，序号1～9）

  TCP连接的内容特征（共13种，序号10～22）

  基于时间的网络流量统计特征 （共9种，序号23～31）

  基于主机的网络流量统计特征 （共10种，序号32～41）

- 首先我们将所有特征中的**字符型数据转化为数值型数据**。比如：protocol_type - 协议类型，离散类型，共有3种：TCP, UDP, ICMP；我们将文件中的这些类型转换为0，1，2数值型数字

  ```py
  python rm_number.py
  ```

- 取出所有的特征除去标签列存为文件 kddcup.data_10_percent.csv

  ```py
  python feature.py
  ```

- 对特征数据 kddcup.data_10_percent.csv进行预处理

  方式一：**标准化**处理

  ```py
  python normal_data.py
  ```

  方式二：**最值归一化**处理

  ```
  python find_one.py
  ```

  

































