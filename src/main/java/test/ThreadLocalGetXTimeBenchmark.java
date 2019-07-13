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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * 多次通过ThreadLocal获取对象
 *  
 * @author qxo
 */
@Fork(5)
@Warmup(iterations = 5,time = 1,timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10,time = 1,timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ThreadLocalGetXTimeBenchmark {
	
	private static class Bean {
		private String name = "ABC";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public String stop(Bean bean) {
			return bean.getName();
		}
	}
	private static final ThreadLocal<Bean> CONTEXT = new ThreadLocal<Bean>() {

		@Override
		protected Bean initialValue() {
			return new Bean();
		}
		
	};

	protected String stop(Bean bean) {
		return bean.stop(bean);
	}


	@Benchmark
	public String getThreadLocalTwice() {
		return get().stop(get());
	}
	
	@Benchmark
	public String getThreadLocalFourTime() {
		get();
		get();
		get();
		get();
		return get().stop(get());
	}
	
	@Benchmark
	public String getThreadLocalOnce1() {
		final Bean bean = get();
		return bean.stop(bean);
	}

//@Benchmark
	public String getThreadLocalOnce2() {
		return stop(get());
	}
	
	
	protected Bean get() {
		return CONTEXT.get();
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(ThreadLocalGetXTimeBenchmark.class.getSimpleName())
			.mode(Mode.Throughput)
			.warmupIterations(8).measurementIterations(8).forks(3)
			.measurementTime(TimeValue.seconds(1)).warmupTime(TimeValue.seconds(1))
			.threads(4)
			.timeUnit(TimeUnit.MILLISECONDS)
			.build();
		new Runner(opt).run();
	 }
}