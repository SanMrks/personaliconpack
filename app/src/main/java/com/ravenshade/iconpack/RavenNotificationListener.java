package com.ravenshade.iconpack;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.lang.ref.WeakReference;
import java.util.List;

public class RavenNotificationListener extends NotificationListenerService {

    private static WeakReference<RavenNotificationListener> sInstanceRef = null;
    private MediaSessionManager mSessionManager;
    private MediaController mActiveController = null;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final MediaController.Callback mControllerCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            syncFromController(mActiveController);
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            syncFromController(mActiveController);
        }

        @Override
        public void onSessionDestroyed() {
            mActiveController = null;
            findActiveSession();
        }
    };

    private final MediaSessionManager.OnActiveSessionsChangedListener mSessionsListener =
            new MediaSessionManager.OnActiveSessionsChangedListener() {
                @Override
                public void onActiveSessionsChanged(List<MediaController> controllers) {
                    pickBestController(controllers);
                }
            };

    @Override
    public void onCreate() {
        super.onCreate();
        sInstanceRef = new WeakReference<>(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSessionManager != null && mSessionsListener != null) {
            try {
                mSessionManager.removeOnActiveSessionsChangedListener(mSessionsListener);
            } catch (Exception ignored) {}
        }
        if (mActiveController != null) {
            try {
                mActiveController.unregisterCallback(mControllerCallback);
            } catch (Exception ignored) {}
        }
        sInstanceRef = null;
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        sInstanceRef = new WeakReference<>(this);
        initMediaSessions();
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        sInstanceRef = null;
    }

    private void initMediaSessions() {
        try {
            mSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
            if (mSessionManager != null) {
                ComponentName comp = new ComponentName(this, RavenNotificationListener.class);
                mSessionManager.addOnActiveSessionsChangedListener(mSessionsListener, comp, mHandler);
                List<MediaController> list = mSessionManager.getActiveSessions(comp);
                pickBestController(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findActiveSession() {
        if (mSessionManager == null) return;
        try {
            ComponentName comp = new ComponentName(this, RavenNotificationListener.class);
            List<MediaController> list = mSessionManager.getActiveSessions(comp);
            pickBestController(list);
        } catch (Exception ignored) {}
    }

    private void pickBestController(List<MediaController> controllers) {
        if (controllers == null || controllers.isEmpty()) {
            return;
        }

        MediaController chosen = null;
        // Priority 1: Check if convx is playing or active
        for (MediaController mc : controllers) {
            if ("com.convx.music".equals(mc.getPackageName())) {
                chosen = mc;
                break;
            }
        }

        // Priority 2: Any currently playing session
        if (chosen == null) {
            for (MediaController mc : controllers) {
                PlaybackState ps = mc.getPlaybackState();
                if (ps != null && ps.getState() == PlaybackState.STATE_PLAYING) {
                    chosen = mc;
                    break;
                }
            }
        }

        // Priority 3: First available session
        if (chosen == null) {
            chosen = controllers.get(0);
        }

        if (mActiveController != chosen) {
            if (mActiveController != null) {
                try {
                    mActiveController.unregisterCallback(mControllerCallback);
                } catch (Exception ignored) {}
            }
            mActiveController = chosen;
            if (mActiveController != null) {
                mActiveController.registerCallback(mControllerCallback, mHandler);
            }
        }

        syncFromController(mActiveController);
    }

    private void syncFromController(MediaController controller) {
        if (controller == null) return;
        try {
            MediaMetadata meta = controller.getMetadata();
            PlaybackState state = controller.getPlaybackState();

            String title = null;
            String artist = null;
            Bitmap art = null;
            boolean isPlaying = false;

            if (state != null) {
                isPlaying = (state.getState() == PlaybackState.STATE_PLAYING);
            }

            if (meta != null) {
                title = meta.getString(MediaMetadata.METADATA_KEY_TITLE);
                artist = meta.getString(MediaMetadata.METADATA_KEY_ARTIST);
                if (artist == null) {
                    artist = meta.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST);
                }
                art = meta.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
                if (art == null) {
                    art = meta.getBitmap(MediaMetadata.METADATA_KEY_ART);
                }
            }

            long pos = (state != null) ? state.getPosition() : 0;
            long dur = (meta != null) ? meta.getLong(MediaMetadata.METADATA_KEY_DURATION) : 0;

            if (title != null && !title.isEmpty()) {
                RavenMusicManager.updateTrackWithProgress(this, title, artist, isPlaying, controller.getPackageName(), art, pos, dur);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null) return;
        Notification n = sbn.getNotification();
        if (n == null) return;

        Bundle extras = n.extras;
        if (extras == null) return;

        String pkg = sbn.getPackageName();
        boolean isConvx = "com.convx.music".equals(pkg);
        boolean isMedia = extras.containsKey(Notification.EXTRA_MEDIA_SESSION)
                || Notification.CATEGORY_TRANSPORT.equals(n.category)
                || isConvx;

        if (isMedia) {
            CharSequence titleSeq = extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence textSeq = extras.getCharSequence(Notification.EXTRA_TEXT);

            if (titleSeq != null && titleSeq.length() > 0) {
                String title = titleSeq.toString();
                String artist = textSeq != null ? textSeq.toString() : "";

                Bitmap art = null;
                try {
                    if (extras.containsKey(Notification.EXTRA_LARGE_ICON)) {
                        Object obj = extras.get(Notification.EXTRA_LARGE_ICON);
                        if (obj instanceof Bitmap) {
                            art = (Bitmap) obj;
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && obj instanceof Icon) {
                            // Icon to drawable if possible, or loaded from controller
                        }
                    }
                } catch (Exception ignored) {}

                // Check media session token if present
                if (extras.containsKey(Notification.EXTRA_MEDIA_SESSION)) {
                    try {
                        MediaSession.Token token = (MediaSession.Token) extras.getParcelable(Notification.EXTRA_MEDIA_SESSION);
                        if (token != null) {
                            MediaController mc = new MediaController(this, token);
                            if (mActiveController != mc) {
                                mActiveController = mc;
                                mActiveController.registerCallback(mControllerCallback, mHandler);
                            }
                            syncFromController(mActiveController);
                            return;
                        }
                    } catch (Exception ignored) {}
                }

                // If not extracted via session token, update directly from notification
                RavenMusicManager.updateTrack(this, title, artist, true, pkg, art);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn == null) return;
        String pkg = sbn.getPackageName();
        if (pkg.equals(RavenMusicManager.getPlayingPackage(this))) {
            findActiveSession();
        }
    }

    // --- STATIC ACTION DISPATCHERS ---

    public static boolean dispatchPlayPause(Context context) {
        RavenNotificationListener listener = getInstance();
        if (listener != null && listener.mActiveController != null) {
            try {
                PlaybackState state = listener.mActiveController.getPlaybackState();
                if (state != null && state.getState() == PlaybackState.STATE_PLAYING) {
                    listener.mActiveController.getTransportControls().pause();
                    RavenMusicManager.updateTrack(context, RavenMusicManager.getTitle(context),
                            RavenMusicManager.getArtist(context), false, listener.mActiveController.getPackageName(), null);
                } else {
                    listener.mActiveController.getTransportControls().play();
                    RavenMusicManager.updateTrack(context, RavenMusicManager.getTitle(context),
                            RavenMusicManager.getArtist(context), true, listener.mActiveController.getPackageName(), null);
                }
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public static boolean dispatchNext(Context context) {
        RavenNotificationListener listener = getInstance();
        if (listener != null && listener.mActiveController != null) {
            try {
                listener.mActiveController.getTransportControls().skipToNext();
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public static boolean dispatchPrevious(Context context) {
        RavenNotificationListener listener = getInstance();
        if (listener != null && listener.mActiveController != null) {
            try {
                listener.mActiveController.getTransportControls().skipToPrevious();
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public static RavenNotificationListener getInstance() {
        return sInstanceRef != null ? sInstanceRef.get() : null;
    }
}
