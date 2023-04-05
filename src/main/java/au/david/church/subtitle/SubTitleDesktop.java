package au.david.church.subtitle;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.System.exit;

public class SubTitleDesktop {
    private static final int WIDTH=35;

    public static void main(String[] args) throws IOException {
        System.out.println("SubTile Application");
        File outputFile = new File("outputLine.txt");
        File headFile = new File("head.txt");
        File metaFile = new File("meta.txt");
        System.out.println("Subtitle file loading...");
        List<String> overSizeLine = findOverSizeLine(new File("subTitle.txt"));
        if (!overSizeLine.isEmpty()){
            System.out.println("Line too long found over 30. Please fix");
            overSizeLine.forEach(l-> System.out.println(l));
            exit(1);
        }
        List<SubTitleDto> subtitles = readFile(new File("subTitle.txt"));
        subtitles.stream().forEach((dto)-> System.out.println(dto.toString()));

        Scanner keyboard = new Scanner(System.in);
        int input = 0;
        int maxId = subtitles.stream().map(s->s.getId()).max(Integer::compareTo).get();
        int port = 6868;
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                OutputStream os = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(os, true);
                try {
                    input = Integer.parseInt(keyboard.nextLine());
                } catch (Exception e) {
                    input++;
                    if (input>maxId){
                        input = maxId;
                    }
                }
                final int inputLine = input;
                Optional<SubTitleDto> output = subtitles.stream().filter((s) -> s.getId() == inputLine).findAny();
                if (output.isPresent()) {
                    if (output.get().getSubtitle() != null) {
                        FileUtils.writeStringToFile(outputFile, output.get().getSubtitle(), "UTF-8", true);
                        FileUtils.writeStringToFile(outputFile, "\n", "UTF-8", true);
                        writer.println(output.get().getSubtitle());
                    } else {
                        FileUtils.writeStringToFile(outputFile, "\n", "UTF-8", true);
                    }
                    if (output.get().getHead() != null) {
                        FileUtils.writeStringToFile(headFile, output.get().getHead(), "UTF-8");
                    } else {
                        FileUtils.writeStringToFile(headFile, "", "UTF-8");
                    }
                    if (output.get().getMeta() != null) {
                        FileUtils.writeStringToFile(metaFile, output.get().getMeta(), "UTF-8");
                    } else {
                        FileUtils.writeStringToFile(metaFile, "", "UTF-8");
                    }
                    System.out.println(output.get().toString());
                } else {
                    FileUtils.writeStringToFile(outputFile, "", "UTF-8", true);
                    FileUtils.writeStringToFile(headFile, "", "UTF-8");
                    FileUtils.writeStringToFile(metaFile, "", "UTF-8");
                    System.out.println("");
                }
            }
        }

    }

    private static List<String> findOverSizeLine(File file)throws IOException {
        return  FileUtils.readLines(file,"UTF-8").stream()
                .filter(l-> !l.startsWith("---"))
                .filter(l-> !l.startsWith("***"))
                .filter((l)->l.trim().length()>WIDTH)
                .collect(Collectors.toList());
    }


    private static List<SubTitleDto> readFile(File file) throws IOException {
        return  FileUtils.readLines(file,"UTF-8").stream()
                .filter(l-> !l.startsWith("---"))
                .filter(l-> !l.startsWith("***"))
                .filter((l)->l.trim().length()!=0)
                .map((l)->new SubTitleDto(l,WIDTH)).collect(Collectors.toList());
    }
}
