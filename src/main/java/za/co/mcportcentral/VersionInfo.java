package za.co.mcportcentral;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

public class VersionInfo {
    public static final VersionInfo INSTANCE = new VersionInfo();
    public final JsonElement versionData;

    public VersionInfo()
    {
        InputStream installProfile = getClass().getResourceAsStream("/install_profile.json");
        JsonParser parser = new JsonParser();

        try
        {
            versionData = parser.parse(new InputStreamReader(installProfile, Charsets.UTF_8));
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    public static String getProfileName()
    {
        JsonObject jsonobject = INSTANCE.versionData.getAsJsonObject();
        return jsonobject.get("install").getAsJsonObject().get("profileName").getAsString();
    }

    public static String getVersionTarget()
    {
        JsonObject jsonobject = INSTANCE.versionData.getAsJsonObject();
        return jsonobject.get("install").getAsJsonObject().get("target").getAsString();
    }
    public static File getLibraryPath(File root)
    {
        JsonObject jsonobject = INSTANCE.versionData.getAsJsonObject();
        String path = jsonobject.get("install").getAsJsonObject().get("path").getAsString();
        String[] split = Iterables.toArray(Splitter.on(':').omitEmptyStrings().split(path), String.class);
        File dest = root;
        Iterable<String> subSplit = Splitter.on('.').omitEmptyStrings().split(split[0]);
        for (String part : subSplit)
        {
            dest = new File(dest, part);
        }
        dest = new File(new File(dest, split[1]), split[2]);
        String fileName = split[1]+"-"+split[2]+".jar";
        return new File(dest,fileName);
    }

    public static String getVersion()
    {
        JsonObject jsonobject = INSTANCE.versionData.getAsJsonObject();
        return jsonobject.get("install").getAsJsonObject().get("version").getAsString();
    }

    public static String getWelcomeMessage()
    {
        JsonObject jsonobject = INSTANCE.versionData.getAsJsonObject();
        return jsonobject.get("install").getAsJsonObject().get("welcome").getAsString();
    }

    public static String getLogoFileName()
    {
        JsonObject jsonobject = INSTANCE.versionData.getAsJsonObject();
        return jsonobject.get("install").getAsJsonObject().get("logo").getAsString();
    }

    public static File getMinecraftFile(File path)
    {
        return new File(new File(path, getMinecraftVersion()),getMinecraftVersion()+".jar");
    }
    public static String getContainedFile()
    {
        JsonObject jsonobject = INSTANCE.versionData.getAsJsonObject();
        return jsonobject.get("install").getAsJsonObject().get("filePath").getAsString();
    }
    public static void extractFile(File path) throws IOException
    {
        INSTANCE.doFileExtract(path);
    }

    private void doFileExtract(File path) throws IOException
    {
        InputStream inputStream = getClass().getResourceAsStream("/"+getContainedFile());
        OutputSupplier<FileOutputStream> outputSupplier = Files.newOutputStreamSupplier(path);
        System.out.println("doFileExtract path = " + path.getAbsolutePath() + ", inputStream = " + inputStream + ", outputSupplier = " + outputSupplier);
        ByteStreams.copy(inputStream, outputSupplier);
    }

    public static String getMinecraftVersion()
    {
        JsonObject jsonobject = INSTANCE.versionData.getAsJsonObject();
        return jsonobject.get("install").getAsJsonObject().get("minecraft").getAsString();
    }
}