cmake_minimum_required (VERSION 3.13.0)

find_package(Java REQUIRED)
include(UseJava)

if (NOT DEFINED HADOOP_SPARK_INSTALL_DIR)
  message(FATAL_ERROR "Please specify the hadoop-spark install dir with -DHADOOP_SPARK_INSTALL_DIR=<directory>")
endif()

message(STATUS "ls ${HADOOP_SPARK_INSTALL_DIR}/jars/spark-core*.jar")
file(GLOB_RECURSE JAR_MATCHES "${HADOOP_SPARK_INSTALL_DIR}/jars/*.jar")

foreach(JAR_FILE ${JAR_MATCHES})
  if (${JAR_FILE} MATCHES "${HADOOP_SPARK_INSTALL_DIR}/jars/spark-core")
    set(SPARK_CORE_JAR ${JAR_FILE})
  endif()

  if (${JAR_FILE} MATCHES "${HADOOP_SPARK_INSTALL_DIR}/jars/spark-graphx")
    set(SPARK_GRAPHX_JAR ${JAR_FILE})
  endif()

  if (${JAR_FILE} MATCHES "${HADOOP_SPARK_INSTALL_DIR}/jars/scala-library")
    set(SCALA_LIBRARY_JAR ${JAR_FILE})
  endif()

endforeach()


if (NOT DEFINED SPARK_CORE_JAR)
  message(FATAL_ERROR "Cannot ind spark-core-*.jar library")
endif()

if (NOT DEFINED SPARK_GRAPHX_JAR)
  message(FATAL_ERROR "Cannot ind spark-graphx-*.jar library")
endif()

if (NOT DEFINED SCALA_LIBRARY_JAR)
  message(FATAL_ERROR "Cannot ind scala-library-*.jar library")
endif()

message(STATUS "Using ${SPARK_CORE_JAR} ")
message(STATUS "Using ${SPARK_GRAPHX_JAR} ")
message(STATUS "Using ${SCALA_LIBRARY_JAR} ")

add_jar(ComputeConnectedComponents ComputeConnectedComponents.java
    INCLUDE_JARS ${SPARK_CORE_JAR}
    INCLUDE_JARS ${SPARK_GRAPHX_JAR}
    INCLUDE_JARS ${SCALA_LIBRARY_JAR} )

