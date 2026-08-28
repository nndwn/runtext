# Implementation Plan - Replace Ads with Support Dialog (25 min interval)

The user has replaced Ads with a `SupportDialog`, but the implementation still uses "Ad" terminology in the data layer and the interval is set to 15 minutes instead of the intended 25 minutes. This plan will synchronize the code with the new logic.

## User Review Required

> [!NOTE]
> The interval for showing the support dialog will be changed from 15 minutes (900,000ms) to 25 minutes (1,500,000ms).

## Proposed Changes

### Data Layer

#### [MODIFY] [SettingsDataStore.kt](file:///home/nndwn/dev/runtext/app/src/main/java/com/nndwn/runtext/data/datastore/SettingsDataStore.kt)
- Update `AD_COOLDOWN_MS` to `1_500_000L` (25 minutes).
- Rename `LAST_AD_SHOWN_TIMESTAMP_KEY` to `LAST_SUPPORT_DIALOG_SHOWN_TIMESTAMP_KEY`.
- Rename `shouldShowAd` to `shouldShowSupportDialog`.
- Rename `recordAdShown` to `recordSupportDialogShown`.
- Rename `recordAdShownIfFirstTime` to `recordSupportDialogShownIfFirstTime`.
- Rename `debugForceShowAd` to `debugForceShowSupportDialog`.

#### [MODIFY] [SettingsRepository.kt](file:///home/nndwn/dev/runtext/app/src/main/java/com/nndwn/runtext/data/repository/SettingsRepository.kt)
- Update calls to renamed `SettingsDataStore` methods.
- Rename `shouldShowDialogSupport` to `shouldShowSupportDialog` to match (optional but good for consistency).

### UI Layer

#### [MODIFY] [AppViewModel.kt](file:///home/nndwn/dev/runtext/app/src/main/java/com/nndwn/runtext/ui/AppViewModel.kt)
- Update references to repository flows/methods.

#### [MODIFY] [RunTextApp.kt](file:///home/nndwn/dev/runtext/app/src/main/java/com/nndwn/runtext/ui/RunTextApp.kt)
- Rename internal variable `shouldShowAd` to `showDialogTrigger` or similar to avoid confusion with the old Ad logic.

## Verification Plan

### Automated Tests
- Run internal logic checks for `SettingsDataStore` if applicable.

### Manual Verification
- Verify `SettingsDataStore` has the correct `1_500_000L` value.
- Re-run the app in debug mode and use the debug tool to force show the dialog to ensure the logic still works after renaming.
