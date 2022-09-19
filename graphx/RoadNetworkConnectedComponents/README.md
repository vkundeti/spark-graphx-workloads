# Graph Work Load To Compute Connected Components using GraphX
This is a simple graph workload which can load a Road Network graph from [http://snap.stanford.edu/data/#road](http://snap.stanford.edu/data/#road) into `JavaGraphRDD` and runs the
`ConnectedComponents` algorithm on the graph. You can build the `ConnectedComponents.jar` file and run using `spark-submit`. Following shows how to build and run on an example graph as well.

## Building GraphX Workload
Set the `JAVA_HOME` directory and specify the installation directory of *Hadoop Spark* as follows:

```
git clone git@github.com:vkundeti/spark-graphx-workloads.git
cd spark-graphx-workloads/graphx/RoadNetworkConnectedComponents
mkdir build/
cd build/
cmake ../src/ -DHADOOP_SPARK_INSTALL_DIR=/home/vamsikku/vamsikku/work/ESC/spark-3.3.0-bin-hadoop3/
make -j10
```

If the build is fine you should see `ComputeConnectedComponents.jar`

## Running Workload on HADOOP-YARN cluster.
Copy the `tests/test-input.txt` into `hdfs` (E.g.  `hdfs -put tests/test-input.txt /user/vamsikku/test-input.txt`). Run the following command to submit the workload to the cluster. Make sure that you have the environment variable `HADOOP_CONF_DIR` set.

```
spark-submit --master yarn --class ComputeConnectedComponents ComputeConnectedComponents.jar test-input.txt output_test-input
```

### Checking the output
The input test has *three* connected components in the graph as below:

```
$[vamsikku@vamsikku-desk]/home/vamsikku/vamsikku/work/ESC/hadoop-3.3.4/bin/hdfs dfs -cat test-input.txt
# Test input with 3 connected components
# Component 1 #
1 2
2 3
3 4
1 3
# Component 2 #
5 6
5 7
# Component 3 #
12 11
```

We expect that workflow will generate nodes and corresponding connected component id (lowest) as we can see below:

``` 
[vamsikku@vamsikku-desk]$/home/vamsikku/vamsikku/work/ESC/hadoop-3.3.4/bin/hdfs dfs -cat output_test-input/*
(4,1)
(6,5)
(12,11)
(2,1)
(11,11)
(1,1)
(3,1)
(7,5)
(5,5)
```

