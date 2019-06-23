package org.quicksplit.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.quicksplit.CostWithCurrency;
import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.Utils;
import org.quicksplit.adapters.GroupFriendsAdapter;
import org.quicksplit.models.Group;
import org.quicksplit.models.GroupModelIn;
import org.quicksplit.models.Purchase;
import org.quicksplit.models.PurchaseModelIn;
import org.quicksplit.models.User;
import org.quicksplit.service.CurrencyClient;
import org.quicksplit.service.GroupClient;
import org.quicksplit.service.PurchaseClient;
import org.quicksplit.service.UserClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePurchaseActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_CAMERA = 1;
    private static final int PICK_IMAGE_GALLERY = 2;
    private static final int SCANN_QR_CAMERA = 3;
    private static final int PICK_LOCATION_MAP = 4;

    private ImageView mImagePurchase;
    private LinearLayout mLinearLayoutPurchaseImage;
    private Bitmap bitmap;
    private File destination = null;
    private String currentImagePath = null;

    private List<GroupModelIn> groups;
    private ArrayAdapter<GroupModelIn> groupArrayAdapter;
    private ArrayAdapter<String> currenciesArrayAdapter;

    private List<String> currencies;

    private List<User> members;
    private List<User> participants = new ArrayList<>();

    private Toolbar mToolbar;

    private TextInputLayout mTextInputLayoutPurchaseName;
    private EditText mEditTextPurchaseName;

    private TextInputLayout mTextInputLayoutSpnGroupName;
    private Spinner mSpinnerGroups;

    private TextInputLayout mTextInputLayoutSpnCurrency;
    private Spinner mSpinnerCurrency;

    private TextInputLayout mTextInputLayoutTxtCost;
    private EditText mEditTextCost;

    private TextInputLayout mTextInputLayoutGroupMembers;
    private RecyclerView mRecyclerViewGroupMembers;
    private GroupFriendsAdapter mRecycleViewGroupFriendsAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    private Button mButtonCreatePurchase;
    private Button mButtonAddMap;
    private Button mButtonScanCost;
    private Bundle myBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_purchase);

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mImagePurchase = findViewById(R.id.img_purchase);
        mLinearLayoutPurchaseImage = findViewById(R.id.lly_imgPurchase);
        mLinearLayoutPurchaseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPurchaseImage();
            }
        });

        mTextInputLayoutGroupMembers = findViewById(R.id.lblError_groupMembers);
        mRecyclerViewGroupMembers = findViewById(R.id.purchisersReciclerView);

        mSpinnerGroups = findViewById(R.id.spn_groupName);
        mSpinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getMembers((GroupModelIn) mSpinnerGroups.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerCurrency = findViewById(R.id.spn_currency);
        mSpinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTextInputLayoutPurchaseName = findViewById(R.id.lblError_purchaseName);
        mEditTextPurchaseName = findViewById(R.id.txt_purchaseName);

        mTextInputLayoutTxtCost = findViewById(R.id.lblError_txtCost);
        mEditTextCost = findViewById(R.id.txt_cost);

        mButtonCreatePurchase = findViewById(R.id.btn_createPurchase);
        mButtonCreatePurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPurchase();
            }
        });

        mButtonAddMap = findViewById(R.id.btn_location);
        mButtonAddMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatePurchaseActivity.this, MapActivity.class);
                startActivityForResult(intent, PICK_LOCATION_MAP);
            }
        });

        mButtonScanCost = findViewById(R.id.btn_scanCost);
        mButtonScanCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerActivity = new Intent(CreatePurchaseActivity.this, ScannerActivity.class);
                startActivityForResult(scannerActivity, SCANN_QR_CAMERA);
            }
        });

        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                break;
            case R.id.done:
                createPurchase();
                break;
            case R.id.picture:
                selectPurchaseImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData() {
        TokenManager tokenManager = new TokenManager(this);
        UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());
        Call<List<GroupModelIn>> call = client.getUserGroups(tokenManager.getUserIdFromToken());

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<GroupModelIn>>() {
            @Override
            public void onResponse(Call<List<GroupModelIn>> call, Response<List<GroupModelIn>> response) {
                if (response.isSuccessful()) {
                    groups = response.body();
                    buildGroupsAdapter();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(CreatePurchaseActivity.this, "Error al obtener grupos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GroupModelIn>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CreatePurchaseActivity.this, "Error en la comunicación al obtener grupos.", Toast.LENGTH_SHORT).show();
            }
        });

        getCurrencies();
    }

    private void getCurrencies() {
        TokenManager tokenManager = new TokenManager(this);
        CurrencyClient client = ServiceGenerator.createService(CurrencyClient.class, tokenManager.getToken());
        Call<List<String>> call = client.getCurrencies();

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    currencies = response.body();
                    buildCurrenciesAdapter();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(CreatePurchaseActivity.this, "Error al obtener monedas.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CreatePurchaseActivity.this, "Error en la comunicación al obtener monedas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildCurrenciesAdapter() {
        currenciesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);
        currenciesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCurrency.setAdapter(currenciesArrayAdapter);
    }

    private void buildGroupsAdapter() {
        groupArrayAdapter = new ArrayAdapter<GroupModelIn>(this, android.R.layout.simple_spinner_item, groups);
        groupArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGroups.setAdapter(groupArrayAdapter);
    }

    private void getMembers(GroupModelIn group) {
        TokenManager tokenManager = new TokenManager(this);
        String id = tokenManager.getUserIdFromToken();

        User user = new User();
        user.setId(id);

        members = group.getMemberships();
        members.remove(user);

        participants = new ArrayList<>(members);
        buildRecyclerViewCreateGroupFriendsAdapter();
    }

    private void buildRecyclerViewCreateGroupFriendsAdapter() {
        mRecyclerViewGroupMembers.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecycleViewGroupFriendsAdapter = new GroupFriendsAdapter(members);
        mRecyclerViewGroupMembers.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewGroupMembers.setAdapter(mRecycleViewGroupFriendsAdapter);
        mRecycleViewGroupFriendsAdapter.setCheckAllItems(true);
        mRecycleViewGroupFriendsAdapter.setOnItemClickListener(new GroupFriendsAdapter.OnItemCheckedListener() {
            @Override
            public void onCheck(User user) {
                participants.add(user);
            }

            @Override
            public void onUncheck(User user) {
                participants.remove(user);
            }
        });
    }

    private void createPurchase() {

        showFormErrors();

        if (!validateForm())
            return;

        TokenManager tokenManager = new TokenManager(this);

        Purchase purchase = new Purchase();

        purchase.setName(mEditTextPurchaseName.getText().toString());
        purchase.setCost(mEditTextCost.getText().toString());
        purchase.setCurrency(mSpinnerCurrency.getSelectedItem().toString());

        purchase.setGroup(((GroupModelIn) mSpinnerGroups.getSelectedItem()).getId());

        if (myBundle != null) {
            purchase.setLatitude(myBundle.getDouble("latitude"));
            purchase.setLongitude(myBundle.getDouble("longitude"));
        }
        purchase.setCurrency(mSpinnerCurrency.getSelectedItem().toString());
        purchase.setGroup(((GroupModelIn) mSpinnerGroups.getSelectedItem()).getId());


        List<String> participantsString = new ArrayList<String>();
        for (int i = 0; i < participants.size(); i++)
            participantsString.add(participants.get(i).getId());

        purchase.setParticipants(participantsString);
        purchase.setPurchaser(tokenManager.getUserIdFromToken());

        PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());
        Call<PurchaseModelIn> call = client.createPurchase(purchase);

        final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

        call.enqueue(new Callback<PurchaseModelIn>() {

            @Override
            public void onResponse(Call<PurchaseModelIn> call, Response<PurchaseModelIn> response) {
                if (response.isSuccessful()) {
                    uploadImageToServer(response.body());
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(CreatePurchaseActivity.this, getString(R.string.error_add_purchase) + " " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PurchaseModelIn> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CreatePurchaseActivity.this, "Error en la comunicación al crear la compra.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToServer(PurchaseModelIn purchase) {

        if (currentImagePath != null) {
            TokenManager tokenManager = new TokenManager(this);

            PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());

            destination = new File(currentImagePath);

            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/" + currentImagePath.substring(currentImagePath.lastIndexOf(".") + 1)), destination);
            MultipartBody.Part part = MultipartBody.Part.createFormData("image", destination.getName(), fileRequestBody);

            final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

            Call call = client.setPurchaseImage(purchase.getId(), part);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        loading.dismiss();
                        destination.delete();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        loading.dismiss();
                        System.out.println("Error al actualizar la imagen.");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    loading.dismiss();
                    Toast.makeText(CreatePurchaseActivity.this, "Error en la conexión al actualizar la imagen.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void showFormErrors() {
        if (!(mEditTextPurchaseName.getText().toString().trim().length() > 0))
            mTextInputLayoutPurchaseName.setError("El nombre es requerído.");
        else
            mTextInputLayoutPurchaseName.setError("");

        if (!(mEditTextCost.getText().toString().trim().length() > 0))
            mTextInputLayoutTxtCost.setError("El costo es requerído.");
        else
            mTextInputLayoutTxtCost.setError("");

        if (!(participants.size() > 0))
            mTextInputLayoutGroupMembers.setError("Por lo menos debe seleccionar un miembro para la compra.");
        else
            mTextInputLayoutGroupMembers.setError("");
    }

    private boolean validateForm() {
        return mEditTextPurchaseName.getText().toString().trim().length() > 0
                && mEditTextCost.getText().length() > 0
                && participants.size() > 0;
    }

    private void selectPurchaseImage() {
        final CharSequence[] options = {"Tomar Foto", "Elegir foto de Galería", "Cancelar"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(CreatePurchaseActivity.this);
        builder.setTitle("Seleccione una Opción");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Tomar Foto")) {

                    PackageManager packageManager = getPackageManager();
                    int checkPermission = packageManager.checkPermission(Manifest.permission.CAMERA, getPackageName());

                    if (checkPermission == PackageManager.PERMISSION_GRANTED) {
                        dialog.dismiss();
                        dispatchTakePictureIntent();
                    } else {
                        ActivityCompat.requestPermissions(CreatePurchaseActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                PICK_IMAGE_CAMERA);
                    }
                } else if (options[item].equals("Elegir foto de Galería")) {

                    PackageManager packageManager = getPackageManager();
                    int checkPermission = packageManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());

                    if (checkPermission == PackageManager.PERMISSION_GRANTED) {
                        dialog.dismiss();
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                    } else {
                        ActivityCompat.requestPermissions(CreatePurchaseActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PICK_IMAGE_GALLERY);
                    }
                } else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(CreatePurchaseActivity.this, "org.quicksplit.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentImagePath = image.getPath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (currentImagePath.length() > 0) {
                        bitmap = BitmapFactory.decodeFile(currentImagePath);
                        mImagePurchase.setImageBitmap(bitmap);
                    }
                } else {
                    currentImagePath = "";
                    Toast.makeText(CreatePurchaseActivity.this, "Error al tomar imagen.", Toast.LENGTH_SHORT).show();
                }
                break;
            case PICK_IMAGE_GALLERY:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        getImageFromGallery(selectedImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    currentImagePath = "";
                    Toast.makeText(CreatePurchaseActivity.this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
                }
                break;
            case SCANN_QR_CAMERA:
                if (resultCode == RESULT_OK) {
                    String dgiLink = data.getExtras().getString("dgi_link");

                    try {
                        URL dgiUrl = new URL(dgiLink);
                        CostWithCurrency costWithCurrency = Utils.QrTicketReader.getCostWithCurrency(dgiUrl);
                        mEditTextCost.setText(costWithCurrency.cost + "");
                        int currencySpinnerPosition = currenciesArrayAdapter.getPosition(costWithCurrency.currency);
                        mSpinnerCurrency.setSelection(currencySpinnerPosition);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_LOCATION_MAP:
                myBundle = data.getExtras();
                break;

        }
    }

    private void getImageFromGallery(Uri selectedImage) throws IOException {

        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        currentImagePath = getRealPathFromURI(selectedImage);
        mImagePurchase.setImageBitmap(bitmap);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.picture, menu);
        return true;
    }
}