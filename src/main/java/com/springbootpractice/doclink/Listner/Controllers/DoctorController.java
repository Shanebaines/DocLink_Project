package com.springbootpractice.doclink.Listner.Controllers;

import com.springbootpractice.doclink.Kernal.Service.DoctorService;
import com.springbootpractice.doclink.Listner.Dto.Response.ViewDoctorDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor")
@AllArgsConstructor
public class DoctorController {
    public final DoctorService doctorService;

    @GetMapping("/view")
    public ResponseEntity<ViewDoctorDto> viewDoctor(@RequestParam Integer id) {
        return doctorService.viewDoctor(Long.valueOf(id));
    }
}
