package com.cevague.ankiquiz.ui.data;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.CardModel;
import com.cevague.ankiquiz.sql.FilesModel;
import com.cevague.ankiquiz.sql.InfoModel;


public class DataManagementFragment extends Fragment {

    private File internalZipFile; // Emplacement du fichier ZIP dans la mémoire interne

    // Lanceur pour sélectionner le fichier
    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    if (fileUri != null) {
                        String fileName = getFileName(fileUri);
                        // Copier le fichier dans la mémoire interne et le décompresser
                        copyFileToInternalStorage(fileUri, fileName);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_management, container, false);

        Button buttonPickFile = view.findViewById(R.id.button_add_data);

        buttonPickFile.setOnClickListener(v -> openFilePicker());

        updateDatabase("data/");

        return view;
    }

    private void updateDatabase(String folderName){
        // On récupère la langue en deux caractères
        String locale_string = Locale.getDefault().getLanguage();

        // Si le fichier __.csv n'existe pas on prend en.csv par défaut
        String path_csv = folderName + locale_string + ".csv";
        File file = new File(requireContext().getFilesDir(), path_csv);
        if(!file.exists()){
            path_csv = folderName + "en.csv";
        }

        file = new File(requireContext().getFilesDir(), path_csv);

        if (!file.exists()) {
            Log.e("FileReader", "❌ Fichier csv introuvable ");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // On saute la première ligne
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                line += ", "; // Pour ne pas faire planter si la dernière colonne est vide
                String[] tmp_line = line.split(",");
                InfoModel tmp_info = new InfoModel(-1, tmp_line[1], tmp_line[2], tmp_line[3], tmp_line[4]);


                // On donne l'InfoModel et le path vers les fichiers
                CardModel card_tmp = createCard(tmp_info, folderName+tmp_line[0]);

                // S'il n'y a pas d'image par défaut on en prend une au hasard
                if(tmp_line[4].isEmpty()){
                    tmp_info.setImg_uri(card_tmp.getImages().getRandomUri());
                }else{
                    // Sinon on reformate le chemin
                    tmp_info.setImg_uri(folderName+tmp_line[0]+"/"+tmp_line[4]);
                }

                card_tmp.setInfo(tmp_info);
                Log.d("FileReader", card_tmp.toString());

                // La on va charger la card dans la DB
            }
        } catch (IOException e) {
            Log.e("FileReader", "Erreur lors de la lecture du fichier", e);
        }
    }

    // Crée une cardModel d'après les infos et le path vers le dossier de fichiers
    private CardModel createCard(InfoModel info, String path){

        ArrayList<String> audios = new ArrayList<String>();
        ArrayList<String> images = new ArrayList<String>();
        ArrayList<String> texts = new ArrayList<String>();

        // Pour notre dossier de fichier
        File subFolder = new File(requireContext().getFilesDir(), path);

        File[] files = subFolder.listFiles();
        if (files != null && files.length > 0) { // S'il n'est pas vide
            for (File file : files) {
                String file_name = file.getName();
                if(file_name.endsWith("mp3") || file_name.endsWith("wav")){
                    audios.add(path+"/"+file_name);
                }else if(file_name.endsWith("jpg") || file_name.endsWith("png")){
                    images.add(path+"/"+file_name);
                }else if(file_name.endsWith("txt")){
                    texts.add(path+"/"+file_name);
                }
            }
        } else {
            Log.d("FileExplorer", "📂 Aucun fichier dans " + texts);
        }

        return new CardModel(-1, info, new FilesModel(audios), new FilesModel(images), new FilesModel(texts), true, 0, new Date());
    }



    // Ouvrir le sélecteur de fichiers (SAF)
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip"); // On filtre uniquement les fichiers ZIP

        filePickerLauncher.launch(intent);
    }

    // Obtenir le nom du fichier sélectionné
    private String getFileName(Uri uri) {
        String fileName = "Fichier inconnu";
        ContentResolver contentResolver = requireContext().getContentResolver();

        try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    // Copier le fichier ZIP sélectionné vers la mémoire interne de l'application
    private void copyFileToInternalStorage(Uri fileUri, String fileName) {
        try {
            File internalDir = requireContext().getFilesDir(); // Dossier interne de l'app
            internalZipFile = new File(internalDir, fileName);

            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);
                 FileOutputStream outputStream = new FileOutputStream(internalZipFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }

            Toast.makeText(getContext(), "ZIP copié en interne: " + internalZipFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.d("FilePicker", "ZIP copié : " + internalZipFile.getAbsolutePath());

            // Décompresser après copie
            unzipFile(internalZipFile, new File(requireContext().getFilesDir(), "data"));
            internalZipFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erreur de copie", Toast.LENGTH_SHORT).show();
        }
    }

    // Décompresser un fichier ZIP
    private void unzipFile(File zipFile, File outputDir) {
        if (!outputDir.exists()) {
            outputDir.mkdirs(); // Créer le dossier si inexistant
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File newFile = new File(outputDir, zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    newFile.mkdirs(); // Créer le dossier si c'est un dossier
                } else {
                    File parent = newFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs(); // Créer les dossiers parents si besoin
                    }

                    try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }

            Toast.makeText(getContext(), "Décompression terminée", Toast.LENGTH_SHORT).show();
            Log.d("FilePicker", "ZIP décompressé dans : " + outputDir.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erreur de décompression", Toast.LENGTH_SHORT).show();
        }
    }
}