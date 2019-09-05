package test;
import java.math.BigInteger;
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
@Fork(2)
@Threads(8)
@Warmup(iterations = 8, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class CreateNumberBenchmark {

	protected static final String NUM_INT="1";
	protected static final String NUM_LONG="1561622149896";
	private static final String NUM = System.getProperty("test_number", NUM_LONG);
	@Benchmark
	//@OperationsPerInvocation(3)
	public Object baseline() {//final Blackhole hole
		final String number =  NUM;
		return createNumber(1,number,10);
	}
	

	@Benchmark
	//@OperationsPerInvocation(3)
	public Object newWay() {//final Blackhole hole
		final String number = NUM;
		return createNumber2(1,number,10);
	}
	
	//@Benchmark
	//@OperationsPerInvocation(3)
	public Object newWay3() {//final Blackhole hole
		final String number = NUM;
		return createNumber3(1,number,10);
	}
	
    private static Number createNumber(int sign, String number, int radix) {
        Number result;
        if (sign < 0) {
            number = "-" + number;
        }
        try {
            result = Integer.valueOf(number, radix);
        } catch (NumberFormatException e) {
            result = createLongOrBigInteger(number, radix);
        }
        return result;
    }

    private static final int MAX_INTEGER_LEN =Integer.toString(Integer.MAX_VALUE).length();
    private static final int MAX_LONG_LEN =Long.toString(Long.MAX_VALUE).length();
    
    private static Number createNumber2(final int sign, String number,final int radix) {
        final int len = number != null ? number.length() : 0;
        if (sign < 0) {
            number = "-" + number;
        }
        final boolean gtInt = len >MAX_INTEGER_LEN;
        if(gtInt) {
            if(len > MAX_LONG_LEN) {
                return new BigInteger(number, radix);
            }
            return createLongOrBigInteger(number, radix);
        }
        Number result;
        try {
            result = Integer.valueOf(number, radix);
        } catch (NumberFormatException e) {
        	result = createLongOrBigInteger(number, radix);
        }
        return result;
    }

    private static Number createNumber3(final int sign, String number,final int radix) {
        final int len = number != null ? number.length() : 0;
        if (sign < 0) {
            number = "-" + number;
        }
        final boolean gtInt = len >MAX_INTEGER_LEN;
        if(gtInt) {
            return len > MAX_LONG_LEN ? new BigInteger(number, radix) 
                       : createLongOrBigInteger(number, radix);
        }
//        else if ( len < MAX_INTEGER_LEN ) {
//           return Integer.valueOf(number, radix);
//        }
        Number result;
        try {
            result = Integer.valueOf(number, radix);
        } catch (NumberFormatException e) {
        	result = createLongOrBigInteger(number, radix);
        }
        return result;
    }
    
	protected static final Number createLongOrBigInteger(final String number,final  int radix) {
		//Number result;
		try {
		    return Long.valueOf(number, radix);
		} catch (NumberFormatException e1) {
		    return  new BigInteger(number, radix);
		}
		//return result;
	}

    
	public static void main(String[] args) throws Exception {
		if (false) {
			final long time = System.currentTimeMillis();
			System.out.println("==>"+Integer.MIN_VALUE+":"+Integer.MAX_VALUE);
			return;
		}
		Options opt = new OptionsBuilder().include(CreateNumberBenchmark.class.getSimpleName())
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

Benchmark                        Mode  Cnt   Score   Error   Units  Score/min
CreateNumberBenchmark.baseline  thrpt   20   2.986 ± 0.162  ops/us      1.000
CreateNumberBenchmark.newWay    thrpt   20  85.324 ± 8.333  ops/us     28.570


number=1
Benchmark                        Mode  Cnt    Score    Error   Units  Score/min
CreateNumberBenchmark.baseline  thrpt   20  393.441 ±  2.525  ops/us      1.051
CreateNumberBenchmark.newWay    thrpt   20  374.351 ± 29.902  ops/us      1.000

 */
}
