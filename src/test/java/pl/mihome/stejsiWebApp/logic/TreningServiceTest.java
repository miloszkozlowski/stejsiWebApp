package pl.mihome.stejsiWebApp.logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import pl.mihome.stejsiWebApp.model.PakietTreningow;
import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.Trening;
import pl.mihome.stejsiWebApp.model.TreningRepo;

class TreningServiceTest {

	@Test
	@DisplayName("If returns weekly list of daily list trainings  ")
	void getTrainingsWeeklyTest() {
		//given
		var trainingList = new ArrayList<Trening>();
		
		//and
		try {
			var owner = new Podopieczny();
			var fieldOwner = Podopieczny.class.getDeclaredField("id");
			fieldOwner.setAccessible(true);
			fieldOwner.set(owner, 3L);
		//and
			var tPackage = new PakietTreningow();
			tPackage.setOwner(owner);
			var fieldPackage = PakietTreningow.class.getDeclaredField("id");
			fieldPackage.setAccessible(true);
			fieldPackage.set(tPackage, 3L);
		//and
			

			var trening1 = new Trening();
			var field = Trening.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(trening1, 1);
			trening1.setTrainingPackage(tPackage);

			trening1.setScheduledFor(LocalDateTime.of(2020, 4, 17, 17, 0));
			var trening2 = new Trening();
			field.set(trening1, 2);
			trening2.setTrainingPackage(tPackage);
			trening2.setScheduledFor(LocalDateTime.of(2020, 4, 22, 1, 30));
			trainingList.add(trening1);
			trainingList.add(trening2);
			tPackage.setTrainings(Set.of(trening1, trening2));
		}
		catch(NoSuchFieldException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
		
		var treningRepo = Mockito.mock(TreningRepo.class);
		Mockito.when(treningRepo.findByScheduledForGreaterThanEqualAndScheduledForLessThan(Mockito.any(), Mockito.any())).thenReturn(trainingList);
		
		var trainingService = new TreningService(treningRepo, null, null, null, null, null, null);
		
		//when
		var weeklyListOfTrainingList1 = trainingService.getTrainingsWeekly(LocalDate.of(2020, 4, 17).atStartOfDay());
		var weeklyListOfTrainingList2 = trainingService.getTrainingsWeekly(LocalDate.of(2020, 4, 20).atStartOfDay());
		
		//then
		Assertions.assertThat(weeklyListOfTrainingList1).hasSize(7);
		Assertions.assertThat(weeklyListOfTrainingList1).hasSize(7);
		
		Assertions.assertThat(weeklyListOfTrainingList1.stream()
				.flatMap(l -> l.stream())
				.count()).isEqualTo(2);
		
		Assertions.assertThat(weeklyListOfTrainingList2.stream()
				.flatMap(l -> l.stream())
				.count()).isEqualTo(1L);
	}

}
