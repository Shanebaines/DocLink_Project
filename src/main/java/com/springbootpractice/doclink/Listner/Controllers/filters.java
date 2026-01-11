package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernel.Service.filterService;
import com.springbootpractice.doclink.Listner.Dto.Response.WorkPLaceDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/filter")
@AllArgsConstructor
public class filters {
    private final filterService filterService;

    @GetMapping("/todayWorkPlaces")
    public ResponseEntity<List<WorkPLaceDto>> viewTodayWorkPlaces(@RequestParam Long id){
        return filterService.viewTodayWorkPlaces(id);
    }

    @GetMapping("/workPLacesByDay")
    public ResponseEntity<List<WorkPLaceDto>> viewWorkPlacesByDay(@RequestParam Long id, @RequestParam DayOfWeek dayOfWeek){
        return filterService.viewWorkPlacesByDay(id, dayOfWeek);
    }

    @GetMapping("/workPlaceNow")
    public ResponseEntity<List<WorkPLaceDto>> viewWorkPlaceNow(@RequestParam Long id){
        return filterService.viewWorkPlaceNow(id);
    }
}
