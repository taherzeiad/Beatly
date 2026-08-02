# Implementation Plan - Unused Code Removal

This plan focuses on removing redundant files, methods, DTOs, and imports to clean up the codebase without impacting the app's functionality.

## Proposed Changes

### File Deletion

#### [DELETE] [SupabaseStorageDataSource.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/data/remote/supabase/SupabaseStorageDataSource.kt)
- This file is unconfigured and not injected anywhere in the project.

#### [DELETE] [PlaylistSelectorDialog.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/ui/library/PlaylistSelectorDialog.kt)
- This composable is not referenced by any other file.

### Data Layer Cleanup

#### [MODIFY] [SpotifyTokenResponse.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/data/remote/spotify/SpotifyTokenResponse.kt)
- Remove unused DTOs: `SpotifyTracksResponse`, `SpotifyArtistResponse`, `SpotifyPlaylistTracksResponse`, `SpotifyPlaylistTrackItem`.
- Remove unused methods from `SpotifyApiService`: `getFeaturedPlaylists`, `getNewReleases`.

#### [MODIFY] [MusicRepositoryImpl.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/data/repository/MusicRepositoryImpl.kt)
- Remove commented-out code and unused imports.

#### [MODIFY] [SettingsDataStore.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/data/local/datastore/SettingsDataStore.kt)
- Remove unused `KEY_AUTH_TOKEN`.

### ViewModel Cleanup

#### [MODIFY] [LibraryViewModel.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/ui/library/LibraryViewModel.kt)
- Remove unused `BeatlyResult` import.

#### [MODIFY] [ProfileViewModel.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/ui/profile/ProfileViewModel.kt)
- Remove unused `BeatlyResult` import.

#### [MODIFY] [EditProfileViewModel.kt](file:///home/taher/AndroidStudioProjects/Beatly/app/src/main/java/com/taher/beatly/ui/settings/EditProfileViewModel.kt)
- Remove unused functions: `onAvatarChanged`, `onChangePin`, `onChangePassword`.

## Verification Plan

### Automated Tests
- Run `gradle build` to ensure that removing these components doesn't break any dependencies.
- Verify that `find_usages` returns no results for the deleted files/methods.

### Manual Verification
- Basic smoke test: Launch app, navigate to Home, Library, and Profile.
- Verify music playback still works.
