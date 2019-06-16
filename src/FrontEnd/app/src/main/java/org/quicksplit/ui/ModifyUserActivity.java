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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.models.User;
import org.quicksplit.service.UserClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyUserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_CAMERA = 1;
    private static final int PICK_IMAGE_GALLERY = 2;

    private Bitmap bitmap;
    private String currentImagePath;

    private User user;
    private File file;
    private Uri imageUri;

    private Toolbar mToolbar;

    private TextView mBigNameLastname;
    private TextView mBigEmail;
    private TextInputLayout mLabelErrorName;
    private EditText mTextName;
    private TextInputLayout mLabelErrorLastName;
    private EditText mTextLastName;
    private TextInputLayout mLabelErrorEmail;
    private EditText mTextEmail;

    private EditText mTextPassword;
    private TextInputLayout mLabelErrorRepeatPassword;
    private EditText mTextRepeatPassword;
    private TextView mLabelErrorMessage;
    private ImageView mImageAvatar;
    private Button mButtonSave;
    private LinearLayout mLayoutChangeAvatar;

    private Button mButtonRefresh;
    private int idMenuResource = R.menu.refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserData();
    }

    private void buildModifyUserContentView() {
        setContentView(R.layout.activity_modify_user);

        idMenuResource = R.menu.picture;

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mBigNameLastname = findViewById(R.id.txt_bigNameLastname);
        mBigEmail = findViewById(R.id.txt_bigEmail);

        mLabelErrorName = findViewById(R.id.lblError_txtName);
        mTextName = findViewById(R.id.txtName);

        mLabelErrorLastName = findViewById(R.id.lblError_txtLastName);
        mTextLastName = findViewById(R.id.txtLastName);

        mLabelErrorEmail = findViewById(R.id.lblError_txtEmail);
        mTextEmail = findViewById(R.id.txtEmail);

        mTextPassword = findViewById(R.id.txtPassword);

        mLabelErrorRepeatPassword = findViewById(R.id.lblError_txtRepeatPassword);
        mTextRepeatPassword = findViewById(R.id.txtRepeatPassword);

        mLabelErrorMessage = findViewById(R.id.lbl_errorMessage);

        mButtonSave = findViewById(R.id.btn_saveChanges);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateUserData();
            }
        });

        mLayoutChangeAvatar = findViewById(R.id.layout_changeAvatar);
        mLayoutChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAvatarImage();
            }
        });

        mImageAvatar = findViewById(R.id.img_avatar);
        loadUserData();
    }

    private void buildErrorContentView() {
        setContentView(R.layout.activity_error);
        idMenuResource = R.menu.refresh;

        mToolbar = findViewById(R.id.toolbar_top);
        mToolbar.setTitle("Modificar Usuario");
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mButtonRefresh = findViewById(R.id.btn_refresh);
        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserData();
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
            case R.id.done:
                updateUserData();
                return true;
            case R.id.picture:
                selectAvatarImage();
                return true;
            case R.id.refresh:
                getUserData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUserData() {
        TokenManager tokenManager = new TokenManager(this);

        UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());
        Call<User> call = client.getUser(tokenManager.getUserIdFromToken());

        final ProgressDialog loading = ProgressDialog.show(this, "Recuperando datos", "Espere...", false, false);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    buildModifyUserContentView();
                    loading.dismiss();
                } else {
                    Toast.makeText(ModifyUserActivity.this, "Error al solicitar la edición de datos.", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                buildErrorContentView();
                loading.dismiss();
            }
        });
    }

    private void loadUserData() {
        mBigNameLastname.setText(user.toString());
        mBigEmail.setText(user.getMail());

        imageUri = Uri.parse(ServiceGenerator.getBaseUrl() + user.getAvatar());
        Picasso.get()
                .load(imageUri)
                .resize(200, 200)
                .centerCrop()
                .into(mImageAvatar);

        mTextName.setText(user.getName());
        mTextLastName.setText(user.getLastName());
        mTextEmail.setText(user.getMail());
    }

    private void updateUserData() {

        if (!validateFieldsAndShowErrors())
            return;

        TokenManager tokenManager = new TokenManager(this);

        User newUser = new User();

        newUser.setName(mTextName.getText().toString());
        newUser.setLastName(mTextLastName.getText().toString());
        newUser.setMail(mTextEmail.getText().toString());
        newUser.setPassword(mTextPassword.getText().toString());

        UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());
        Call<User> call = client.editUser(tokenManager.getUserIdFromToken(), newUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    uploadToServer(currentImagePath);
                } else {
                    String errorMessage = null;
                    try {
                        errorMessage = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mLabelErrorMessage.setVisibility(View.VISIBLE);
                    mLabelErrorMessage.setText(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ModifyUserActivity.this, "Error al modificar datos del usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateFieldsAndShowErrors() {

        boolean isValid = false;

        String name = mTextName.getText().toString();
        String lastName = mTextLastName.getText().toString();
        String email = mTextEmail.getText().toString();
        String password = mTextPassword.getText().toString();
        String repeatPassword = mTextRepeatPassword.getText().toString();

        if (name.isEmpty()) {
            mLabelErrorName.setError("El nombre es requerido.");
            isValid = false;
        } else {
            mLabelErrorName.setError("");
            isValid = true;
        }

        if (lastName.isEmpty()) {
            mLabelErrorLastName.setError("El apellido es requerido.");
            isValid = false;
        } else {
            mLabelErrorLastName.setError("");
            isValid &= true;
        }

        if (email.isEmpty()) {
            mLabelErrorEmail.setError("El email es requerido.");
            isValid = false;
        } else {
            mLabelErrorEmail.setError("");
            isValid &= true;
        }

        if (!password.equals(repeatPassword)) {
            mLabelErrorRepeatPassword.setError("Las contraseñas no coinciden.");
            isValid = false;
        } else {
            mLabelErrorRepeatPassword.setError("");
            isValid &= true;
        }

        return isValid;
    }

    @Override
    public void onClick(View v) {
        updateUserData();
    }

    private void uploadToServer(String filePath) {

        if (filePath == null) {
            Toast.makeText(ModifyUserActivity.this, "Datos modificados correctamente.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TokenManager tokenManager = new TokenManager(this);

        UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());

        file = new File(filePath);

        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/" + filePath.substring(filePath.lastIndexOf(".") + 1)), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), fileRequestBody);

        Call call = client.setUserAvatar(tokenManager.getUserIdFromToken(), part);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Picasso.get().invalidate(imageUri);
                    file.delete();
                    Toast.makeText(ModifyUserActivity.this, "Datos modificados correctamente.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    System.out.println("Error al actualizar la imagen.");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(ModifyUserActivity.this, "Error en la conexión al actualizar la imagen.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectAvatarImage() {

        final CharSequence[] options = {"Tomar Foto", "Elegir foto de Galería", "Cancelar"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ModifyUserActivity.this);
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
                        ActivityCompat.requestPermissions(ModifyUserActivity.this,
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
                        ActivityCompat.requestPermissions(ModifyUserActivity.this,
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (currentImagePath.length() > 0) {
                        bitmap = BitmapFactory.decodeFile(currentImagePath);
                        mImageAvatar.setImageBitmap(bitmap);
                    }
                } else {
                    currentImagePath = "";
                    Toast.makeText(ModifyUserActivity.this, "Error al tomar imagen.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ModifyUserActivity.this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
                Uri photoURI = FileProvider.getUriForFile(ModifyUserActivity.this, "org.quicksplit.android.fileprovider", photoFile);
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

    private void getImageFromGallery(Uri selectedImage) throws IOException {

        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        currentImagePath = getRealPathFromURI(selectedImage);
        mImageAvatar.setImageBitmap(bitmap);
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
}