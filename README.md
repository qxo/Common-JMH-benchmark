Common JMH benchmark  
================================================
ref: 
* http://openjdk.java.net/projects/code-tools/jmh
* http://java-performance.info/jmh/
* http://java-performance.info/introduction-jmh-profilers/
* http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/

## build jar
```
mvn clean package
```

## get help from cmd `java -jar target/benchmarks.jar  -h`
* List matching benchmarks -- `java -jar target/benchmarks.jar -l `
* List profilers -- `java -jar target/benchmarks.jar  -lprof `

## test case
*  ThreadLocalGetXTimeBenchmark:
```
java -jar target/benchmarks.jar  ThreadLocalGetXTimeBenchmark
``` 
```
Benchmark                                             Mode  Cnt       Score      Error   Units  Score/min
ThreadLocalGetXTimeBenchmark.getThreadLocalFourTime  thrpt   50   70274.092 ±  140.994  ops/ms      1.000
ThreadLocalGetXTimeBenchmark.getThreadLocalOnce1     thrpt   50  232928.024 ±  308.397  ops/ms      3.315
ThreadLocalGetXTimeBenchmark.getThreadLocalTwice     thrpt   50  159792.399 ± 4924.628  ops/ms      2.274
```
ThreadLocal get once vs twice fast over 40% ( 3.315/  2.274 )