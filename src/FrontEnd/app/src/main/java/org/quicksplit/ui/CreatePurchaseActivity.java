package org.quicksplit.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.GroupFriendsAdapter;
import org.quicksplit.models.Group;
import org.quicksplit.models.Purchase;
import org.quicksplit.models.User;
import org.quicksplit.service.CurrencyClient;
import org.quicksplit.service.GroupClient;
import org.quicksplit.service.PurchaseClient;
import org.quicksplit.service.UserClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

public class CreatePurchaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static int PICK_IMAGE_CAMERA = 1;
    private static int PICK_IMAGE_GALLERY = 2;
    private ImageView mImageViewPurchase;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String purchaseImagePath = null;


    private List<Group> groups;
    private ArrayAdapter<Group> groupArrayAdapter;
    private ArrayAdapter<String> currenciesArrayAdapter;

    private List<String> currencies;

    private List<User> members;
    private List<User> participants;

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

    private Button mButtonUploadImage;
    private Button mButtonCreatePurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_purchase);

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mImageViewPurchase = findViewById(R.id.img_purchase);

        mTextInputLayoutGroupMembers = findViewById(R.id.lblError_groupMembers);
        mRecyclerViewGroupMembers = findViewById(R.id.purchisersReciclerView);

        mSpinnerGroups = findViewById(R.id.spn_groupName);
        mSpinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getMembers((Group) mSpinnerGroups.getSelectedItem());
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

        mButtonUploadImage = findViewById(R.id.btn_uploadImage);
        mButtonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPurchaseImage();
            }
        });

        mButtonCreatePurchase = findViewById(R.id.btn_createPurchase);
        mButtonCreatePurchase.setOnClickListener(this);

        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.done) {
            createPurchase();
        }

        switch (item.getItemId()) {
            case android.R.id.home:
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
        Call<List<Group>> call = client.getUserGroups(tokenManager.getUserIdFromToken());

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
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
            public void onFailure(Call<List<Group>> call, Throwable t) {
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
        groupArrayAdapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_item, groups);
        groupArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGroups.setAdapter(groupArrayAdapter);
    }

    private void getMembers(Group group) {
        TokenManager tokenManager = new TokenManager(this);

        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());
        Call<List<User>> call = client.getGroupMembers(group.getId());

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    members = response.body();
                    participants = new ArrayList<>(members);
                    buildRecyclerViewCreateGroupFriendsAdapter();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(CreatePurchaseActivity.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CreatePurchaseActivity.this, "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onClick(View v) {
        createPurchase();
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
        purchase.setGroup(((Group) mSpinnerGroups.getSelectedItem()).getId());

        List<String> participantsString = new ArrayList<String>();
        for (int i = 0; i < participants.size(); i++)
            participantsString.add(participants.get(i).getId());

        purchase.setParticipants(participantsString);
        purchase.setPurchaser(tokenManager.getUserIdFromToken());

        PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());
        Call<Purchase> call = client.createPurchase(purchase);

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<Purchase>() {

            @Override
            public void onResponse(Call<Purchase> call, Response<Purchase> response) {
                if (response.isSuccessful()) {
                    uploadImageToServer(response.body());
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(CreatePurchaseActivity.this, "Error al crear la compra", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Purchase> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CreatePurchaseActivity.this, "Error en la comunicación al crear la compra.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToServer(Purchase purchase) {

        if (purchaseImagePath != null) {
            TokenManager tokenManager = new TokenManager(this);

            PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());

            destination = new File(purchaseImagePath);

            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/" + purchaseImagePath.substring(purchaseImagePath.lastIndexOf(".") + 1)), destination);
            MultipartBody.Part part = MultipartBody.Part.createFormData("image", destination.getName(), fileReqBody);

            Call call = client.addPurchaseImage(tokenManager.getUserIdFromToken(), part);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CreatePurchaseActivity.this, "La imagen se actualizó correctamente.", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("Error al actualizar la imagen.");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(CreatePurchaseActivity.this, "Error en la conexión al actualizar la imagen.", Toast.LENGTH_SHORT).show();
                }
            });
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

        ActivityCompat.requestPermissions(CreatePurchaseActivity.this,
                new String[]{Manifest.permission.CAMERA},
                PICK_IMAGE_CAMERA);

        ActivityCompat.requestPermissions(CreatePurchaseActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PICK_IMAGE_GALLERY);

        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(CreatePurchaseActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK && data.hasExtra("data")) {
            try {
                getImageFromCamera(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK && data.hasExtra("data")) {
            Uri selectedImage = data.getData();
            try {
                getImageFromGallery(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getImageFromCamera(Intent data) throws IOException {

        bitmap = (Bitmap) data.getExtras().get("data");
        mImageViewPurchase.setImageBitmap(bitmap);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        purchaseImagePath = image.getAbsolutePath();
    }

    private void getImageFromGallery(Uri selectedImage) throws IOException {

        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        Log.e("Activity", "Pick from Gallery::>>> ");

        purchaseImagePath = getRealPathFromURI(selectedImage);
        destination = new File(purchaseImagePath);
        mImageViewPurchase.setImageBitmap(bitmap);
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