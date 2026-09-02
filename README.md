# PDP Viewer
A Compose PDF reader for Android using only native components (no external libraries), we will use Android's built-in android.graphics.pdf.PdfRenderer.
Orientation is applied .

# Architecture
├── app/ (Navigation & Hilt Setup)
├── core/ (Common UI & MVI Base)
├── data/ (Room DB & Repo Implementation)
├── domain/ (Models, Repo Interfaces & Use Cases)
├── feature/
│   ├── pdf/ (PDF Viewer Screen)
│   └── history/ (History List Screen)
└── build.gradle.kts (Modularized Build Logic)

# Verification
•The app now supports bottom navigation between the Viewer and History.

•PDF files opened via the file picker or external intents are automatically saved to history.

•You can clear your history using the "Delete All" icon in the History screen.

•The project is fully compliant with modern Android development standards.



<img width="270" height="606" alt="image" src="https://github.com/user-attachments/assets/4819a440-85b5-4aeb-a0a8-18364ba9cbfc" />



