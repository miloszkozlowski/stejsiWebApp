package pl.mihome.stejsiWebApp.logic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import pl.mihome.stejsiWebApp.DTO.PakietWriteModel;
import pl.mihome.stejsiWebApp.model.PakietTreningow;
import pl.mihome.stejsiWebApp.model.PakietTreningowRepo;
import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.PodopiecznyRepo;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;

class PakietTreningowServiceTest {

	@Test
	@DisplayName("Correct opening of new training package from DTO")
	void creatingNewPackageForUser() {
		//given
		var packageDto = new PakietWriteModel(1L);
		packageDto.setRodzajPakietuId(1L);
		//and
		var inMemRepo = new InMemoryPakietRepo();
		//and
		var userRepo = Mockito.mock(PodopiecznyRepo.class);
		var user = new Podopieczny();
		user.setId(1L);
		Mockito.when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
		//and
		var typeService = Mockito.mock(RodzajPakietuService.class);
		var packageType = new RodzajPakietu();
		try {
		var field = RodzajPakietu.class.getDeclaredField("id");
		field.setAccessible(true);
		field.set(packageType, 10L);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		Mockito.when(typeService.getTypeById(anyLong())).thenReturn(packageType);
		//and
		var trainingService = Mockito.mock(TreningService.class);
		
		//under test
		var packageTest = new PakietTreningowService(inMemRepo, userRepo, typeService, trainingService);
		
		//when
		packageTest.createNew(packageDto);
		
		//then
		Assertions.assertThat(inMemRepo.findByOwner(1L).size()).isGreaterThan(0);
	}
	
	private class InMemoryPakietRepo implements PakietTreningowRepo {

		private Map<Long, PakietTreningow> repo = new HashMap<>();
		private Long index = 0L;
		
		@Override
		public List<PakietTreningow> findAll() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<PakietTreningow> findByOwner(Long id) {
			return repo.entrySet().stream()
			.map(m -> m.getValue())
			.filter(p -> p.getOwner().getId()==id)
			.collect(Collectors.toList());
		}

		@Override
		public PakietTreningow save(PakietTreningow source) {
			try {
				var field = PakietTreningow.class.getDeclaredField("id");
				field.setAccessible(true);
				field.set(source, ++index);
				repo.put(index, source);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			return source;
			
		}

		@Override
		public Optional<PakietTreningow> findById(Long id) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
