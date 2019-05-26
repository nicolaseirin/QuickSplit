package org.quicksplit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

public class TokenManager {

    private  Context context;

    public TokenManager(Context context){
        this.context = context;
    }

    public String getToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = preferences.getString("token", null);
        return token;
    }

    public static void setToken(String token, Context context) {

    }

    public String getUserIdFromToken() {
        String token = getToken();
        JWT parsedJWT = new JWT(token);
        Claim subscriptionMetaData = parsedJWT.getClaim("Id");
        String userId = subscriptionMetaData.asString();

        return userId;
    }
}
