package server;

import org.json.JSONObject;

/**
 * Created by user on 8/28/2018.
 */

public interface GetJSONCallback {
    public abstract void done(JSONObject returnedJSON);
}
