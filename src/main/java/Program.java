import classes.Coordinates;
import classes.MusicBand;
import classes.MusicGenre;
import classes.Studio;
import commands.*;
import utils.CommandMap;
import utils.Console;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Главный класс приложения для управления коллекцией музыкальных групп.
 * <p>
 * Содержит точку входа {@link #main(String[])} и запускает интерактивный режим.
 *
 * @see classes Основная сущность приложения
 * @see commands Исполняемые команды
 */
public class Program {
    /**
     * Точка входа в программу.
     * @param args список строк - путей к файлам.
     */
    public static void main(String[] args){
        if (args.length < 1){
            System.err.println("Error: expected at least 1 argument: 'file_csv' and optional 'file_script1 file_script2 ...'");
            System.exit(1);
        }

        fileIsReadableAndWriteable(args[0]);
        File file_csv = new File(args[0]);

        List<File> scriptFiles = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            fileIsReadable(args[i]);
            scriptFiles.add(new File(args[i]));
        }

        MusicBand band = new MusicBand("Name", new Coordinates(2.765, 2), 2, "description", MusicGenre.POST_ROCK, new Studio("studio name"));

        TreeMap<Long, MusicBand> musicBands = new TreeMap<>();
        musicBands.put(2L, band);

        Executor executor = new Executor(file_csv, scriptFiles);

        Map<String, Command> commands = CommandMap.createMapWithCommands(executor);

        Command command;
        CommandWithArgument commandWithArgument;

        Console console = new Console();

        System.out.println("To see a list of possible commands, enter \"help\"");
        while(true){
            Console.CommandInput input;

            input = console.readCommand();

            if(input == null){
                System.out.println("Input is complete");
                commands.get("exit").execute();
            }

            if(!Console.isValidCommand(input.command)){
                System.out.printf("There is no command '%s'\n", input.command);
                continue;
            }

            command = commands.get(input.command);
            if(!Console.isCommandWithArgument(command)){
                command.execute();
            }else{
                try {
                    commandWithArgument = (CommandWithArgument) command;
                    commandWithArgument.setArgument(input.argument);
                    commandWithArgument.execute();
                }catch(IllegalArgumentException e){
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
    }

    public static boolean isValidFilename(String path) {
        File file = new File(path);
        try {
            file.getCanonicalPath();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static void fileIsReadableAndWriteable(String path){
        if(!isValidFilename(path)){
            System.err.printf("Invalid filename '%s'\n", path);
            System.exit(1);
        }
        File file = new File(path);
        if(!file.exists()){
            System.err.printf("There is no file '%s'\n", file);
            System.exit(1);
        }
        if(!file.canWrite()){
            System.err.printf("The file '%s' is not writeable\n", file);
            System.exit(1);
        }
        if(!file.canRead()){
            System.err.printf("The file '%s' is not readable\n", file);
            System.exit(1);
        }
    }
    public static void fileIsReadable(String path){
        if(!isValidFilename(path)){
            System.err.printf("Invalid filename '%s'\n", path);
            System.exit(1);
        }
        File file = new File(path);
        if(!file.exists()){
            System.err.printf("There is no file '%s'\n", file);
            System.exit(1);
        }
        if(!file.canRead()){
            System.err.printf("The file '%s' is not readable\n", file);
            System.exit(1);
        }
    }
}
