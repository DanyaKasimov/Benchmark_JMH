package backend.academy;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


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

        method = Student.class.getMethod("surname");

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        methodHandle = lookup.findVirtual(Student.class, "surname", MethodType.methodType(String.class));

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
