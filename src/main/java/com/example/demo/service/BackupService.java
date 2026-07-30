package com.example.demo.service;

import java.io.*;
import java.nio.file.*;

public class BackupService {

    public boolean backupDatabase(String mysqlBinPath, String dbUser, String dbPassword, String dbName, File outputFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    mysqlBinPath + "/mysqldump", "-u", dbUser, "-p" + dbPassword, dbName
            );
            pb.redirectOutput(outputFile);
            Process process = pb.start();
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restoreDatabase(String mysqlBinPath, String dbUser, String dbPassword, String dbName, File backupFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    mysqlBinPath + "/mysql", "-u", dbUser, "-p" + dbPassword, dbName
            );
            pb.redirectInput(backupFile);
            Process process = pb.start();
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}