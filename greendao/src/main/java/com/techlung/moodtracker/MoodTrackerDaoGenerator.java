package com.techlung.moodtracker;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MoodTrackerDaoGenerator {

    public static final String TEMP_NAMESPACE = "com.techlung.moodtracker.greendao.generated";
    public static final String TEMP_PATH = "com/techlung/moodtracker/greendao/generated/";
    public static final String TEMP_ROOT = "com";
    public static final String TARGET_PATH = "app/src/main/java/com/techlung/moodtracker/greendao/generated/";

    public static final int DATABASE_VERSION = 8;

    public static void main(String[] args) throws Exception {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        new File(TEMP_PATH).mkdirs();
        new File(TARGET_PATH).mkdirs();
        System.out.println("##################  copy files from src ##################");
        moveFiles(TARGET_PATH, TEMP_PATH);
        File f = new File(TARGET_PATH);
        deleteDir(f);
        new File(TARGET_PATH).mkdirs();
        System.out.println("##################  Generating DAO's ##################");
        generateDaos();
        System.out.println("##################  copy files to src ##################");
        moveFiles(TEMP_PATH, TARGET_PATH);
        System.out.println("##################       clean        ##################");
        deleteDir(new File(TEMP_ROOT));
        System.out.println("##################       done!        ##################");
    }

    private static void generateDaos() throws Exception {
        Schema schema = new Schema(DATABASE_VERSION, TEMP_NAMESPACE);

        Entity moodScope = createMoodScopeEntity(schema);

        Entity moodRating = createMoodRatingEntity(schema, moodScope);

        Entity logEntry = createLogEntryEntity(schema);

        new DaoGenerator().generateAll(schema, ".");
    }

    private static Entity createMoodScopeEntity(Schema schema) {
        Entity ms = schema.addEntity("MoodScope");
        ms.setHasKeepSections(true);

        ms.addLongProperty("id").primaryKey().autoincrement();
        ms.addStringProperty("name").unique();
        ms.addIntProperty("sequence");

        return ms;
    }

    private static Entity createMoodRatingEntity(Schema schema, Entity moodScope) {
        Entity mr = schema.addEntity("MoodRating");
        mr.setHasKeepSections(true);

        mr.addLongProperty("id").primaryKey().autoincrement();
        mr.addIntProperty("rating");
        Property scope = mr.addLongProperty("scope").getProperty();

        mr.addDateProperty("day");
        mr.addLongProperty("timestamp");

        mr.addToOne(moodScope, scope);
        mr.addStringProperty("name").unique();

        return mr;
    }

    private static Entity createLogEntryEntity(Schema schema) {
        Entity le = schema.addEntity("LogEntry");

        le.addLongProperty("id").primaryKey().autoincrement();

        le.addStringProperty("text");
        le.addStringProperty("category");

        le.addDateProperty("day");
        le.addLongProperty("timestamp");

        return le;
    }

    // Utilities
    private static void moveFiles(String fromPath, String toPath) {
        File fromDirectory = new File(fromPath);

        for (File from : fromDirectory.listFiles()) {
            File to = new File(toPath + from.getName());
            System.out.print("Moving: ");
            System.out.print(from.getAbsolutePath());
            System.out.print("  -->  ");
            System.out.println(to.getAbsolutePath());
            from.renameTo(to);
        }
    }

    private static void deleteDir(File path) {
        for (File file : path.listFiles()) {
            if (file.isDirectory()) {
                deleteDir(file);
            }
            System.out.println("Deleting: " + file.getAbsolutePath());
            file.delete();
        }
        System.out.println("Deleting: " + path.getAbsolutePath());
        path.delete();
    }
}
