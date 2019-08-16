package downloader;

import Objects.Data;

public interface GetUserCallback {

    /**
     * Invoked when background task is completed
     */

    public abstract void done(Data returnedData);
}