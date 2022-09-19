import org.apache.spark.graphx.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Vector;
import java.lang.Long;
import java.lang.NumberFormatException;
import scala.Tuple2;
import scala.reflect.ClassTag;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.graphx.lib.ConnectedComponents;




////////////////////////////////////////////////////////////////////////////////
class CanonicalEdgeMapper implements FlatMapFunction<String, Edge<String> > {

  @Override 
  public Iterator< Edge<String> > call(String s) {

    Vector< Edge<String> > output = new Vector< Edge<String> >();

    if (!s.startsWith("#")) {
      StringTokenizer tokenizer = new StringTokenizer(s);

      int count = 0;
      Long[] vertices = new Long[2];
      while (tokenizer.hasMoreTokens()) {
        if (count > 2) { break; }
        else {
          try {
            vertices[count] = new Long(tokenizer.nextToken());
          } catch (NumberFormatException e) {
            break;
          }
        }
        ++count;
      }

      if (!(vertices[0].compareTo(vertices[1]) <= 0)) {
        Long tmp = vertices[1];
        vertices[1] = vertices[0];
        vertices[0] = tmp;
      }
      output.add(
          new Edge<String>(vertices[0].longValue(), 
                           vertices[1].longValue(), "") );
    }
    return output.iterator();
  }
}

class RoadNetworkBuilder {
  public static Graph<String, String> buildFromLocalTextFile(
      String input_file_name, JavaSparkContext sc) {
    // STEP-1: create an RDD of String Collection 
    JavaRDD<String> string_edges = sc.textFile(input_file_name);

    // STEP-2: drop all the things which start with # //
    // STEP-3: canonicalize the edges (a , b) = () // 
    JavaRDD< Edge<String> > edges = string_edges.flatMap(
        new FlatMapFunction<String, Edge<String> >() {

          @Override 
          public Iterator< Edge<String> > call(String s) {

            Vector< Edge<String> > output = new Vector< Edge<String> >();

            if (!s.startsWith("#")) {
              StringTokenizer tokenizer = new StringTokenizer(s);

              int count = 0;
              Long[] vertices = new Long[2];
              while (tokenizer.hasMoreTokens()) {
                if (count > 2) { break; }
                else {
                  try {
                    vertices[count] = new Long(tokenizer.nextToken());
                  } catch (NumberFormatException e) {
                    break;
                  }
                }
                ++count;
              }

              if (count == 2) {
                if (!(vertices[0].compareTo(vertices[1]) <= 0)) {
                  Long tmp = vertices[1];
                  vertices[1] = vertices[0];
                  vertices[0] = tmp;
                }
                output.add(
                    new Edge<String>(vertices[0].longValue(), 
                                     vertices[1].longValue(), "") );
              }
            }
            return output.iterator();
          }

        }
    );

    System.out.println("Edge count="+edges.count());
    
    ClassTag<String> string_tag =
        scala.reflect.ClassTag$.MODULE$.apply(String.class);
    Graph<String, String> graph = Graph.fromEdges( edges.rdd(), "",
        StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(),
          string_tag, string_tag);
    return graph;
  }
}

////////////////////////////////////////////////////////////////////////////////


public class ComputeConnectedComponents {

  public static void main(String[] args) {
    SparkConf sparkConf = new SparkConf().setAppName("ComputeConnectedComponents");
    JavaSparkContext sc = new JavaSparkContext(sparkConf);

    if (args.length != 2) {
      System.out.println(
          "ERROR: needs {input} graph file and {output directory}");
      return;
    }

    System.out.println("Processing " + args[0]);
    Graph<String, String> graph =
        RoadNetworkBuilder.buildFromLocalTextFile( args[0], sc);

    // compute connected components //
    ClassTag<String> string_tag =
        scala.reflect.ClassTag$.MODULE$.apply(String.class);
    Graph<Object, String> conn_comps = ConnectedComponents.run(graph,
          string_tag, string_tag); 

    String output = args[1];
    conn_comps.vertices().saveAsTextFile(output);
  }

}
