package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.AppointmentService;
import com.springbootpractice.doclink.Listner.Dto.Response.viewAppointmentsDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("appointment")
@AllArgsConstructor
public class AppointmentController {
    public final AppointmentService appointmentService;

    @GetMapping("/viewMyAppointments")
    public ResponseEntity<List<viewAppointmentsDto>> viewAppointments(@RequestParam Long id){
        return appointmentService.viewAppointments(id);
    }

}
