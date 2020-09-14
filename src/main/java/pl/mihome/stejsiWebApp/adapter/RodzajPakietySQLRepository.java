package pl.mihome.stejsiWebApp.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.mihome.stejsiWebApp.model.RodzajPakietu;
import pl.mihome.stejsiWebApp.model.RodzajPakietuRepo;

@Repository
interface RodzajPakietySQLRepository extends RodzajPakietuRepo, JpaRepository<RodzajPakietu, Long> {

}
