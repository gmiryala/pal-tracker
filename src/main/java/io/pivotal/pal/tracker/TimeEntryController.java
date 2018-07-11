package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {
    private TimeEntryRepository timeEntryRepository;
    private final CounterService counter;
    private final GaugeService gauge;

    @Autowired
    public TimeEntryController(TimeEntryRepository timeEntryRepository,CounterService counter,
                               GaugeService gauge) {
        this.timeEntryRepository = timeEntryRepository;
        this.counter = counter;
        this.gauge = gauge;
    }

    @PostMapping("/time-entries")
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry createdTimeEntry = timeEntryRepository.create(timeEntryToCreate);
        counter.increment("TimeEntry.created");
        gauge.submit("timeEntries.count", timeEntryRepository.list().size());

        return new ResponseEntity<>(createdTimeEntry, HttpStatus.CREATED);
    }

    @GetMapping("/time-entries/{l}")
    public ResponseEntity<TimeEntry> read(@PathVariable long l) {
        TimeEntry timeEntry = timeEntryRepository.find(l);
        if(timeEntry != null) {
            counter.increment("TimeEntry.read");
            return new ResponseEntity<>(timeEntry, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(timeEntry, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list() {
        counter.increment("TimeEntry.listed");
        return new ResponseEntity<>(timeEntryRepository.list(), HttpStatus.OK);
    }

    @PutMapping("/time-entries/{l}")
    public ResponseEntity<TimeEntry> update(@PathVariable long l, @RequestBody TimeEntry expected) {
        TimeEntry timeEntry = timeEntryRepository.update(l, expected);

        if(timeEntry != null){
            counter.increment("TimeEntry.updated");
            return new ResponseEntity<>(timeEntry, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(timeEntry, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/time-entries/{l}")
    public ResponseEntity delete(@PathVariable long l) {
        timeEntryRepository.delete(l);
        counter.increment("TimeEntry.deleted");
        gauge.submit("timeEntries.count", timeEntryRepository.list().size());
        return new ResponseEntity<>( HttpStatus.NO_CONTENT);
    }
}
