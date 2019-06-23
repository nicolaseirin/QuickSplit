package org.quicksplit.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.AddFriendsAdapter;
import org.quicksplit.adapters.DeleteFriendsAdapter;
import org.quicksplit.models.Group;
import org.quicksplit.models.GroupModelIn;
import org.quicksplit.models.ModifyPurchase;
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

public class ModifyPurchaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_CAMERA = 1;
    private static final int PICK_IMAGE_GALLERY = 2;

    private String currentImagePath;
    private File file;
    private Bitmap bitmap;
    private Uri imageUri;

    private PurchaseModelIn purchase;
    private GroupModelIn group;

    private ImageView mImagePurchase;

    private Toolbar mToolbar;

    private TextView mTextViewGroupName;

    private TextInputLayout mTextInputLayoutPurchaseName;
    private EditText mEditTextPurchaseName;

    private Spinner mSpinnerCurrency;

    private TextInputLayout mTextInputLayoutTxtCost;
    private EditText mEditTextCost;

    private List<String> currencies;
    private List<User> members;
    private List<User> participants;

    private RecyclerView.LayoutManager mRecyclerViewManager;
    private Button mButtonModifyPurchase;

    private Button mButtonOpenMap;
    private Button mButtonUploadImage;

    private TextInputLayout mTextInputLayoutGroupMembers;
    private RecyclerView mRecyclerViewMembers;
    private AddFriendsAdapter mRecycleViewGroupMembersAdapter;

    private RecyclerView mRecyclerViewPaticipants;
    private DeleteFriendsAdapter mRecycleViewDeleteMembersAdapter;

    private ArrayAdapter<String> currenciesArrayAdapter;

    private Button mButtonRefresh;
    private int idMenuResource = R.menu.refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPurchaseData();
    }

    private void buildModifyPurchaseContentView() {
        setContentView(R.layout.activity_modify_purchase);

        idMenuResource = R.menu.picture;

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mImagePurchase = findViewById(R.id.img_purchase);

        mRecyclerViewMembers = findViewById(R.id.membersReciclerView);
        mRecyclerViewPaticipants = findViewById(R.id.purchasersReciclerView);

        mTextViewGroupName = findViewById(R.id.txt_groupName);

        mTextInputLayoutPurchaseName = findViewById(R.id.lblError_purchaseName);
        mEditTextPurchaseName = findViewById(R.id.txt_purchaseName);

        mSpinnerCurrency = findViewById(R.id.spn_currency);

        mTextInputLayoutTxtCost = findViewById(R.id.lblError_txtCost);
        mEditTextCost = findViewById(R.id.txt_cost);

        mButtonModifyPurchase = findViewById(R.id.btn_modifyPurchase);
        mButtonModifyPurchase.setOnClickListener(this);

        mButtonOpenMap = findViewById(R.id.btn_openMap);
        mButtonOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModifyPurchaseActivity.this, PurchaseMapActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putDouble("latitude", purchase.getLatitude());
                myBundle.putDouble("longitude", purchase.getLongitude());
                intent.putExtras(myBundle);
                startActivity(intent);
            }
        });
                mButtonUploadImage = findViewById(R.id.btn_uploadImage);
        mButtonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPurchaseImage();
            }
        });

        mTextInputLayoutGroupMembers = findViewById(R.id.lblError_groupMembers);
    }

    private void buildErrorContentView() {
        setContentView(R.layout.activity_error);
        idMenuResource = R.menu.refresh;

        mToolbar = findViewById(R.id.toolbar_top);
        mToolbar.setTitle("Modificar Compra");
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mButtonRefresh = findViewById(R.id.btn_refresh);
        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPurchaseData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(idMenuResource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.done:
                modifyPurchase();
                return true;
            case R.id.picture:
                selectPurchaseImage();
                return true;
            case R.id.refresh:
                loadPurchaseData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectPurchaseImage() {

        final CharSequence[] options = {"Tomar Foto", "Elegir foto de Galería", "Cancelar"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ModifyPurchaseActivity.this);
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
                        ActivityCompat.requestPermissions(ModifyPurchaseActivity.this,
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
                        ActivityCompat.requestPermissions(ModifyPurchaseActivity.this,
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
                Uri photoURI = FileProvider.getUriForFile(ModifyPurchaseActivity.this, "org.quicksplit.android.fileprovider", photoFile);
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
                        uploadToServer(currentImagePath);
                        bitmap = BitmapFactory.decodeFile(currentImagePath);
                        mImagePurchase.setImageBitmap(bitmap);
                    }
                } else {
                    currentImagePath = "";
                    Toast.makeText(ModifyPurchaseActivity.this, "Error al tomar imagen.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ModifyPurchaseActivity.this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void uploadToServer(String filePath) {

        TokenManager tokenManager = new TokenManager(this);

        PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());

        file = new File(filePath);

        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/" + filePath.substring(filePath.lastIndexOf(".") + 1)), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), fileRequestBody);

        Call call = client.setPurchaseImage(purchase.getId(), part);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Picasso.get().invalidate(imageUri);
                    file.delete();
                    Toast.makeText(ModifyPurchaseActivity.this, "La imagen se actualizó correctamente.", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Error al actualizar la imagen.");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(ModifyPurchaseActivity.this, "Error en la conexión al actualizar la imagen.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getImageFromGallery(Uri selectedImage) throws IOException {

        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        currentImagePath = getRealPathFromURI(selectedImage);
        mImagePurchase.setImageBitmap(bitmap);
        uploadToServer(currentImagePath);
    }

    public String getRealPathFromURI(Uri contentUri) {

        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(contentUri, filePath,
                null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        String FilePathStr = c.getString(columnIndex);
        c.close();

        return FilePathStr;
    }

    private void loadPurchaseData() {
        String purchaseId = getIntent().getStringExtra("EXTRA_PURCHASE_ID");
        getPurchaseFromId(purchaseId);
    }

    private void getPurchaseFromId(String purchaseId) {
        TokenManager tokenManager = new TokenManager(this);

        PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());
        Call<PurchaseModelIn> call = client.getPurchases(purchaseId);

        final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

        call.enqueue(new Callback<PurchaseModelIn>() {
            @Override
            public void onResponse(Call<PurchaseModelIn> call, Response<PurchaseModelIn> response) {
                if (response.isSuccessful()) {
                    purchase = response.body();
                    buildModifyPurchaseContentView();
                    getPurchaseValues();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(ModifyPurchaseActivity.this, "Error al obtener compras.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PurchaseModelIn> call, Throwable t) {
                loading.dismiss();
                buildErrorContentView();
            }
        });
    }

    private void getPurchaseValues() {

        TokenManager tokenManager = new TokenManager(this);
        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());

        Call<GroupModelIn> call = client.getGroup(purchase.getGroup());
        call.enqueue(new Callback<GroupModelIn>() {
            @Override
            public void onResponse(Call<GroupModelIn> call, Response<GroupModelIn> response) {
                if (response.isSuccessful()) {
                    group = response.body();
                    setPurchaseValues();
                    getAvilableCurrencies();
                    getGroupMembers();
                } else {
                    System.out.println("Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<GroupModelIn> call, Throwable t) {
                buildErrorContentView();
            }
        });
    }

    private void setPurchaseValues() {
        mTextViewGroupName.setText(group.getName());
        mEditTextPurchaseName.setText(purchase.getName());
        mEditTextCost.setText(purchase.getCost());
        imageUri = Uri.parse(ServiceGenerator.getBaseUrl() + "purchases/" + purchase.getId() + "/image");
        Picasso.get()
                .load(imageUri)
                .resize(300, 300)
                .centerCrop()
                .into(mImagePurchase);
    }

    private void getAvilableCurrencies() {
        TokenManager tokenManager = new TokenManager(this);
        CurrencyClient client = ServiceGenerator.createService(CurrencyClient.class, tokenManager.getToken());
        Call<List<String>> call = client.getCurrencies();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    currencies = response.body();
                    buildCurrenciesAdapter();
                } else {
                    System.out.println("Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }

    private void buildCurrenciesAdapter() {

        String currency = purchase.getCurrency();
        currenciesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);
        currenciesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCurrency.setAdapter(currenciesArrayAdapter);

        int currencySpinnerPosition = currenciesArrayAdapter.getPosition(currency);
        mSpinnerCurrency.setSelection(currencySpinnerPosition);
    }

    private void getGroupMembers() {

        members = group.getMemberships();

        User user = new User();
        user.setId(purchase.getPurchaser());
        members.remove(user);

        getParticipants();
    }

    private void getParticipants() {
        participants = purchase.getParticipants();
        buildRecyclerViewAddMembersAdapter();
    }

    private void buildRecyclerViewAddMembersAdapter() {
        members.removeAll(participants);

        mRecyclerViewMembers.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecycleViewGroupMembersAdapter = new AddFriendsAdapter(members);
        mRecyclerViewMembers.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewMembers.setAdapter(mRecycleViewGroupMembersAdapter);
        mRecycleViewGroupMembersAdapter.setOnItemClickListener(new AddFriendsAdapter.OnItemClickListener() {
            @Override
            public void onAddClick(User user) {
                participants.add(user);
                members.remove(user);

                buildRecyclerViewAddMembersAdapter();
                buildRecyclerViewDeleteMembersAdapter();
            }
        });

        buildRecyclerViewDeleteMembersAdapter();
    }

    private void buildRecyclerViewDeleteMembersAdapter() {
        mRecyclerViewPaticipants.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecycleViewDeleteMembersAdapter = new DeleteFriendsAdapter(participants);
        mRecyclerViewPaticipants.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewPaticipants.setAdapter(mRecycleViewDeleteMembersAdapter);
        mRecycleViewDeleteMembersAdapter.setOnItemClickListener(new DeleteFriendsAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(User user) {
                participants.remove(user);
                members.add(user);

                buildRecyclerViewDeleteMembersAdapter();
                buildRecyclerViewAddMembersAdapter();
            }
        });
    }

    @Override
    public void onClick(View v) {
        modifyPurchase();
    }

    private void modifyPurchase() {
        showErrors();

        if (!validFields())
            return;

        ModifyPurchase purchase = new ModifyPurchase();

        purchase.setName(mEditTextPurchaseName.getText().toString());
        purchase.setCurrency(mSpinnerCurrency.getSelectedItem().toString());
        purchase.setCost(mEditTextCost.getText().toString());

        ArrayList<String> participantsString = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++)
            participantsString.add(participants.get(i).getId());
        purchase.setParticipants(participantsString);

        TokenManager tokenManager = new TokenManager(this);

        PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());
        Call<PurchaseModelIn> call = client.modifyPurchase(getIntent().getStringExtra("EXTRA_PURCHASE_ID"), purchase);

        final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

        call.enqueue(new Callback<PurchaseModelIn>() {

            @Override
            public void onResponse(Call<PurchaseModelIn> call, Response<PurchaseModelIn> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    loading.dismiss();
                    Toast.makeText(ModifyPurchaseActivity.this, getString(R.string.error_modify_purchase) + " " + response.code(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                }
            }

            @Override
            public void onFailure(Call<PurchaseModelIn> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ModifyPurchaseActivity.this, "Error en la comunicación al modificar la compra.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showErrors() {
        if (!(mEditTextPurchaseName.getText().toString().trim().length() > 0))
            mTextInputLayoutPurchaseName.setError("El nombre es requerído.");
        else
            mTextInputLayoutPurchaseName.setError("");

        if (!(mEditTextCost.getText().toString().trim().length() > 0))
            mTextInputLayoutTxtCost.setError("El costo es requerído.");
        else
            mTextInputLayoutTxtCost.setError("");

        if (!(participants.size() > 0))
            mTextInputLayoutGroupMembers.setError("Por lo menos debe agregar un miembro para la compra.");
        else
            mTextInputLayoutGroupMembers.setError("");
    }

    private boolean validFields() {
        return mEditTextPurchaseName.getText().toString().trim().length() > 0
                && mEditTextCost.getText().toString().length() > 0
                && participants.size() > 0;
    }
}

