package pl.mihome.stejsiWebApp.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pl.mihome.stejsiWebApp.model.TipPicture;
import pl.mihome.stejsiWebApp.model.TipPictureRepo;

import java.util.Optional;

@Repository
interface TipPictureSQLRepo extends TipPictureRepo, JpaRepository<TipPicture, Long> {

}
