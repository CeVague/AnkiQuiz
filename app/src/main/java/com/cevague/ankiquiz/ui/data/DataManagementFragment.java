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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cevague.ankiquiz.R;
import com.cevague.ankiquiz.sql.DBHelper;
import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.sql.InfoModel;
import com.cevague.ankiquiz.utils.AudioPlayer;
import com.cevague.ankiquiz.utils.ZipUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

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
                String str_selected = spinner_import.getSelectedItem().toString();
                if(!str_selected.equals(CREATE_NEW)){
                    button_import.setEnabled(true);
                    // Si on a selectionné un item, on affiche les datas qu'on a dessus
                    RecyclerView recyclerView = requireView().findViewById(R.id.recyclerView_data);

                    actualiseListRV(str_selected);

                }else{
                    button_import.setEnabled(false);
                    RecyclerView recyclerView = requireView().findViewById(R.id.recyclerView_data);
                    ArrayList<InfoModel> list_info = new ArrayList<InfoModel>();
                    ArrayList<FileModel> list_file = new ArrayList<FileModel>();
                    InfoRecyclerViewAdapter infoRVA = new InfoRecyclerViewAdapter(getContext(), list_info, list_file);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(infoRVA);

                    button_import.callOnClick();
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
                            if (!text.isBlank() && isValidString(text)) {
                                data_folder.mkdirs();
                                actualiseListDataset();
                                int i = adapter.getPosition(text);
                                spinner_import.setSelection(i);
                                button_import.setEnabled(true);
                            }else{
                                Toast.makeText(getContext(), "Ce nom n'est pas valide", Toast.LENGTH_SHORT).show();
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



    private static boolean isValidString(String input) {
        // Expression régulière pour vérifier les caractères autorisés
        String regex = "^[a-zA-Z0-9 _]*$";

        // Vérification de la chaîne par rapport à l'expression régulière
        return input.matches(regex);
    }

    private void actualiseListRV(String str_selected){
        RecyclerView recyclerView = requireView().findViewById(R.id.recyclerView_data);

        try (DBHelper db = new DBHelper(getContext())) {
            ArrayList<InfoModel> list_info = db.getAllInfo(str_selected);
            ArrayList<FileModel> list_file = db.getAllFiles(str_selected);
            InfoRecyclerViewAdapter infoRVA = new InfoRecyclerViewAdapter(getContext(), list_info, list_file);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(infoRVA);

            infoRVA.setOnItemClickListener(new InfoRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(InfoModel item) {
                    ArrayList<FileModel> list_file = db.getAllFiles(item.getId_i(), "mp3");

                    int r = new Random().nextInt(list_file.size());
                    AudioPlayer.playAudio(getContext(), list_file.get(r).getAbsolute_path());
                }
            });

        }
    }


    // Actualise le spinner selon les dossiers présents
    private void actualiseListDataset(){
        adapter.clear();

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

        adapter.add(CREATE_NEW);
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

            Log.d("FilePicker", "ZIP copié : " + internalZipFile.getAbsolutePath());
            Log.d("FilePicker", "internalZipFile " + internalZipFile.getPath());
            Log.d("FilePicker", "outputPath " + outputPath);

            // Unzip after copy
            File output_file = new File(requireContext().getFilesDir(), outputPath);
            ZipUtils.unzip(internalZipFile, output_file);
            // Delete old zip
            internalZipFile.delete();
            // populate db
            readCSVFile(output_file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erreur de copie", Toast.LENGTH_SHORT).show();
        }
    }

    private void readCSVFile(File directory) throws IOException {
        String locale = Locale.getDefault().getLanguage();
        File[] files = directory.listFiles((dir, name) -> name.equals(locale+".csv"));

        if(files == null || files.length == 0){
            files = directory.listFiles((dir, name) -> name.equals("en.csv"));
        }

        if (files != null && files.length > 0) {
            File csvFile = files[0];
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine();

                String line;
                while ((line = br.readLine()) != null) {
                    String[] fields = (line + ", ").split(",");

                    // Création et/ou ajout d'un info la à DB
                    InfoModel info_tmp = new InfoModel(-1, directory.getName(), fields[0], fields[1], fields[2], fields[3], fields[4]);

                    InfoModel info = getOrCreateInfo(info_tmp);
                    Log.i("Populate DB", info.toString());

                    // Check files in their folder
                    File path_card = new File(requireContext().getFilesDir(), "data/"+info.getCard_set()+"/"+info.getFolder());

                    File[] list_files = path_card.listFiles();
                    if(list_files != null){
                        for(File file : list_files){

                            String type = file.getName().substring(file.getName().length()-3);

                            FileModel file_tmp = new FileModel(-1, info.getId_i(), info.getCard_set(), file.getName(), path_card + "/" + file.getName(), type);
                            addFileDB(file_tmp);
                            Log.i("Populate DB", file_tmp.toString());
                        }
                    }

                }
            }

            actualiseListRV(directory.getName());

        } else {
            System.out.println("No CSV file found in directory.");
        }
    }

    private InfoModel getOrCreateInfo(InfoModel info){
        try (DBHelper db = new DBHelper(getContext())) {
            // If info already exist, we get it, else we create it
            if (db.existInfo(info.getCard_set(), info.getFolder())) {
                info = db.getInfo(info.getCard_set(), info.getFolder());
            } else {
                long id = db.addInfo(info);
                info.setId_i(id);
            }
        }
        return info;
    }

    private void addFileDB(FileModel file){
        try (DBHelper db = new DBHelper(getContext())) {
            // If info already exist, we get it, else we create it
            if (!db.existFile(file)) {
                db.addFile(file);
            }
        }
    }


}