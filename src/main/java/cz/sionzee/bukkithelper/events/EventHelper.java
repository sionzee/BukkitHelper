package cz.sionzee.bukkithelper.events;

import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class EventHelper {

    private static final Pattern COMPILE = Pattern.compile("/", Pattern.LITERAL);

    /**
     * This will register all listeners what are inside jar.
     */
    public static void RegisterAllListeners(String packageName, Consumer<Class<? extends Listener>> consumer) {
        try {
            JarFile jar = GetJarFile();
            Stream<JarEntry> stream = GetJarEntriesStream(jar);
            stream.filter(entry -> !entry.isDirectory() && entry.getName().endsWith(".class") && entry.getName().startsWith(packageName)).map(entry -> {
               String entryName = COMPILE.matcher(entry.getName()).replaceAll(Matcher.quoteReplacement("."));
               return entryName.substring(0, entryName.length() - ".class".length());
            }).map(className -> {
                try {
                    return Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }).filter(Listener.class::isAssignableFrom).forEach(listenerClass -> {
                consumer.accept((Class<? extends Listener>) listenerClass);
            });
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    static JarFile GetJarFile() throws IOException, URISyntaxException {
        return new JarFile(new File(EventHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
    }

    static Stream<JarEntry> GetJarEntriesStream(JarFile file) {
        return Collections.list(file.entries()).stream();
    }

}
