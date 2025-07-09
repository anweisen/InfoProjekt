package game.engine.assets;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Sound {

    public static final Sound DUMMY = new Sound(new byte[0], 0f);
    public static final Sound SWOOSH = loadSound("ui", "swoosh.wav", 1f);
    public static final Sound CLICK = loadSound("ui", "click.wav", 0.7f);
    public static final Sound BUY = loadSound("ui", "buy.wav", 0.5f);
    public static final Sound POP = loadSound("ui", "pop.wav", 0.5f);
    public static final Sound MECHANICAL = loadSound("ui", "mechanical2.wav", 0.2f);

    private final byte[] data;
    private final float volume;

    public Sound(byte[] data, float volume) {
        this.data = data;
        this.volume = volume;
    }

    // Sollte nur für kleine Sound Dateien genutzt werden,
    // die immer wieder wiederholt werden (z.B. Schüsse, Klicks)
    public static Sound loadSound(String category, String name, float volume) {
        try {
            InputStream stream = Assets.loadResource("sounds", category, name);
            byte[] data = stream.readAllBytes();
            stream.close();
            return new Sound(data, volume);
        } catch (Exception ex) {
            System.err.println("Error loading sound '" + name + "': " + ex.getMessage());
            ex.printStackTrace();
            return DUMMY;
        }
    }

    // Sollte nur für große Sound Dateien genutzt werden,
    // die einmalig abgespielt werden (z.B. Musik)
    public static Clip loadClip(String category, String name, float volume) {
        InputStream stream = Assets.loadResource("sounds", category, name);
        return loadClip(stream, volume);
    }

    public static Clip loadClip(InputStream stream, float volume) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(stream));

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log10(volume) * 20);
            gainControl.setValue(dB);

            return clip;
        } catch (Exception ex) {
            System.err.println("Error playing sound: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public void playSound() {
        playSound(volume);
    }

    public void playSound(float overwriteVolume) {
        Clip clip = getNewClip(overwriteVolume);
        if (clip != null) clip.start();
    }

    public Clip getNewClip() {
        return getNewClip(volume);
    }

    public Clip getNewClip(float overwriteVolume) {
        return loadClip(new ByteArrayInputStream(data), overwriteVolume);
    }

    public byte[] getData() {
        return data;
    }

    public float getVolume() {
        return volume;
    }
}
