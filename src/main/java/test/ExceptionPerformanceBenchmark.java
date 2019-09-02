package test;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 *  Exception as control flow performance benchmark
 *  
 * @author qxo
 *
 */
@Fork(1)
@Threads(10)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class ExceptionPerformanceBenchmark {

	@Benchmark
	//@OperationsPerInvocation(3)
	public Object baseline() {//final Blackhole hole
		java.util.concurrent.CancellationException ex = new java.util.concurrent.CancellationException();
		return ex;
	}
	
	private static final java.util.concurrent.CancellationException EX = new java.util.concurrent.CancellationException() {
		@Override
		public Throwable fillInStackTrace() {
			return this;
		}	
	};
	
	@Benchmark
	//@OperationsPerInvocation(3)
	public Object noStackTrace() {//final Blackhole hole
		java.util.concurrent.CancellationException ex = new java.util.concurrent.CancellationException() {
			@Override
			public Throwable fillInStackTrace() {
				return this;
			}	
		};
		return ex;
	}

	@Benchmark
	//@OperationsPerInvocation(3)
	public Object singleton() {//final Blackhole hole
		java.util.concurrent.CancellationException ex = EX;
		return ex;
	}
	
	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder().include(ExceptionPerformanceBenchmark.class.getSimpleName())
				//	.addProfiler("gc")
//					.mode(Mode.Throughput)
//					.warmupIterations(8).measurementIterations(8).forks(2).measurementTime(TimeValue.seconds(1))
//					.warmupTime(TimeValue.seconds(1)).threads(2).timeUnit(TimeUnit.MILLISECONDS)
					.build();
			new Runner(opt).run();
    }
/*
# JMH version: 1.21
# VM version: JDK 1.8.0_221, Java HotSpot(TM) 64-Bit Server VM, 25.221-b11

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark                                    Mode  Cnt     Score    Error   Units  Score/min
ExceptionPerformanceBenchmark.baseline      thrpt   10     3.842 ±  0.036  ops/us      1.000
ExceptionPerformanceBenchmark.noStackTrace  thrpt   10   336.346 ±  0.692  ops/us     87.542
ExceptionPerformanceBenchmark.singleton     thrpt   10  1327.454 ± 97.471  ops/us    345.502
 */
}
