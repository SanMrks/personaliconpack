package com.ravenshade.iconpack;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class RavenRingtoneManager {

    public static class ToneInfo {
        public final int id;
        public final String title;
        public final String category;
        public final String description;
        public final double baseFreq;

        public ToneInfo(int id, String title, String category, String description, double baseFreq) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.description = description;
            this.baseFreq = baseFreq;
        }
    }

    public static final ToneInfo[] BUILTIN_TONES = new ToneInfo[] {
        new ToneInfo(0, "Raven Crimson Bell", "RINGTONE", "Futuristic crimson resonance chime", 587.33), // D5
        new ToneInfo(1, "Obsidian Resonance", "RINGTONE", "Crystalline acoustic resonant tone", 440.0), // A4
        new ToneInfo(2, "Cyber Raven Horizon", "ALARM", "Futuristic pulsating synth arpeggio", 659.25), // E5
        new ToneInfo(3, "Crisp Droplet", "NOTIFICATION", "Delicate acoustic chime tone", 880.0) // A5
    };

    private static MediaPlayer sMediaPlayer = null;
    private static int sPlayingIndex = -1;

    public static void playTone(final Context context, final int index, final Runnable onStateChange) {
        stopTone();
        if (index < 0 || index >= BUILTIN_TONES.length) return;

        try {
            File toneFile = ensureToneFile(context, BUILTIN_TONES[index]);
            if (toneFile == null || !toneFile.exists()) return;

            sMediaPlayer = new MediaPlayer();
            sMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            sMediaPlayer.setDataSource(toneFile.getAbsolutePath());
            sMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopTone();
                    if (onStateChange != null) onStateChange.run();
                }
            });
            sMediaPlayer.prepare();
            sMediaPlayer.start();
            sPlayingIndex = index;
            if (onStateChange != null) onStateChange.run();
        } catch (Exception e) {
            e.printStackTrace();
            stopTone();
            if (onStateChange != null) onStateChange.run();
        }
    }

    public static void stopTone() {
        if (sMediaPlayer != null) {
            try {
                if (sMediaPlayer.isPlaying()) {
                    sMediaPlayer.stop();
                }
                sMediaPlayer.release();
            } catch (Exception ignored) {}
            sMediaPlayer = null;
        }
        sPlayingIndex = -1;
    }

    public static boolean isPlaying(int index) {
        return sPlayingIndex == index && sMediaPlayer != null && sMediaPlayer.isPlaying();
    }

    public static boolean applyTone(Context context, int index, int ringtoneType) {
        if (index < 0 || index >= BUILTIN_TONES.length) return false;
        ToneInfo tone = BUILTIN_TONES[index];
        File toneFile = ensureToneFile(context, tone);
        if (toneFile == null) return false;

        return applyAudioFileAsRingtone(context, toneFile, tone.title, ringtoneType);
    }

    public static boolean applyCustomAudioUri(Context context, Uri uri, String customTitle, int ringtoneType) {
        try {
            File dest = new File(getTonesDir(context), "custom_" + System.currentTimeMillis() + ".wav");
            InputStream is = context.getContentResolver().openInputStream(uri);
            OutputStream os = new FileOutputStream(dest);
            byte[] buf = new byte[8192];
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            os.close();
            is.close();

            return applyAudioFileAsRingtone(context, dest, customTitle != null ? customTitle : "Raven Custom Tune", ringtoneType);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean applyAudioFileAsRingtone(Context context, File file, String title, int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                return false; // Caller will launch Settings.ACTION_MANAGE_WRITE_SETTINGS
            }
        }

        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, title);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav");
            values.put(MediaStore.MediaColumns.SIZE, file.length());
            values.put(MediaStore.Audio.Media.ARTIST, "Raven Shade");
            values.put(MediaStore.Audio.Media.IS_RINGTONE, (type == RingtoneManager.TYPE_RINGTONE));
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, (type == RingtoneManager.TYPE_NOTIFICATION));
            values.put(MediaStore.Audio.Media.IS_ALARM, (type == RingtoneManager.TYPE_ALARM));
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
            Uri newUri = null;
            try {
                context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=?", new String[]{file.getAbsolutePath()});
            } catch (Exception ignored) {}

            newUri = context.getContentResolver().insert(uri, values);
            if (newUri == null) {
                newUri = Uri.fromFile(file);
            }

            RingtoneManager.setActualDefaultRingtoneUri(context, type, newUri);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static File getTonesDir(Context context) {
        File dir = new File(context.getFilesDir(), "ringtones");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static File ensureToneFile(Context context, ToneInfo tone) {
        File dir = getTonesDir(context);
        File file = new File(dir, "raven_tone_" + tone.id + ".wav");
        if (file.exists() && file.length() > 1000) {
            return file;
        }

        // Generate harmonic tone file (PCM 16-bit 44.1kHz mono WAV)
        try {
            generateWavFile(file, tone.baseFreq, tone.id == 3 ? 1.2 : 3.5);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void generateWavFile(File file, double freq, double durationSec) throws Exception {
        int sampleRate = 44100;
        int numSamples = (int) (durationSec * sampleRate);
        byte[] pcmData = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / sampleRate;
            // Envelope: quick attack, smooth exponential decay
            double envelope = Math.exp(-t * 2.2);
            // Rich harmonic chord: fundamental + major third + fifth
            double sample = 0.55 * Math.sin(2.0 * Math.PI * freq * t)
                          + 0.30 * Math.sin(2.0 * Math.PI * (freq * 1.25) * t)
                          + 0.15 * Math.sin(2.0 * Math.PI * (freq * 1.5) * t);

            short val = (short) (sample * envelope * 32000.0);
            pcmData[i * 2] = (byte) (val & 0xff);
            pcmData[i * 2 + 1] = (byte) ((val >> 8) & 0xff);
        }

        FileOutputStream fos = new FileOutputStream(file);
        // WAV Header (44 bytes)
        int totalDataLen = pcmData.length + 36;
        int byteRate = sampleRate * 2;

        byte[] header = new byte[44];
        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';
        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0; // Subchunk1Size
        header[20] = 1; header[21] = 0; // AudioFormat (PCM)
        header[22] = 1; header[23] = 0; // NumChannels (1 = Mono)
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = 2; header[33] = 0; // BlockAlign
        header[34] = 16; header[35] = 0; // BitsPerSample
        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
        header[40] = (byte) (pcmData.length & 0xff);
        header[41] = (byte) ((pcmData.length >> 8) & 0xff);
        header[42] = (byte) ((pcmData.length >> 16) & 0xff);
        header[43] = (byte) ((pcmData.length >> 24) & 0xff);

        fos.write(header);
        fos.write(pcmData);
        fos.close();
    }
}
