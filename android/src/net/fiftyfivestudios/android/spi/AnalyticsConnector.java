package net.fiftyfivestudios.android.spi;

/**
 * Created by Eduardo Angeleri on 01/20/19.
 */

public interface AnalyticsConnector {

    void sendEvent(final String eventName);
    void sendLevelWon(final String world, final String level, final String stars);
    void sendLevelLost(final Integer world, final Integer level);
    void sendLiveWon(final Integer quantity, final String medium);
    void sendLiveLost(final Integer world, final Integer level);
    void sendPwupUsed(final Integer world, final Integer level, final String item_name);
    void sendPwupObtained(final String item_name, final Integer quantity, final String medium);
    void sendWorldUnlocked(final Integer world);
    void sendLevelSelected(final Integer world, final Integer level);
    void sendWorldSelected(final Integer world);
    void sendTutorialCompleted(final String name_tutorial);
}
