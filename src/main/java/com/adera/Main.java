package com.adera;

import com.adera.commonTypes.Config;
import com.adera.database.EstablishmentDatabase;
import com.adera.database.UserDatabase;
import com.adera.entities.EstablishmentEntity;
import com.adera.entities.UserEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private static UserEntity user = null;
    private static EstablishmentEntity establishment = null;
    private static boolean logged = false;
    public static void main(String[] args) throws SQLException, FileNotFoundException {
        Logger.logInfo("Iniciando aplicação");

        var config = new Config();

        ArrayList<String> errList = new ArrayList<String>();

        System.out.println("""
                 ______     _____     ______     ______     ______     ______     ______     ______     __   __     __   __     ______     ______   \s
                /\\  __ \\   /\\  __-.  /\\  ___\\   /\\  == \\   /\\  __ \\   /\\  ___\\   /\\  ___\\   /\\  __ \\   /\\ "-.\\ \\   /\\ "-.\\ \\   /\\  ___\\   /\\  == \\  \s
                \\ \\  __ \\  \\ \\ \\/\\ \\ \\ \\  __\\   \\ \\  __<   \\ \\  __ \\  \\ \\___  \\  \\ \\ \\____  \\ \\  __ \\  \\ \\ \\-.  \\  \\ \\ \\-.  \\  \\ \\  __\\   \\ \\  __<  \s
                 \\ \\_\\ \\_\\  \\ \\____-  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\ \\_\\  \\/\\_____\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\\\"\\_\\  \\ \\_\\\\"\\_\\  \\ \\_____\\  \\ \\_\\ \\_\\\s
                  \\/_/\\/_/   \\/____/   \\/_____/   \\/_/ /_/   \\/_/\\/_/   \\/_____/   \\/_____/   \\/_/\\/_/   \\/_/ \\/_/   \\/_/ \\/_/   \\/_____/   \\/_/ /_/\s
                                                                                                                                                    \s""");

        do {
            Config cfg = tryReadCfgFile();

            if(cfg == null) {
                createCfgFile();
                cfg = tryReadCfgFile();
            }

            if(errList.contains("notfound")) {
                System.err.println("\nEmail ou Senha inválidos\n");
            }

            errList.clear();

            assert cfg != null;
            if(user == null && cfg.getUserId() != null) {
                user = UserDatabase.getOneById(cfg.getUserId());
                if (user != null) {
                    writeToCfgFile(user.getId().toString());
                    establishment = EstablishmentDatabase.getOneById(user.getEstablishmentId().toString());
                    config.setEstablishmentId(establishment.getId());
                    logged = true;
                }
            } else {
                user = requestEmailAndPassword();

                if(user == null) {
                    errList.add("notfound");
                    System.out.println("\n\nEmail ou senha inválidos\n\n");
                } else {
                    writeToCfgFile(user.getId().toString());
                    establishment = EstablishmentDatabase.getOneById(user.getEstablishmentId().toString());
                    config.setEstablishmentId(establishment.getId());
                    logged = true;
                }
            }

        } while (!logged);

        var monitor = new Monitor(config);

        Runnable monitorLoop = monitor::insertMetrics;

        var monitorScheduler = Executors.newScheduledThreadPool(1);
        monitorScheduler.scheduleAtFixedRate(monitorLoop, 0, 2, TimeUnit.SECONDS);

        Runnable commandLoop = () -> {
            var listener = new CommandListener(config.getEstablishmentId(), monitor.getMachine());
            listener.fetchCommands();
            listener.runCommands();
            listener.watch();
        };

        var commandScheduler = Executors.newScheduledThreadPool(1);
        commandScheduler.scheduleAtFixedRate(commandLoop, 0, 10, TimeUnit.SECONDS);
    }

    public static UserEntity requestEmailAndPassword() throws SQLException {
        Scanner in = new Scanner(System.in);
        System.out.println("Email:");
        String email = in.next();

        System.out.println("Senha:");
        String password = in.next();

        user = UserDatabase.getOneByEmailAndPassword(email, password);
        return user;
    }

    public static Config tryReadCfgFile() throws FileNotFoundException {
        try {
            File cfgFile = new File("config.txt");
            Scanner myReader = new Scanner(cfgFile);
            Config cfg = new Config();
            while (myReader.hasNextLine()) {
                cfg.setUserId(myReader.nextLine());
            }
            return cfg;
        } catch(FileNotFoundException e) {
            return null;
        }
    }

    public static void createCfgFile() {
        try {
            File myObj = new File("config.txt");
            myObj.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void writeToCfgFile(String userId) {
        try {
            FileWriter myWriter = new FileWriter("config.txt");
            myWriter.write(userId);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}