package pl.mihome.stejsiWebApp.model;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pl.mihome.stejsiWebApp.DTO.TreningReadModel;

class TreningDTOEntityTest {

	@Test
	@DisplayName("Returns true if markedAsDone date exits")
	void markedAsDone() {
		
		//given
		var trening = new Trening();
		trening.setMarkedAsDone(LocalDateTime.now());
		
		var treningDTO = new TreningReadModel(trening);
		
		//when and then
		Assertions.assertThat(treningDTO.isDone()).isTrue();
	}
	
	@Test
	@DisplayName("Returns false if markedAsDone date is null")
	void markedAsDoneFalse() {
		
		//given
		var trening = new Trening();
		
		var treningDTO = new TreningReadModel(trening);
		
		//when and then
		Assertions.assertThat(treningDTO.isDone()).isFalse();
	}

}
