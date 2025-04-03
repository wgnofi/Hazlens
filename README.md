# HazLens - Eerie Image Processing App

HazLens is an Android application that allows users to apply chilling, unsettling filters to their images. It utilizes WorkManager to handle background image processing, ensuring a smooth and reliable user experience.

## Features

* **Eerie Image Filters:**
    * **Bitmap Decay:** Distorts and degrades the image, creating a decaying effect.
    * **Mutated Blur:** Applies an unnatural, unsettling blur to the image.
    * **Pixel Parasite:** Corrupts the image pixels, creating a parasitic visual effect.
* **WorkManager Integration:**
    * Uses WorkManager to handle background image processing, ensuring the app remains responsive.
    * Chains three distinct workers for efficient image processing:
        * `CleanupWorker`: Cleans up temporary files.
        * `FilterWorker`: Applies the selected eerie filter.
        * `SaveImageToFileWorker`: Saves the processed image to a file.
* **Utility Functions:**
    * `WorkerUtils.kt`: Contains filter functions and file saving functions for image processing.
* **Constants:**
    * `Constants.kt`: Stores constants used throughout the application.

## Technologies Used

* Kotlin
* Android WorkManager
* Android Bitmap
* Android File I/O
* Jetpack Compose

## Project Structure

```
HazLens/
├── app/
│   ├── manifests/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/
│   │   │   │   └── com/example/hazlens/
│   │   │   │       ├── data/
│   │   │   │       │   ├── AppContainer.kt
│   │   │   │       │   ├── FilterOp.kt
│   │   │   │       │   ├── FilterOptionData.kt
│   │   │   │       │   ├── HazRepository.kt
│   │   │   │       │   └── WorkManagerHazRepository.kt
│   │   │   │       ├── ui/
│   │   │   │       │   ├── theme/
│   │   │   │       │   │   ├── Color.kt
│   │   │   │       │   │   ├── Theme.kt
│   │   │   │       │   │   └── Type.kt
│   │   │   │       │   ├── HazlensScreen.kt
│   │   │   │       │   └── HazViewModel.kt
│   │   │   │       ├── workers/
│   │   │   │       │   ├── CleanupWorker.kt
│   │   │   │       │   ├── FilterWorker.kt
│   │   │   │       │   └── SaveImageToFileWorker.kt
│   │   │   │       ├── Constants.kt
│   │   │   │       ├── HazlensApplication.kt
│   │   │   │       └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── ... (layout files, drawables, etc.)
├── build.gradle.kts
├── settings.gradle.kts
```

## Setup

1.  Clone the repository:

    ```
    git clone https://github.com/wgnofi/Hazlens
    ```

2.  Open the project in Android Studio.

3.  Build and run the app on an Android emulator or device.

## Usage

1.  Launch the HazLens app.
2.  Select an image from your device's gallery.
3.  Choose one of the eerie filters: "Bitmap Decay", "Mutated Blur", or "Pixel Parasite".
4.  The app will process the image in the background using WorkManager.
5.  The processed image will be saved to your device's storage and displayed in the app.

## Screenshots
![hz1](https://github.com/user-attachments/assets/dc5fd8e7-1470-46cf-be9c-11e081138a25)
![hz2](https://github.com/user-attachments/assets/c96f90d0-5f34-43f6-a2cc-364cdda52a6b)
![hz3](https://github.com/user-attachments/assets/e46d2e64-1a8c-47ec-b28a-b1cba2bff749)
![hz4](https://github.com/user-attachments/assets/b96a63e0-bdff-4c4a-83b2-b30316ef04e4)
![hz5](https://github.com/user-attachments/assets/5af44187-e6ea-4185-af5e-6dc587d6d4d2)
![hz6](https://github.com/user-attachments/assets/a5a82539-e576-41bc-ba74-8dbd822bea63)
![hz7](https://github.com/user-attachments/assets/06a40efc-043b-40a4-a6eb-7e64c554af72)
![hz8](https://github.com/user-attachments/assets/8c3c4db3-f9e8-46eb-bbc5-4e87fb9fdf82)


## WorkManager Flow

1.  The user selects an image and a filter.
2.  A `OneTimeWorkRequest` is created, chaining the three workers: `CleanupWorker` -> `FilterWorker` -> `SaveImageToFileWorker`.
3.  `CleanupWorker` removes any previous temporary files.
4.  `FilterWorker` applies the selected filter to the image using functions from `WorkerUtils.kt`.
5.  `SaveImageToFileWorker` saves the processed image to a file.
6.  The app displays a notification when the processing is complete.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

## License

MIT License
