package backend.academy;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class CallMethodBenchmark {

    private Student student;

    private Method method;

    private MethodHandle methodHandle;

    private StudentSurnameGetter lambdaGetter;


    @Setup(Level.Trial)
    public void setup() throws Throwable {
        student = new Student("Danil", "Kasimov");
        String methodName = "surname";

        method = Student.class.getMethod(methodName);

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        methodHandle = lookup.findVirtual(Student.class, methodName, MethodType.methodType(String.class));

        CallSite site = LambdaMetafactory.metafactory(
            lookup,
            "getSurname",
            MethodType.methodType(StudentSurnameGetter.class),
            MethodType.methodType(String.class, Student.class),
            methodHandle,
            MethodType.methodType(String.class, Student.class)
        );
        lambdaGetter = (StudentSurnameGetter) site.getTarget().invokeExact();

    }

    @Benchmark
    public void directCall(Blackhole blackhole) {
        blackhole.consume(student.name());
    }

    @Benchmark
    public void reflectionCall(Blackhole blackhole) throws Exception {
        blackhole.consume(method.invoke(student));
    }

    @Benchmark
    public void methodHandlesCall(Blackhole blackhole) throws Throwable {
        blackhole.consume((String) methodHandle.invoke(student));
    }

    @Benchmark
    public void lambdaMetafactoryCall(Blackhole blackhole) {
        blackhole.consume(lambdaGetter.getSurname(student));
    }
}
