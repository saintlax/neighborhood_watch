package widgets;

import android.view.View;

import java.util.Hashtable;

import Objects.Message;

public interface MessageViewHashtableCallback {
    public abstract void complete(Hashtable<Message, View> hashtable);
}
