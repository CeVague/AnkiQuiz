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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.cevague.ankiquiz.DialogNewDatasetFragment;
import com.cevague.ankiquiz.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataManagementFragment extends Fragment {

    ArrayList<String> list_dataset = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    String CREATE_NEW;


    Button button_import;
    Spinner spinner_import;
    private File internalZipFile; // Emplacement du fichier ZIP dans la mémoire interne

    // Lanceur pour sélectionner le fichier
    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    if (fileUri != null) {
                        String fileName = getFileName(fileUri);
                        // Copier le fichier dans la mémoire interne et le décompresser
                        copyFileToInternalStorage(fileUri, fileName, "data/" + spinner_import.getSelectedItem().toString());
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_management, container, false);

        CREATE_NEW = getResources().getString(R.string.create_dataset);

        button_import = view.findViewById(R.id.button_import_data);
        spinner_import = view.findViewById(R.id.spinner_import_data);

        // Créer un ArrayAdapter avec la liste d'éléments
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, list_dataset);

        // Spécifier le layout à utiliser lorsque la liste des choix apparaît
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Appliquer l'adaptateur au Spinner
        spinner_import.setAdapter(adapter);

        spinner_import.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    // Si on a selectionné un item, on affiche les datas qu'on a dessus
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        actualiseListDataset();

        button_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spinner_item = spinner_import.getSelectedItem().toString();
                // Si on cherche a créer un nouveau dataset
                if(spinner_item.equals(CREATE_NEW)){

                    DialogNewDatasetFragment dialogFragment = new DialogNewDatasetFragment();
                    dialogFragment.setTextInputListener(new DialogNewDatasetFragment.TextInputListener() {
                        @Override
                        public void onTextEntered(String text) {
                            File data_folder = new File(getContext().getFilesDir(), "data/"+text);

                            // Vérifie si le dossier existe, sinon le créer
                            if (!data_folder.exists()) {
                                data_folder.mkdirs();
                                actualiseListDataset();
                            }
                        }
                    });
                    dialogFragment.show(getParentFragmentManager(), "TextInputDialog");
                }else{





                    // Code de loading d'une BDD

                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/zip");
                    filePickerLauncher.launch(intent);







                }
            }
        });

        return view;
    }

    // Actualise le spinner selon les dossiers présents
    private void actualiseListDataset(){
        adapter.clear();
        adapter.add(CREATE_NEW);

        File data_folder = new File(getContext().getFilesDir(), "data/");

        // Vérifie si le dossier existe, sinon le créer
        if (!data_folder.exists()) {
            data_folder.mkdirs();
        }

        // Liste des dossiers
        File[] files = data_folder.listFiles();
        if(files != null){
            for(File file : files) {
                if(file.isDirectory()){
                    adapter.add(file.getName());
                }
            }
        }

        adapter.notifyDataSetChanged();
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
    private void copyFileToInternalStorage(Uri fileUri, String fileName, String outputPath) {
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
            unzipFile(internalZipFile, new File(requireContext().getFilesDir(), outputPath));
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
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erreur de décompression", Toast.LENGTH_SHORT).show();
        }
    }
}