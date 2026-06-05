package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.entity.BackupHistory;
import com.unipart.unipart_backend.entity.ScheduleConfig;
import com.unipart.unipart_backend.repository.BackupHistoryRepository;
import com.unipart.unipart_backend.repository.ScheduleConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class BackupService {

    @Value("${app.backup.dir:./backups/}")
    private String backupDirPath;

    @Value("${app.backup.mysqldump.path:mysqldump}")
    private String mysqldumpPath;

    @Value("${app.backup.mysql.path:mysql}")
    private String mysqlPath;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    @Value("${spring.datasource.url}")
    private String dbUrl; 

    @Autowired
    private BackupHistoryRepository backupHistoryRepository;

    @Autowired
    private ScheduleConfigRepository scheduleConfigRepository;

    private String getDbName() {
        try {
            String cleanUrl = dbUrl.substring(dbUrl.indexOf("://") + 3);
            String hostPortDb = cleanUrl.substring(0, cleanUrl.indexOf("?"));
            return hostPortDb.substring(hostPortDb.indexOf("/") + 1);
        } catch (Exception e) {
            return "UniPartDB";
        }
    }

    private String getDbHost() {
        try {
            String cleanUrl = dbUrl.substring(dbUrl.indexOf("://") + 3);
            String hostPort = cleanUrl.substring(0, cleanUrl.indexOf("/"));
            if (hostPort.contains(":")) {
                return hostPort.split(":")[0];
            }
            return hostPort;
        } catch (Exception e) {
            return "localhost";
        }
    }

    private String getDbPort() {
        try {
            String cleanUrl = dbUrl.substring(dbUrl.indexOf("://") + 3);
            String hostPort = cleanUrl.substring(0, cleanUrl.indexOf("/"));
            if (hostPort.contains(":")) {
                return hostPort.split(":")[1];
            }
            return "3306";
        } catch (Exception e) {
            return "3306";
        }
    }

    public BackupHistory createBackup(String type) {
        long startTime = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String fileName = "backup_" + type + "_" + dateStr + ".zip";
        
        BackupHistory history = BackupHistory.builder()
                .type(type)
                .status("running")
                .date(now)
                .fileName(fileName)
                .build();
        history = backupHistoryRepository.save(history);

        try {
            Path backupDir = Paths.get(backupDirPath);
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }

            String dbName = getDbName();
            String dbHost = getDbHost();
            String dbPort = getDbPort();
            
            ProcessBuilder pb = new ProcessBuilder(
                    mysqldumpPath,
                    "-h" + dbHost,
                    "-P" + dbPort,
                    "-u" + dbUser,
                    "--databases",
                    dbName
            );
            
            pb.environment().put("MYSQL_PWD", dbPass);
            
            Process process = pb.start();

            File zipFile = new File(backupDir.toFile(), fileName);
            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos);
                 InputStream is = process.getInputStream()) {
                
                ZipEntry entry = new ZipEntry("database-dump.sql");
                zos.putNextEntry(entry);
                
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                history.setStatus("completed");
                log.info("Backup successfully created: {}", fileName);
            } else {
                history.setStatus("failed");
                log.error("mysqldump failed with exit code: {}", exitCode);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        log.error("mysqldump error: {}", line);
                    }
                }
            }
        } catch (Exception e) {
            history.setStatus("failed");
            log.error("Backup process encountered an exception", e);
        }

        long endTime = System.currentTimeMillis();
        long durationSec = (endTime - startTime) / 1000;
        history.setDuration(durationSec + " giây");

        return backupHistoryRepository.save(history);
    }

    public void restoreBackup(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        boolean isZip = originalFilename != null && originalFilename.toLowerCase().endsWith(".zip");
        boolean isSql = originalFilename != null && originalFilename.toLowerCase().endsWith(".sql");

        if (!isZip && !isSql) {
            throw new IllegalArgumentException("Chỉ chấp nhận file .zip hoặc .sql");
        }

        Path tempSqlFile = null;

        try {
            tempSqlFile = Files.createTempFile("restore_", ".sql");

            if (isZip) {
                try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
                    ZipEntry entry = zis.getNextEntry();
                    if (entry != null) {
                        Files.copy(zis, tempSqlFile, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        throw new IllegalArgumentException("File zip không hợp lệ hoặc rỗng");
                    }
                }
            } else {
                try (InputStream is = file.getInputStream()) {
                    Files.copy(is, tempSqlFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            String dbName = getDbName();
            String dbHost = getDbHost();
            String dbPort = getDbPort();
            
            ProcessBuilder pb = new ProcessBuilder(
                    mysqlPath,
                    "-h" + dbHost,
                    "-P" + dbPort,
                    "-u" + dbUser,
                    dbName
            );

            pb.redirectInput(tempSqlFile.toFile());
            pb.environment().put("MYSQL_PWD", dbPass);

            Process process = pb.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("mysql restore failed with exit code: {}", exitCode);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        log.error("mysql error: {}", line);
                    }
                }
                throw new RuntimeException("Lỗi phục hồi dữ liệu! Mã lỗi: " + exitCode);
            }

            log.info("Restore process completed successfully.");

        } finally {
            if (tempSqlFile != null && Files.exists(tempSqlFile)) {
                try {
                    Files.delete(tempSqlFile);
                } catch (IOException e) {
                    log.warn("Không thể xóa file tạm: {}", tempSqlFile);
                }
            }
        }
    }

    public List<BackupHistory> getHistory() {
        return backupHistoryRepository.findAllByOrderByDateDesc();
    }

    public Resource getBackupFile(Long id) throws Exception {
        BackupHistory history = backupHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Backup not found"));
        Path filePath = Paths.get(backupDirPath).resolve(history.getFileName()).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new FileNotFoundException("File not found: " + history.getFileName());
        }
    }

    @Transactional
    public ScheduleConfig getSchedule() {
        return scheduleConfigRepository.findById(1L).orElseGet(() -> {
            ScheduleConfig config = ScheduleConfig.builder()
                    .id(1L)
                    .fullEnabled(true)
                    .fullTime("02:00")
                    .fullFrequency("daily")
                    .incrementalEnabled(true)
                    .incrementalEvery("6")
                    .build();
            return scheduleConfigRepository.save(config);
        });
    }

    @Transactional
    public ScheduleConfig updateSchedule(ScheduleConfig config) {
        config.setId(1L);
        return scheduleConfigRepository.save(config);
    }
}
