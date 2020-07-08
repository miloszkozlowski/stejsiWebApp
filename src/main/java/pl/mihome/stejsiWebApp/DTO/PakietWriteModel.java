package pl.mihome.stejsiWebApp.DTO;

import pl.mihome.stejsiWebApp.model.PakietTreningow;
import pl.mihome.stejsiWebApp.model.Podopieczny;
import pl.mihome.stejsiWebApp.model.RodzajPakietu;

public class PakietWriteModel {

	private Long podopiecznyId;
	private Long rodzajPakietuId;
	
	public PakietWriteModel(Long podopiecznyId) {
		this.podopiecznyId = podopiecznyId;
	}

	public Long getPodopiecznyId() {
		return podopiecznyId;
	}

	public void setPodopiecznyId(Long podopiecznyId) {
		this.podopiecznyId = podopiecznyId;
	}

	public Long getRodzajPakietuId() {
		return rodzajPakietuId;
	}

	public void setRodzajPakietuId(Long rodzajPakietuId) {
		this.rodzajPakietuId = rodzajPakietuId;
	}
	
	public PakietTreningow toPakietTreningow(Podopieczny owner, RodzajPakietu type) {
		return new PakietTreningow(owner, type);
	}
	
	
}
