package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.entity.BackupHistory;
import com.unipart.unipart_backend.entity.ScheduleConfig;
import com.unipart.unipart_backend.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/backup")
@PreAuthorize("hasRole('ADMIN')")
public class BackupController {

    @Autowired
    private BackupService backupService;

    @PostMapping("/create")
    public ResponseEntity<BackupHistory> createBackup(@RequestParam(defaultValue = "full") String type) {
        BackupHistory history = backupService.createBackup(type);
        return ResponseEntity.ok(history);
    }

    @PostMapping(value = "/restore", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> restoreBackup(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            backupService.restoreBackup(file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<BackupHistory>> getHistory() {
        return ResponseEntity.ok(backupService.getHistory());
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadBackup(@PathVariable Long id) {
        try {
            Resource resource = backupService.getBackupFile(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/schedule")
    public ResponseEntity<ScheduleConfig> getSchedule() {
        return ResponseEntity.ok(backupService.getSchedule());
    }

    @PutMapping("/schedule")
    public ResponseEntity<ScheduleConfig> updateSchedule(@RequestBody ScheduleConfig config) {
        return ResponseEntity.ok(backupService.updateSchedule(config));
    }
}
