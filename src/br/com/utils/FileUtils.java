package br.com.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public static String split(final Integer qtd, final String path, final String directoryWhereCreateNewFiles,
            final String newFilesName) throws IOException, IllegalArgumentException, InterruptedException {

        if (!isFile(path)) {
            final String msg = "Caminho de origem do arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }

        if (!isDirectory(directoryWhereCreateNewFiles)) {
            final String msg = "Caminho de destino dos arquivos inválido.";
            throw new IllegalArgumentException(msg);
        }

        final Integer count = countLines(path);
        if (qtd < 1 || qtd > count) {
            final String msg = "O tamanho dos arquivos divididos não pode ser igual menor que 1 uma linha   "
                    + "nem maior que o número de linhas do arquivo original.";
            throw new IllegalArgumentException(msg);
        }

        final FileInputStream fis = new FileInputStream(path);

        try (Scanner scanner = new Scanner(fis)) {
            try {
                Integer fileNumber = 0, countLines = 0;
                FileWriter fw = null;
                String directoryOfSplitedFiles = null;
                String fileHeader = "";
                if (scanner.hasNextLine()) {
                    fileHeader = scanner.nextLine();
                }
                try {
                    while (scanner.hasNextLine()) {
                        final String linha = scanner.nextLine();
                        if (countLines == 0) { // Se entrar gera um novo arquivo
                            fileNumber++;
                            final File fileCreated = create(fileNumber, path, directoryWhereCreateNewFiles, newFilesName);
                            if (fileCreated != null) {
                                fw = new FileWriter(fileCreated);
                                directoryOfSplitedFiles = getDirectory(fileCreated.getPath());
                                fw.write(fileHeader + "\n");
                                countLines++;
                                countLines++;
                            } else {
                                final String newFilePath = FileUtils.getName(fileNumber,
                                        path, directoryWhereCreateNewFiles, newFilesName);
                                directoryOfSplitedFiles = getDirectory(newFilePath);
                            }
                        }
                        countLines++;
                        if (fw != null) {
                            fw.write(linha + "\n");
                            if (countLines >= qtd) {
                                countLines = 0;
                                fw.flush();
                                fw.close();
                            }
                        }
                    }
                } finally {
                    if (fw != null) {
                        fw.flush();
                        fw.close();
                    }
                }
                return directoryOfSplitedFiles;
            } finally {
                scanner.close();
            }
        } finally {
            fis.close();
        }
    }

    public static Integer countLines(final String path) throws FileNotFoundException, IOException, IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        Integer qtd;
        final FileReader fr = new FileReader(path);
        try (LineNumberReader reader = new LineNumberReader(fr)) {
            try {
                while (reader.readLine() != null) {
                }
                qtd = reader.getLineNumber();
            } finally {
                reader.close();
            }
        } finally {
            fr.close();
        }
        return qtd;
    }

    public static Boolean isAllowRead(final String path) throws IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        return new File(path).canRead();
    }

    public static Boolean isHidden(final String path) throws IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        return new File(path).isHidden();
    }

    public static Boolean isAllowWrite(final String path) throws IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        return new File(path).canWrite();
    }

    public static Long getLastModification(final String path) throws IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        return new File(path).lastModified();
    }

    public static void renameIfExists(final String path, final String newName) throws IllegalArgumentException {

        if (isFile(path)) {
            final String directiry = getDirectory(path);
            final String tmp = directiry + File.separator + newName;
            new File(path).renameTo(new File(tmp));
        } else {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
    }

    public static Boolean anableJustReadPermission(final String path) throws IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        return new File(path).setReadOnly();
    }

    public void enableJustWritePermission(final String path, final Boolean justOwn) throws IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        if (justOwn) {
            new File(path).setWritable(Boolean.TRUE, Boolean.TRUE);
        } else {
            new File(path).setWritable(Boolean.TRUE);
        }
    }

    private static String getName(final Integer fileNumber, final String path, final String directoryWhereCreate, final String fileName) {

        String diretorio;
        String nomeArquivo;

        if (fileName != null
                && !fileName.trim().isEmpty()) {
            nomeArquivo = fileName;
        } else {
            return null;
        }

        if (directoryWhereCreate != null && !directoryWhereCreate.trim().isEmpty()) {
            diretorio = directoryWhereCreate;
            createDirectoryIfNotExists(diretorio);
        } else {
            return null;
        }

        final String extencao = getExtension(path);

        final String caminhoNovoArquivo = diretorio + File.separator + nomeArquivo
                + "_" + fileNumber + extencao;

        return caminhoNovoArquivo;
    }

    private static File create(final Integer fileNumber, final String path, final String directoryWhereCreate,
            final String fileName) throws IOException {

        final String caminhoNovoArquivo = FileUtils.getName(fileNumber,
                path, directoryWhereCreate, fileName);

        createIfNotExists(caminhoNovoArquivo);

        return new File(path);
    }

    public static String getName(final String path, final Boolean returnWithExtension)
            throws IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        String fileName = new File(path).getName();
        if (path.endsWith(".csv")) {
            if (returnWithExtension) {
                return fileName;
            }
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        if (returnWithExtension) {
            return fileName + ".csv";
        }
        return fileName;
    }

    public static String getExtension(final String path) throws IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        final Integer ultimaPosicao = path.lastIndexOf(".");
        if (ultimaPosicao <= 0) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        return path.substring(ultimaPosicao, path.length());
    }

    public static String getDirectory(final String path) throws IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        final Integer ultimaPosicao = path.lastIndexOf(File.separator);
        if (ultimaPosicao <= 0) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        return path.substring(0, ultimaPosicao);
    }

    public static void del(final String path) {

        final File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    public static void delInDirectory(final String path) throws IllegalArgumentException, IOException {

        if (!isDirectory(path)) {
            final String msg = "Diretório inválido.";
            throw new IllegalArgumentException(msg);
        }

        final File file = new File(path);
        final File[] files = file.listFiles();

        if (files != null && files.length != 0) {
            for (final File arquivos : files) {
                if (arquivos.isDirectory()) {
                    delInDirectory(arquivos.getCanonicalPath());
                } else {
                    del(arquivos.getCanonicalPath());
                }
                del(arquivos.getCanonicalPath());
            }
        }
    }

    public static void delDirectoryIfExists(final String directory) {

        if (!isDirectory(directory)) {
            return;
        }
        new File(directory).delete();
    }

    private static Boolean isExists(final String path) {
        return new File(path).exists();
    }

    public static Boolean isFile(final String path) {

        final Boolean isFile = new File(path).isFile();
        if (isFile) {
            final Boolean existFile = isExists(path);
            return isFile && existFile;
        }
        return Boolean.FALSE;
    }

    public static LinkedList<String> listLines(final String path, final Integer qtd, final Boolean readAllData)
            throws FileNotFoundException, IOException, IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }

        LinkedList<String> lines;

        try (FileReader fr = new FileReader(path)) {
            try (BufferedReader buffer = new BufferedReader(fr)) {
                try {
                    lines = new LinkedList<>();
                    String valor;
                    if (readAllData) {
                        while ((valor = buffer.readLine()) != null) {
                            lines.add(valor);
                        }
                    } else {
                        Integer qtdeLinhasLidas = 0;
                        while ((valor = buffer.readLine()) != null && qtdeLinhasLidas++ < qtd) {
                            lines.add(valor);
                        }
                    }
                } finally {
                    buffer.close();
                }
            } finally {
                fr.close();
            }
        }
        return lines;
    }

    public static String getData(final String path, final String encoding, final Boolean delSpecialCharacter)
            throws IllegalArgumentException, IOException {

        String data;
        if (encoding != null && !encoding.trim().isEmpty()) {
            data = new String(getBytes(path), encoding);
        } else {
            data = new String(getBytes(path));
        }
        if (delSpecialCharacter) {
            return Normalizer.normalize(data, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        } else {
            return data;
        }
    }

    public static void updateData(final String path, final String sourceData, final String dataToReplaced,
            final Integer offset) throws FileNotFoundException, IOException {

        try (RandomAccessFile raf = new RandomAccessFile(new File(path), "rw")) {
            final byte[] bytes;
            if (sourceData.length() > dataToReplaced.length()) {
                bytes = new byte[sourceData.length()];
            } else {
                bytes = new byte[dataToReplaced.length()];
            }
            System.arraycopy(dataToReplaced.getBytes(), 0, bytes, 0, dataToReplaced.length());
            raf.write(bytes, offset, bytes.length);
            raf.close();
        }
    }

    public static void renameColumns(final String path, final String sourceData,
            final String dataToReplaced) throws FileNotFoundException, IOException {

        try (final RandomAccessFile file = new RandomAccessFile(path, "rws")) {

            final byte[] text = new byte[(int) file.length()];
            file.readFully(text);
            final byte[] content = Arrays.copyOfRange(text, sourceData.length(), text.length);

            try (FileOutputStream fout = new FileOutputStream(path + "_novo.csv")) {
                fout.write(dataToReplaced.getBytes());
                fout.write(content);
                fout.flush();
                fout.close();
            }
            file.close();
        }
        final String nomeArquivo = getName(path, Boolean.TRUE);
        del(path);
        renameIfExists(path + "_novo.csv", nomeArquivo);
    }

    public static void saveData(final List<String> listLines, final String path, final Boolean delExistentHeader)
            throws IOException, IllegalArgumentException {

        if (!isFile(path)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        if (listLines.isEmpty()) {
            return;
        }
        final File file = new File(path);
        Boolean startAtEnd = Boolean.FALSE;
        if (file.length() != 0) {
            startAtEnd = Boolean.TRUE;
        }
        try (FileWriter fileWriter = new FileWriter(path, startAtEnd)) {
            try (BufferedWriter strW = new BufferedWriter(fileWriter)) {
                try {
                    if (delExistentHeader && file.length() != 0) {
                        listLines.remove(0);
                    }
                    Integer contador = 0;
                    for (final String valor : listLines) {
                        if (startAtEnd) {
                            strW.newLine();
                        }
                        strW.write(Normalizer.normalize(valor, Normalizer.Form.NFD)
                                .replaceAll("[^\\p{ASCII}]", "").replaceAll("\n", ""));
                        if (!startAtEnd
                                && (contador++ < listLines.size() - 1)) {
                            strW.newLine();
                        }
                    }
                } finally {
                    strW.flush();
                    strW.close();
                }
            } finally {
                fileWriter.flush();
                fileWriter.close();
            }
        }
    }

    public static Long getLenght(final String file) throws IllegalArgumentException {

        if (!isFile(file)) {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
        return new File(file).length();
    }

    public static Boolean isDirectory(final String directory) {
        return new File(directory).isDirectory();
    }

    public static void createIfNotExists(final String path) throws IOException {

        final String directory = getDirectory(path);
        createDirectoryIfNotExists(directory);
        new File(path).createNewFile();
    }

    public static void createDirectoryIfNotExists(final String directory) {
        if (!isDirectory(directory)) {
            new File(directory).mkdirs();
        }
    }

    public static File[] list(final String directory) throws IllegalArgumentException {

        if (!isDirectory(directory)) {
            final String msg = "Diretório inválido.";
            throw new IllegalArgumentException(msg);
        }
        return new File(directory).listFiles();
    }

    public static String[] listNames(final String directory) throws IllegalArgumentException {

        if (!isDirectory(directory)) {
            final String msg = "Diretório inválido.";
            throw new IllegalArgumentException(msg);
        }
        final File file = new File(directory);
        final FilenameFilter filter = (File file1, String string) -> !isDirectory(file1.getAbsolutePath() + File.separator + string);
        return file.list(filter);
    }

    public static byte[] getBytes(final String directory) throws IOException, IllegalArgumentException {

        final Path path = Paths.get(directory);
        if (path.toFile().exists() && path.toFile().isFile()) {
            return Files.readAllBytes(path);
        } else {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
    }

    public static List<String> listLines(final String directory) throws IOException, IllegalArgumentException {

        final Path path = Paths.get(directory);
        if (path.toFile().exists() && path.toFile().isFile()) {
            try (Stream<String> stream = Files.lines(path, StandardCharsets.ISO_8859_1)) {
                final List<String> lines = stream.collect(Collectors.toList());
                return lines;
            }
        } else {
            final String msg = "Arquivo inválido.";
            throw new IllegalArgumentException(msg);
        }
    }

    public static String getFirstLine(final String path) throws IOException {

        final Path p = Paths.get(path);
        if (p.toFile().exists() && p.toFile().isFile()) {
            try (Stream<String> stream = Files.lines(p, StandardCharsets.ISO_8859_1)) {
                Optional<String> optional = stream.findFirst();
                if (optional.isPresent()) {
                    final String line = optional.get();
                    return line;
                } else {
                    return "";
                }
            }
        } else {
            return null;
        }
    }

    public static String getDocRootDirectory() {
        try {
            return (new File("../docroot")).getCanonicalPath();
        } catch (IOException ex) {
            return null;
        }
    }
}
