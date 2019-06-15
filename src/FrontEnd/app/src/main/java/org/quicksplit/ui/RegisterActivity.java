package org.quicksplit.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.models.Login;
import org.quicksplit.models.Token;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_CAMERA = 1;
    private static final int PICK_IMAGE_GALLERY = 2;

    private ProgressDialog loading;

    private String currentImagePath;
    private Bitmap bitmap;

    private String token;
    private ImageView mImageViewAvatar;
    private TextInputLayout mLabelErrorName;
    private EditText mTextName;
    private TextInputLayout mLabelErrorLastName;
    private EditText mTextLastName;
    private TextInputLayout mLabelErrorEmail;
    private EditText mTextEmail;
    private TextInputLayout mLabelErrorPassword;
    private EditText mTextPassword;
    private TextInputLayout mLabelErrorRepeatPassword;
    private EditText mTextRepeatPassword;
    private TextView mLabelErrorMessage;
    private Button mButtonregister;
    private TextView mTextViewLogin;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mImageViewAvatar = findViewById(R.id.img_avatar);
        mImageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAvatarImage();
            }
        });

        mLabelErrorName = findViewById(R.id.lblError_txtName);
        mTextName = findViewById(R.id.txtName);

        mLabelErrorLastName = findViewById(R.id.lblError_txtLastName);
        mTextLastName = findViewById(R.id.txtLastName);

        mLabelErrorEmail = findViewById(R.id.lblError_txtEmail);
        mTextEmail = findViewById(R.id.txtEmail);

        mLabelErrorPassword = findViewById(R.id.lblError_txtPassword);
        mTextPassword = findViewById(R.id.txtPassword);

        mLabelErrorRepeatPassword = findViewById(R.id.lblError_txtRepeatPassword);
        mTextRepeatPassword = findViewById(R.id.txtRepeatPassword);

        mLabelErrorMessage = findViewById(R.id.lbl_errorMessage);

        mButtonregister = findViewById(R.id.btn_register);
        mButtonregister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loading = ProgressDialog.show(RegisterActivity.this, getString(R.string.fetching_data), getString(R.string.please_wait), false, false);
                tryToRegisterUser();
            }
        });

        mTextViewLogin = findViewById(R.id.txtView_login);
        mTextViewLogin.setOnClickListener(this);
    }

    private void tryToRegisterUser() {

        User user = new User();

        user.setName(mTextName.getText().toString());
        user.setLastName(mTextLastName.getText().toString());
        user.setMail(mTextEmail.getText().toString());
        user.setPassword(mTextPassword.getText().toString());

        if (!validateFieldsAndShowErrors()) {
            loading.dismiss();
            return;
        }

        registerUser(user);
    }


    private void registerUser(User user) {
        UserClient client = ServiceGenerator.createService(UserClient.class);
        Call<User> call = client.createAccount(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    login();
                } else {
                    loading.dismiss();
                    showErrorMessage(response);
                }
            }

            private void showErrorMessage(Response<User> response) {

                String errorMessage = null;
                try {
                    errorMessage = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mLabelErrorMessage.setVisibility(View.VISIBLE);
                mLabelErrorMessage.setText(errorMessage);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(RegisterActivity.this, "Error al registrar Usuario", Toast.LENGTH_SHORT).show();
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

        if (password.isEmpty()) {
            mLabelErrorPassword.setError("La contraseña es requerida.");
            isValid = false;
        } else {
            mLabelErrorPassword.setError("");
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

    private void login() {

        Login login = new Login();

        login.setMail(mTextEmail.getText().toString());
        login.setPassword(mTextPassword.getText().toString());

        UserClient client = ServiceGenerator.createService(UserClient.class);
        Call<Token> call = client.login(login);

        call.enqueue(new Callback<Token>() {

            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    storageTokenAccess(response);
                    setAvatar();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(RegisterActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storageTokenAccess(Response<Token> response) {
        token = response.body().getToken();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.commit();

        redirect();
    }

    private void setAvatar() {
        if (currentImagePath != null) {
            TokenManager tokenManager = new TokenManager(this);

            final File destination = new File(currentImagePath);

            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/" + currentImagePath.substring(currentImagePath.lastIndexOf(".") + 1)), destination);
            MultipartBody.Part part = MultipartBody.Part.createFormData("image", destination.getName(), fileRequestBody);

            UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());

            Call call = client.setUserAvatar(tokenManager.getUserIdFromToken(), part);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        destination.delete();
                        redirect();
                    } else {
                        System.out.println("Error al actualizar la imagen.");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Error en la conexión al actualizar la imagen.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void redirect() {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    private void selectAvatarImage() {
        final CharSequence[] options = {"Tomar Foto", "Elegir foto de Galería", "Cancelar"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RegisterActivity.this);
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
                        ActivityCompat.requestPermissions(RegisterActivity.this,
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
                        ActivityCompat.requestPermissions(RegisterActivity.this,
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
                Uri photoURI = FileProvider.getUriForFile(RegisterActivity.this, "org.quicksplit.android.fileprovider", photoFile);
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
                        mImageViewAvatar.setImageBitmap(bitmap);
                    }
                } else {
                    currentImagePath = "";
                    Toast.makeText(RegisterActivity.this, "Error al tomar imagen.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RegisterActivity.this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getImageFromGallery(Uri selectedImage) throws IOException {

        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        currentImagePath = getRealPathFromURI(selectedImage);
        mImageViewAvatar.setImageBitmap(bitmap);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
