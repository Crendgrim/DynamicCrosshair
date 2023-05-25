package mod.crend.yaclx.opt;

import com.google.gson.JsonObject;

public interface ConfigUpdater {
	boolean updateConfigFile(JsonObject json);
}
