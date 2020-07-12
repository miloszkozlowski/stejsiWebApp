package pl.mihome.stejsiWebApp.logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import pl.mihome.stejsiWebApp.model.Trening;
import pl.mihome.stejsiWebApp.model.TreningRepo;

class TreningServiceTest {

	@Test
	@DisplayName("If returns weekly list of daily list trainings  ")
	void getTrainingsWeeklyTest() {
		//given
		var trening1 = new Trening();
		trening1.setScheduledFor(LocalDateTime.of(2020, 4, 17, 17, 0));
		var trening2 = new Trening();
		trening2.setScheduledFor(LocalDateTime.of(2020, 4, 22, 1, 30));
		var trainingList = new ArrayList<Trening>();
		trainingList.add(trening1);
		trainingList.add(trening2);
		
		var treningRepo = Mockito.mock(TreningRepo.class);
		Mockito.when(treningRepo.findByScheduledForGreaterThanEqualAndScheduledForLessThan(Mockito.any(), Mockito.any())).thenReturn(trainingList);
		
		var trainingService = new TreningService(treningRepo, null, null, null, null, null, null);
		
		//when
		var weeklyListOfTrainingList1 = trainingService.getTrainingsWeekly(LocalDate.of(2020, 4, 17).atStartOfDay());
		var weeklyListOfTrainingList2 = trainingService.getTrainingsWeekly(LocalDate.of(2020, 4, 20).atStartOfDay());
		
		//then
		Assertions.assertThat(weeklyListOfTrainingList1).hasSize(1);
		Assertions.assertThat(weeklyListOfTrainingList1).hasSize(1);
		
		Assertions.assertThat(weeklyListOfTrainingList1.stream()
				.flatMap(l -> l.stream())
				.count()).isEqualTo(2);
		
		Assertions.assertThat(weeklyListOfTrainingList2.stream()
				.flatMap(l -> l.stream())
				.count()).isEqualTo(2);
	}

}
