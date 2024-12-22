package backend.academy;

import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@UtilityClass
@SuppressWarnings("MagicNumber")
public class Main {
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
            .include(CallMethodBenchmark.class.getSimpleName())
            .shouldFailOnError(true)
            .shouldDoGC(true)
            .mode(Mode.AverageTime)
            .timeUnit(TimeUnit.NANOSECONDS)
            .forks(2)
            .warmupForks(1)
            .warmupIterations(3)
            .warmupTime(TimeValue.seconds(3))
            .measurementIterations(5)
            .measurementTime(TimeValue.seconds(5))
            .build();

        new Runner(options).run();
    }
}
