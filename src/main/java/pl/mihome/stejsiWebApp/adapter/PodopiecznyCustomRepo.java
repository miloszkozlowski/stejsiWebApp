package pl.mihome.stejsiWebApp.adapter;

import org.springframework.stereotype.Repository;
import pl.mihome.stejsiWebApp.model.Podopieczny;

import java.util.List;

@Repository
public interface PodopiecznyCustomRepo {

    List<Podopieczny> elasticSearchFindByKeyWord(String keyWord);

    List<Podopieczny> elasticSearchFindByPhoneNumber(String keyWord);
}
