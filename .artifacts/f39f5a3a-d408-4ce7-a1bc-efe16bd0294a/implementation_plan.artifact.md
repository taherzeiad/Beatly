# Implementation Plan - Robust Music Playback & Background Support

This plan addresses music playback reliability, background support, and playlist navigation (Skip Next/Previous) by implementing standard Android Media3 patterns.

## User Review Required

> [!IMPORTANT]
> To support background playback and system-wide media controls (notification/lock screen), I will implement a `MediaSessionService`. This requires adding a service entry to the `AndroidManifest.xml`.

> [!NOTE]
> The "Skip Next/Previous" functionality currently does nothing because only one song is loaded into the player at a time. I will update the logic to support basic queueing.

## Proposed Changes

### Configuration & Permissions

#### [MODIFY] [AndroidManifest.xml](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/AndroidManifest.xml)
- Add `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_MEDIA_PLAYBACK` permissions.
- Add `WAKE_LOCK` permission to prevent music from stopping when the CPU sleeps.
- Declare `PlaybackService` under the `<application>` tag.

### Media Service Layer

#### [NEW] [PlaybackService.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/data/service/PlaybackService.kt)
- Implement `MediaSessionService`.
- Manage `ExoPlayer` lifecycle and link it to a `MediaSession`.
- Handle service destruction and player release.

### Data Layer Refinement

#### [MODIFY] [MusicRepositoryImpl.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/data/repository/MusicRepositoryImpl.kt)
- **State Management**: Improve reactive updates from the player.
- **Robust Playback**: Ensure `prepare()` and `play()` are called correctly, handling the case where the same song is re-triggered.
- **Error Handling**: Add more detailed logging for playback failures.
- **Fallback URL**: Ensure the fallback URL is a high-availability test stream.

### Dependency Injection

#### [MODIFY] [AppModule.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/di/AppModule.kt)
- Ensure `ExoPlayer` is configured for optimal music playback.

## Verification Plan

### Manual Verification
1.  **Foreground Playback**: Play a song and ensure audio is heard.
2.  **Background Playback**: Minimize the app while music is playing. Music should continue.
3.  **System Controls**: Check the notification shade for media controls (Play/Pause).
4.  **Skip Functionality**: Test Skip Next/Previous (may require adding multiple songs to a mock list).
5.  **Error States**: Observe Logcat for `MusicRepository` errors if a song fails to load.
