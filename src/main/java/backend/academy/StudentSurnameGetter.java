package backend.academy;

@FunctionalInterface
public interface StudentSurnameGetter {
    String getSurname(Student student);
}
