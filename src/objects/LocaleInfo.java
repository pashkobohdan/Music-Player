package objects;

import controllers.Controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocaleInfo {
    public static final String LOCALE_PATH = Controller.PROJECT_PATH + "/src/bundles";
    public static final String MAIN_LANGUAGE_PATH = Controller.PROJECT_PATH + "/src/language/mainLanguage.txt";

    public static int countLanguages() {
        return new File(LOCALE_PATH).listFiles().length;
    }

    public static List<String> languagesList() {
        return Arrays.stream(new File(LOCALE_PATH).listFiles())
                .filter((a) -> a.getName().contains(".properties"))
                .map((a) -> a.getName().substring(a.getName().indexOf("_") + 1, a.getName().lastIndexOf(".")))
                .collect(Collectors.toList());
    }

    public static String readMainLanguage() {

        int length = (int) (new File(MAIN_LANGUAGE_PATH).length());
        try (FileInputStream fileInputStream = new FileInputStream(MAIN_LANGUAGE_PATH)) {
            byte[] bytes = new byte[length];
            fileInputStream.read(bytes);

            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeMainLanguage(String currentLanguage) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(MAIN_LANGUAGE_PATH)) {
            fileOutputStream.write(currentLanguage.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
