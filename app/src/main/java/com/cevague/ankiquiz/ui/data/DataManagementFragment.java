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
import com.cevague.ankiquiz.sql.CardModel;
import com.cevague.ankiquiz.sql.DBHelper;
import com.cevague.ankiquiz.sql.FileModel;
import com.cevague.ankiquiz.sql.InfoModel;
import com.cevague.ankiquiz.utils.ZipUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class DataManagementFragment extends Fragment {

    ArrayList<String> list_dataset = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    String CREATE_NEW;

    String lastSetFolderName, lastSetName;


    Button btnImport, btnImportUrl, btnRename;
    Spinner spinnerImport;
    private File internalZipFile; // Emplacement du fichier ZIP dans la mémoire interne

    // Lanceur pour sélectionner le fichier
    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    if (fileUri != null) {
                        String fileName = getFileName(fileUri);
                        // Copier le fichier dans la mémoire interne et le décompresser
                        copyFileToInternalStorage(fileUri, fileName, "data/" + lastSetFolderName);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_management, container, false);

        generateNewSetFolderName();

        CREATE_NEW = getResources().getString(R.string.create_dataset);

        btnImport = view.findViewById(R.id.button_import_data);
        btnRename = view.findViewById(R.id.button_rename);
        btnImportUrl = view.findViewById(R.id.button_import_data_url);
        spinnerImport = view.findViewById(R.id.spinner_import_data);

        // Créer un ArrayAdapter avec la liste d'éléments
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, list_dataset);

        // Spécifier le layout à utiliser lorsque la liste des choix apparaît
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Appliquer l'adaptateur au Spinner
        spinnerImport.setAdapter(adapter);

        spinnerImport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str_selected = spinnerImport.getSelectedItem().toString();
                lastSetName = str_selected;
                if(!str_selected.equals(CREATE_NEW)){
                    btnRename.setEnabled(true);
                    btnImport.setEnabled(true);
                    btnImportUrl.setEnabled(true);
                    // Si on a selectionné un item, on affiche les datas qu'on a dessus
                    actualiseListRV(str_selected);
                }else{
                    btnRename.setEnabled(false);
                    btnImport.setEnabled(true);
                    btnImportUrl.setEnabled(true);
                    RecyclerView recyclerView = requireView().findViewById(R.id.recyclerView_data);
                    ArrayList<InfoModel> list_info = new ArrayList<InfoModel>();
                    ArrayList<FileModel> list_file = new ArrayList<FileModel>();
                    InfoRecyclerViewAdapter infoRVA = new InfoRecyclerViewAdapter(getContext(), list_info, list_file);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(infoRVA);

                    lastSetName = spinnerImport.getSelectedItem().toString();

                    createNewDatasetDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        actualiseListDataset();

        btnRename.setOnClickListener(v -> {
            showNewDatasetDialog(s -> {
                String oldName = spinnerImport.getSelectedItem().toString();
                String newName = s;

                try (DBHelper db = new DBHelper(getContext())) {
                    db.renameCardSet(oldName, newName);
                }

                actualiseListDataset();

            }, getResources().getString(R.string.rename));
        });

        btnImport.setOnClickListener(v -> {
            String spinner_item = spinnerImport.getSelectedItem().toString();
            // Si on cherche a créer un nouveau dataset
            if(spinner_item.equals(CREATE_NEW)){
                createNewDatasetDialog();
            }else{
                DBHelper db = new DBHelper(getContext());
                lastSetFolderName = db.getSetFolderFromSetName(lastSetName);

                if(lastSetFolderName == null){
                    generateNewSetFolderName();
                }

                // Code de loading d'une BDD
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/zip");
                filePickerLauncher.launch(intent);
            }
        });

        return view;
    }

    private void generateNewSetFolderName(){
        lastSetFolderName = UUID.randomUUID().toString();
    }

    private void createNewDatasetDialog(){
        showNewDatasetDialog(s -> {
            lastSetName = s;
            actualiseListDataset();
            int i = adapter.getPosition(s);
            spinnerImport.setSelection(i);
        }, getResources().getString(R.string.add));
    }

    private void showNewDatasetDialog(Consumer<String> callback, String validation){
        generateNewSetFolderName();
        DialogNewDatasetFragment dialogFragment = new DialogNewDatasetFragment();
        dialogFragment.setBtnAddText(validation);
        dialogFragment.setTextInputListener(text -> {
            if (!text.isBlank()) {
                callback.accept(text);
            }else{
                Toast.makeText(getContext(), "Ce nom n'est pas valide", Toast.LENGTH_SHORT).show();
            }
        });
        dialogFragment.show(getParentFragmentManager(), "TextInputDialog");
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
                    //AudioPlayer.playAudio(getContext(), list_file.get(r).getAbsolute_path());



                    SetManagementFragment fragment = new SetManagementFragment();

                    Bundle bundle = new Bundle();
                    bundle.putLong("id_info", item.getId_i());

                    fragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment) // fragment_container = id du container dans le layout
                            .addToBackStack(null) // optionnel si tu veux revenir en arrière
                            .commit();


                }
            });

        }
    }


    // Actualise le spinner selon les dossiers présents
    private void actualiseListDataset(){
        adapter.clear();

        ArrayList<String> listCardSetNames;
        try (DBHelper db = new DBHelper(getContext())) {
            listCardSetNames = db.getAllCardSet();
        }

        adapter.addAll(listCardSetNames);

        if(lastSetName != null && !listCardSetNames.contains(lastSetName)){
            adapter.add(lastSetName);
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
                    String[] fields = (line + "; ").split(";");

                    // Création et/ou ajout d'un info la à DB
                    InfoModel info_tmp = new InfoModel(-1, lastSetName, fields[0], fields[1], fields[2], fields[3], fields[4]);

                    InfoModel info = getOrCreateInfo(info_tmp);
                    Log.i("Populate DB", info.toString());

                    // Ajout des cards
                    CardModel card_tmp = new CardModel(info, 0, Calendar.getInstance().getTime());
                    createCard(card_tmp);


                    // Check files in their folder
                    File path_card = new File(requireContext().getFilesDir(), "data/"+lastSetFolderName+"/"+info.getFolder());

                    File[] list_files = path_card.listFiles();
                    if(list_files != null){
                        for(File file : list_files){

                            String type = file.getName().substring(file.getName().length()-3);

                            FileModel file_tmp = new FileModel(-1, info.getId_i(), info.getCard_set(), lastSetFolderName, info.getFolder(), file.getName(), type);
                            addFileDB(file_tmp);
                            Log.i("Populate DB", file_tmp.toString());
                        }
                    }

                }
            }

            actualiseListRV(lastSetName);

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

    private void createCard(CardModel card){
        try (DBHelper db = new DBHelper(getContext())) {
            // If card doesn't exist, we create it
            if (!db.existCard(card)) {
                long id = db.addCard(card);
            }
        }
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