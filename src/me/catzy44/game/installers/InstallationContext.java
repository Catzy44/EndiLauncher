package me.catzy44.game.installers;

import java.io.File;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.function.BiPredicate;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.catzy44.game.GameInstallation;
import me.catzy44.game.GameInstallationManager;
import me.catzy44.game.GameInstallation.Status;
import me.catzy44.struct.GameVersion;
import me.catzy44.tools.Download;
import me.catzy44.tools.Unzip;

/** Shared state for one installation run; progress uses the launcher's existing observers. */
public final class InstallationContext {
    final GameInstallation installation;
    final GameVersion ver;
    final File gameDir, libsDir, assetsDir, natDir;
    final Gson gson = new Gson();
    final PropertyChangeSupport pcs;
    final String osName = GameInstallationManager.osName;
    private final BiPredicate<JsonObject, Map<String, Boolean>> rules;
    public JsonObject manifest;
    public String classpath = "";
    volatile Download down;
    Unzip unzip;
    Status status = Status.INITIATED;

    public InstallationContext(GameInstallation installation, JsonObject manifest,
            BiPredicate<JsonObject, Map<String, Boolean>> rules) {
        this.installation = installation;
        this.manifest = manifest;
        this.rules = rules;
        ver = installation.getVersion();
        gameDir = installation.getGameDirectory();
        libsDir = new File(gameDir, "libraries");
        assetsDir = new File(gameDir, "assets");
        natDir = new File(gameDir, "natives");
        pcs = installation.pcs;
    }

    public Download getDownloader() { return down; }

    public void cancel() {
        Download current = down;
        if (current != null) current.cancel();
    }

    void reloadManifest() throws Exception {
        installation.loadInstalledVersionManifest();
        manifest = installation.getVersionManifest();
    }

    void setStatus(Status next) {
        Status previous = status;
        status = next;
        pcs.firePropertyChange("status", previous, next);
    }

    boolean shouldIncludeArgument(JsonObject entry, Map<String, Boolean> features) {
        return rules.test(entry, features);
    }

    void completedArtifact(int done, int total) {
        pcs.firePropertyChange("completedArtifact", null, new int[] {done, total});
    }

    void processingArtifact(String filename) {
        String[] parts = filename.split("/");
        pcs.firePropertyChange("processingArtifact", null, parts[parts.length - 1]);
    }
}
