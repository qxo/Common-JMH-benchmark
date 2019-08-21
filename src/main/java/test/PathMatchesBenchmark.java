package test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


@Fork(1)
@Threads(5)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class PathMatchesBenchmark {

	private static final boolean VALID = true;
	
	private static final String[][] samples = new String[][] {
		  {"/abc/","/abc", "true" }
		  , {"/abc/","/abc/abc","false" }
		  , {"/abc","/abc/abc","false" }
		  , {"/abc","/abc","true" }
	};
	
	@Benchmark
	@OperationsPerInvocation(4)
	public void removeTrailingWay(Blackhole blackhole) {
		for(String[] arr :samples) {
			final boolean match = pathMatchesExactOld(arr[0],arr[1]);
			blackhole.consume(match);
			if(VALID && !arr[2].equals(Boolean.toString(match))) {
				throw new IllegalStateException("shoud be:"+arr[2] + " but was:"+match+" :"+Arrays.asList(arr));
			}
		}
	}
	

	@Benchmark
	@OperationsPerInvocation(4)
	public void regionMatchesWay(Blackhole blackhole) {
		for(String[] arr :samples) {
			boolean match = pathMatchesExactNew(arr[0],arr[1]);
			blackhole.consume(match);
			if(VALID && !arr[2].equals(Boolean.toString(match))) {
				throw new IllegalStateException("shoud be:"+arr[2] + " but was:"+match+ " :"+Arrays.asList(arr));
			}
		}
	}
	
	
	  private boolean pathMatchesExactOld(String path1, String path2) {
		    // Ignore trailing slash when matching paths
		   return removeTrailing(path1).equals(removeTrailing(path2));
	  }
	  
	  private boolean pathMatchesExactNew(String path1, String path2) {
	    // Ignore trailing slash when matching paths
	   // return removeTrailing(path1).equals(removeTrailing(path2));
		final int idx1 = path1.length()-1;
		final int idx2 = path2.length()-1;
		return path2.charAt(idx2) == '/' ?
				  ( path1.charAt(idx1) == '/' ? path1.equals(path2) : path2.regionMatches(0, path1, 0, path1.length()) )
					: ( path1.charAt(idx1) != '/' ? path1.equals(path2)  :  path1.regionMatches(0, path2, 0, path2.length()) );
	  }
	
	  private String removeTrailing(String path) {
	    int i = path.length();
	    if (path.charAt(i - 1) == '/') {
	      path = path.substring(0, i - 1);
	    }
	    return path;
	  }
	  
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(PathMatchesBenchmark.class.getSimpleName())
			//	.addProfiler("gc")
//				.mode(Mode.Throughput)
//				.warmupIterations(8).measurementIterations(8).forks(2).measurementTime(TimeValue.seconds(1))
//				.warmupTime(TimeValue.seconds(1)).threads(2).timeUnit(TimeUnit.MILLISECONDS)
				.build();
		new Runner(opt).run();
	}
/*
 # JMH version: 1.21
# VM version: JDK 1.8.0_221, Java HotSpot(TM) 64-Bit Server VM, 25.221-b11

Benchmark                                Mode  Cnt    Score    Error   Units  Score/min
PathMatchesBenchmark.regionMatchesWay   thrpt   10  425.602 ± 54.997  ops/us      1.702
PathMatchesBenchmark.removeTrailingWay  thrpt   10  250.100 ±  3.807  ops/us      1.000

===============================================================
# JMH version: 1.21
# VM version: JDK 11, Java HotSpot(TM) 64-Bit Server VM, 11+28

Benchmark                                Mode  Cnt    Score    Error   Units  Score/min
PathMatchesBenchmark.regionMatchesWay   thrpt   10  360.569 ±  6.034  ops/us      1.871
PathMatchesBenchmark.removeTrailingWay  thrpt   10  192.728 ± 23.952  ops/us      1.000
*/
}
