import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CommentCount {
    private static final String helpMessage = "  Usage: java CommentCount -file [filename1 filename2 ...]";
    private static final Map<String, Extension> files = new HashMap<>();
    private static final Map<Extension, Map<CommentType, String>> mappings = new HashMap<>();

    private static void initMapping() {
        Map<CommentType, String> javaMapping = new HashMap<>();
        javaMapping.put(CommentType.singleLine, "//");
        javaMapping.put(CommentType.blockStart, "/*");
        javaMapping.put(CommentType.blockEnd, "*/");
        mappings.put(Extension.java, javaMapping);
        mappings.put(Extension.ts, javaMapping);

        Map<CommentType, String> pythonMapping = new HashMap<>();
        pythonMapping.put(CommentType.singleLine, "#");
        pythonMapping.put(CommentType.blockStart, "'''");
        pythonMapping.put(CommentType.blockEnd, "'''");
        mappings.put(Extension.py, pythonMapping);
    }

    private static boolean containsInlineBlockComment(String line, Map<CommentType, String> dictionary, Extension fileExtension) {
        if (fileExtension.equals(Extension.py)) {
            int startIdx = line.indexOf(dictionary.get(CommentType.blockStart));
            int endIdx = line.indexOf(dictionary.get(CommentType.blockEnd), startIdx + 3);
            return (endIdx != -1);
        } else {
            return line.endsWith(dictionary.get(CommentType.blockEnd));
        }
    }

    private static void parseFiles(Map<String, Extension> files) {
        for (Map.Entry<String, Extension> file : files.entrySet()) {
            String fileName = file.getKey();
            Extension fileExtension = file.getValue();
            int lineTotal = 0;
            int singleLineComment = 0;
            int blockComment = 0;
            int blockCount = 0;
            int todoCount = 0;

            try {
                // wrap FileReader in a BufferedReader for IO
                FileReader reader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(reader);

                // loop until EOF and print each line
                String line;

                Map<CommentType, String> dictionary = mappings.get(fileExtension);
                String singleLine = dictionary.get(CommentType.singleLine);
                String blockStart = dictionary.get(CommentType.blockStart);
                String blockEnd = dictionary.get(CommentType.blockEnd);

                while ((line = bufferedReader.readLine()) != null) {
                    lineTotal++;
                    line = line.trim();
                    // case: single line comment
                    if (line.contains(singleLine)) {
                        if (line.contains("TODO")) todoCount++;
                        singleLineComment++;
                    }

                    // case: block comment
                    if (line.contains(blockStart)) {
                        if (line.contains("TODO")) todoCount++;
                        blockCount++;
                        blockComment++;

                        // case: inline block comment
                        if (containsInlineBlockComment(line, dictionary, fileExtension)) {
                            continue;
                        }

                        while (!(line = bufferedReader.readLine()).contains(blockEnd)) {
                            if (line.contains("TODO")) todoCount++;
                            lineTotal++;
                            blockComment++;
                        }
                        lineTotal++;
                        blockComment++;
                    }
                }

                // close when complete
                bufferedReader.close();

                System.out.println("Comment count for file: " + fileName);
                System.out.println("Total # of lines: " + lineTotal);
                System.out.println("Total # of comment lines: " + (singleLineComment + blockComment));
                System.out.println("Total # of single line comments: " + singleLineComment);
                System.out.println("Total # of comment lines within block comments: " + blockComment);
                System.out.println("Total # of block line comments: " + blockCount);
                System.out.println("Total # of TODO’s: " + todoCount);
                System.out.println();

            } catch (FileNotFoundException ex) {
                System.out.println("Error reading file: " + ex.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(helpMessage);
            System.exit(1);
        }

        String option = args[0].substring(1);
        if (option.equals("help")) {
            System.out.println(helpMessage);
            System.exit(0);
        }

        if (!option.equals("file")) {
            System.out.println("Error: Invalid option: " + option);
        }

        for (int i = 1; i < args.length; i++) {
            String fileName = args[i];
            int idx = fileName.lastIndexOf('.');
            if (idx == 0) {
                System.out.println("Error: Invalid file name: " + fileName);
            } else if (idx > 0) {
                String extension = fileName.substring(idx + 1);
                if (!(extension.equals("java") || extension.equals("py") || extension.equals("ts"))) {
                    System.out.println("Error: File extension is not supported by program for file: " + fileName);
                } else {
                    files.put(fileName, Extension.valueOf(extension));
                }
            } else {
                System.out.println("Error: No file extension found for file: " + fileName);
            }
        }

        initMapping();
        parseFiles(files);
        System.exit(0);
    }
}
