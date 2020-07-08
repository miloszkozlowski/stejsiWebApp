package pl.mihome.stejsiWebApp.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.mihome.stejsiWebApp.model.TipComment;
import pl.mihome.stejsiWebApp.model.TipCommentRepo;

@Repository
interface TipCommentSQLRepo extends TipCommentRepo, JpaRepository<TipComment, Long>{

}
