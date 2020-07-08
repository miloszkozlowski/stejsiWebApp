package pl.mihome.stejsiWebApp.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pl.mihome.stejsiWebApp.DTO.PakietReadModel;
import pl.mihome.stejsiWebApp.DTO.TreningReadModel;
import pl.mihome.stejsiWebApp.exeptions.CallendarTimeConflictException;
import pl.mihome.stejsiWebApp.logic.LokalizacjaService;
import pl.mihome.stejsiWebApp.logic.PakietTreningowService;
import pl.mihome.stejsiWebApp.logic.TreningService;
import pl.mihome.stejsiWebApp.model.Trening;

@Controller
@Secured("ROLE_STEJSI")
@RequestMapping("/plan")
public class PlanController {
	
	private PakietTreningowService service;
	private LokalizacjaService locationService;
	private TreningService traininigService;

	
	PlanController(PakietTreningowService service, LokalizacjaService locationService, TreningService traininigService) {
		this.service = service;
		this.locationService = locationService;
		this.traininigService = traininigService;
	}

	
	@GetMapping
	String showCallendar(@RequestParam(required = false) Integer week,
			 Model model) {
		

		//LocalDate weekEnd = weekStart.plusDays(6L);
		
		if(week == null)
			week = 0;
		model.addAttribute("weekDates", weekDates(weekStart(week)));
		model.addAttribute("week", week);
		model.addAttribute("weeklyTrainings", traininigService.getTrainingsWeekly(weekStart(week).atStartOfDay()));
		
		return "callendar";
	}
	
	@GetMapping("/zmien/{tid}")
	String changeOneTraining(@PathVariable Long tid,
			@RequestParam(required = false) Integer week,
			Model model) {
		var training = new TreningReadModel(traininigService.getOneToChange(tid));
		//var traininigPackage = training.getTrainingPackage();
		model.addAttribute("opcja", "trainingChange");
		model.addAttribute("training", training);
		if(week == null)
			week = 0;
		model.addAttribute("weekDates", weekDates(weekStart(week)));
		model.addAttribute("week", week);
		model.addAttribute("weeklyTrainings", traininigService.getTrainingsWeekly(weekStart(week).atStartOfDay()));
		model.addAttribute("locations", locationService.getAll());
		
		return "callendar";
	}
	
	@GetMapping("/{pid}/przywroc/{tid}")
	String unCancelTraining(@PathVariable Long tid,
			@PathVariable Long pid,
			@RequestParam(required = false) Integer week,
			Model model) {
		
		traininigService.unCancel(tid);
		
		if(week == null)
			week = 0;
		model.addAttribute("weekDates", weekDates(weekStart(week)));
		model.addAttribute("week", week);
		model.addAttribute("weeklyTrainings", traininigService.getTrainingsWeekly(weekStart(week).atStartOfDay()));
		model.addAttribute("opcja", "packagePlan");
		model.addAttribute("package", service.getByIdEagerly(pid));
		model.addAttribute("locations", locationService.getAll());
	
		
		return "callendar";
	}
	
	@GetMapping("/{pid}/usun/{tid}")
	String removeOne(@PathVariable Long pid,
			@PathVariable Long tid,
			@RequestParam(required = false) Integer week,
			Model model) {
		
		traininigService.removeSchedule(pid, tid);
		
		if(week == null)
			week = 0;
		model.addAttribute("weekDates", weekDates(weekStart(week)));
		model.addAttribute("week", week);
		model.addAttribute("weeklyTrainings", traininigService.getTrainingsWeekly(weekStart(week).atStartOfDay()));
		model.addAttribute("opcja", "packagePlan");
		model.addAttribute("package", service.getByIdEagerly(pid));
		model.addAttribute("locations", locationService.getAll());
	
		return "callendar";
	}
	
	@GetMapping("/{pid}/obecny/{tid}")
	String confirmPresence(@PathVariable Long pid,
			@PathVariable Long tid,
			@RequestParam(required = false) Integer week,
			Model model) {
		
		traininigService.confirmPresence(pid, tid);
		
		if(week == null)
			week = 0;
		model.addAttribute("weekDates", weekDates(weekStart(week)));
		model.addAttribute("week", week);
		model.addAttribute("weeklyTrainings", traininigService.getTrainingsWeekly(weekStart(week).atStartOfDay()));
		model.addAttribute("opcja", "packagePlan");
		model.addAttribute("package", service.getByIdEagerly(pid));
		model.addAttribute("locations", locationService.getAll());
		
		
		return "callendar";
	}
	
	@GetMapping("/{pid}")
	String planPackageInCallendar(@PathVariable Long pid,
			@RequestParam(required = false) Integer week,
			Model model) {
		
		if(week == null)
			week = 0;
		model.addAttribute("weekDates", weekDates(weekStart(week)));
		model.addAttribute("week", week);
		model.addAttribute("opcja", "packagePlan");
		model.addAttribute("weeklyTrainings", traininigService.getTrainingsWeekly(weekStart(week).atStartOfDay()));
		var trainingPackage = service.getByIdEagerly(pid);
		model.addAttribute("package", trainingPackage);
		model.addAttribute("defaultLocation", defaultLocation(trainingPackage));
		model.addAttribute("locations", locationService.getAll());
		
		
		return "callendar";
	}
	
	@PostMapping("/{pid}")
	String scheduleTraining(@PathVariable Long pid,
			@RequestParam("tid") Long trainingId,
			@RequestParam String trainingDate,
			@RequestParam String trainingTime,
			@RequestParam(required = false) Long locationId,
			@RequestParam Integer week,
			Model model) {
		
		if(week == null)
			week = 0;
		
		try {
			traininigService.schedule(trainingId, trainingDate, trainingTime, Optional.ofNullable(locationId));
		}
		catch(CallendarTimeConflictException ex) {
			model.addAttribute("msg", "Termin " + ex.getReadableTermin() + " jest już zajęty");
			week = (int)(ChronoUnit.WEEKS.between(weekStart(0), ex.getTermin().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))));
		}
		

		model.addAttribute("weekDates", weekDates(weekStart(week)));
		model.addAttribute("week", week);
		model.addAttribute("weeklyTrainings", traininigService.getTrainingsWeekly(weekStart(week).atStartOfDay()));
		model.addAttribute("opcja", "packagePlan");
		var trainingPackage = service.getByIdEagerly(pid);
		model.addAttribute("package", trainingPackage);
		model.addAttribute("defaultLocation", defaultLocation(trainingPackage));
		model.addAttribute("locations", locationService.getAll());

		
		return "callendar";
	}
	
	Long defaultLocation(PakietReadModel pakiet) {
		Long lastPickedLocation = pakiet.getTrainings().stream()
		.filter(t -> t.getLocation() != null)
		.sorted(Comparator.comparing(TreningReadModel::getScheduledFor, Comparator.nullsFirst(Comparator.naturalOrder())).reversed())
		.findFirst()
		.map(t -> t.getLocation().getId())
		.orElse(0L);
		
		if(lastPickedLocation > 0)
			return lastPickedLocation;
		else
			return locationService.getDefaultId();
		
	}
	
	List<String> weekDates(LocalDate weekStart) {
		return Stream.iterate(weekStart, d -> d.plusDays(1))
				.limit(7)
				.map(d -> d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.collect(Collectors.toList());
	}
	
	LocalDate weekStart(Integer week) {
		LocalDate today = LocalDate.now();
		return today.with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1L).plusWeeks(week);
	}
}
