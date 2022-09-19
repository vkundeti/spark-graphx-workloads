# Graph Work Load To Compute Connected Components using GraphX
This is a simple graph workload which can load a Road Network graph from [http://snap.stanford.edu/data/#road](http://snap.stanford.edu/data/#road) into `JavaGraphRDD` and runs the
`ConnectedComponents` algorithm on the graph. You can build the `ConnectedComponents.jar` file and run using `spark-submit`. Following shows how to build and run on an example graph as well.

## Building GraphX Workload
You need to specify the installation directory of *Hadoop Spark* as follows

```
git clone URL
mkdir build/
cd build/
cmake ../src/ -DHADOOP_SPARK_INSTALL_DIR=/home/vamsikku/vamsikku/work/ESC/spark-3.3.0-bin-hadoop3/
make -j10
```
