package pl.mihome.stejsiWebApp.DTO;


import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pl.mihome.stejsiWebApp.model.PakietTreningow;
import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;
import pl.mihome.stejsiWebApp.model.Trening;

class PakietReadModelTest {

	@Test
	@DisplayName("Count planned trainings in package when no trainings planned")
	void countPlannedTrainings() {
		//given
		var user = new Podopieczny();
		var pakiet = new PakietTreningow();
		var rodzaj = new RodzajPakietu();
		Set<Trening> trainings = new HashSet<Trening>();
		for(int i = 0; i<5; i++) {
			var trening = new Trening();
			trening.setTrainingPackage(pakiet);
			trainings.add(trening);
		}
		pakiet.setTrainings(trainings);
		pakiet.setPackageType(rodzaj);
		pakiet.setOwner(user);
		Set<PakietTreningow> pakiety = new HashSet<PakietTreningow>();
		pakiety.add(pakiet);
		user.setTrainingPackages(pakiety);
				

		
		//under test
		var pakietDTO = new PakietReadModel(pakiet, new PodopiecznyReadModel(user));
		
		//when
		var amount = pakietDTO.getAmountTrainingsPlanned();
		
		//then
		Assertions.assertThat(amount).isEqualTo(0);
	}

}
