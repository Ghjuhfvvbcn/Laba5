package commands;

import java.io.File;
import java.io.IOException;

/**
 * Команда выполняет скрипт из указанного файла.
 * <p>
 * Реализует интерфейс {@link CommandWithArgument}
 * @see CommandWithArgument
 * @see Executor
 */
public class Execute_script implements CommandWithArgument<File> {
    /**
     * Хранит имя команды (в данном случае "execute_script").
     */
    private final String commandName = "execute_script";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;
    /**
     * Хранит аргумент команды - файл скрипта.
     */
    private File scriptFile;

    /**
     * Создает объект {@link Execute_script} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Execute_script(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Проверяет рекурсию и вызывает метод {@link Executor#execute_script(File)} у объекта executor.
     */
    @Override
    public void execute(){
        if (scriptFile == null) {
            System.err.println("Error: no script file specified");
            return;
        }

        // Проверяем рекурсию здесь, а не в setArgument
        try {
            File canonicalFile = scriptFile.getCanonicalFile();
            if (executor.isScriptExecuting(canonicalFile)) {
                System.out.println("Warning: recursive call to '" + scriptFile.getName() + "' ignored");
                return; // Просто игнорируем, не вызываем executor.execute_script()
            }
        } catch (IOException e) {
            System.err.println("Error checking script file: " + e.getMessage());
            return;
        }

        executor.execute_script(scriptFile);
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link CommandWithArgument#setArgument(String)}.
     * Устанавливает аргумент команды - файл скрипта.
     * @param argument имя файла скрипта в виде строки
     * @throws IllegalArgumentException если файл не существует или недоступен для чтения
     */
    @Override
    public void setArgument(String argument) {
        if (argument == null || argument.trim().isEmpty()) {
            throw new IllegalArgumentException("Script file name cannot be null or empty");
        }

        scriptFile = new File(argument.trim());

        if (!scriptFile.exists()) {
            throw new IllegalArgumentException("Script file '" + argument + "' does not exist");
        }
        if (!scriptFile.canRead()) {
            throw new IllegalArgumentException("Script file '" + argument + "' is not readable");
        }
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link CommandWithArgument#getArgument()}.
     * @return текущий аргумент команды (файл скрипта)
     */
    @Override
    public File getArgument() {
        return scriptFile;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "execute_script")
     */
    @Override
    public String getCommandName(){return commandName;}
}