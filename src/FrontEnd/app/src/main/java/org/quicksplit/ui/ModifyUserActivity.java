package org.quicksplit.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.Utils;
import org.quicksplit.models.User;
import org.quicksplit.service.UserClient;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyUserActivity extends AppCompatActivity implements View.OnClickListener {

    private static int RESULT_LOAD_IMAGE = 1;

    private User user;
    private File file;

    private TextView mBigNameLastname;
    private TextView mBigEmail;
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
    private ImageView mImageAvatar;
    private Button mButtonSave;
    private Button mButtonUploadAvatar;
    private LinearLayout mLayoutChangeAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        mBigNameLastname = findViewById(R.id.txt_bigNameLastname);
        mBigEmail = findViewById(R.id.txt_bigEmail);

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
                openGallery();
            }
        });

        mImageAvatar = findViewById(R.id.img_avatar);
        getUserData();
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
                    loadUserData();
                    loading.dismiss();
                } else {
                    Toast.makeText(ModifyUserActivity.this, "Error al solicitar la edición de datos.", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ModifyUserActivity.this, "Error al solicitar la edición de datos.", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        });
    }

    private void loadUserData() {
        mBigNameLastname.setText(user.getName() + " " + user.getLastName());
        mBigEmail.setText(user.getMail());

        mImageAvatar.setImageBitmap(Utils.stringToBitMap(user.getAvatar()));
        mTextName.setText(user.getName());
        mTextLastName.setText(user.getLastName());
        mTextEmail.setText(user.getMail());
    }

    private void updateUserData() {

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
                    Toast.makeText(ModifyUserActivity.this, "Datos modificados correctamente.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ModifyUserActivity.this, "Error al intentar modificar datos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ModifyUserActivity.this, "Error al modificar datos del usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        updateUserData();
    }

    private void openGallery() {
        ActivityCompat.requestPermissions(ModifyUserActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }

    @Override
    protected void onActivityResult(int recuestCode, int resultCode, Intent data) {
        super.onActivityResult(recuestCode, resultCode, data);

        if (recuestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath,
                    null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String FilePathStr = c.getString(columnIndex);
            c.close();

            uploadToServer(FilePathStr);
        }
    }

    private void uploadToServer(String filePath) {

        TokenManager tokenManager = new TokenManager(this);

        UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());

        file = new File(filePath);

        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/" + filePath.substring(filePath.lastIndexOf(".") + 1)), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);

        Call call = client.setUserAvatar(tokenManager.getUserIdFromToken(), part);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ModifyUserActivity.this, "La imagen se actualizó correctamente.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else {
                    Toast.makeText(ModifyUserActivity.this, "Permission denied to read your External storage.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}