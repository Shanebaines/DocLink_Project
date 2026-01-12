package com.springbootpractice.doclink.Listner.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springbootpractice.doclink.Kernal.Service.BookmarkService;
import com.springbootpractice.doclink.Listner.Dto.Response.BookmarkDoctorDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"})
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<String> addBookmark(@RequestParam Long patientId, @RequestParam Long doctorId) {
        log.info("Add bookmark request: patient={} doctor={}", patientId, doctorId);
        return bookmarkService.addBookmark(patientId, doctorId);
    }

    @DeleteMapping
    public ResponseEntity<String> removeBookmark(@RequestParam Long patientId, @RequestParam Long doctorId) {
        log.info("Remove bookmark request: patient={} doctor={}", patientId, doctorId);
        return bookmarkService.removeBookmark(patientId, doctorId);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<List<BookmarkDoctorDto>> listBookmarks(@PathVariable Long patientId) {
        log.info("List bookmarks request: patient={}", patientId);
        return bookmarkService.listBookmarks(patientId);
    }
}
