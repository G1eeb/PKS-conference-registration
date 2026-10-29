package ru.mirea.conference;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import ru.mirea.conference.model.*;
import java.time.LocalDate;
public class ModelTest {
 @Test void conferenceDateRangeIsRepresented(){Conference c=new Conference("Test",ConferenceTrack.IT,LocalDate.of(2026,1,1),LocalDate.of(2026,1,2),"Москва");assertThat(c.getEndDate()).isAfterOrEqualTo(c.getStartDate());}
 @Test void registrationStatusesContainRequiredBusinessStates(){assertThat(RegistrationStatus.values()).contains(RegistrationStatus.CREATED,RegistrationStatus.CONFIRMED,RegistrationStatus.PAID,RegistrationStatus.ATTENDED,RegistrationStatus.CANCELLED);}
}