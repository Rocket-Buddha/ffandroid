package org.godotengine.godot;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.FacebookSdkNotInitializedException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class FacebookFF extends Godot.SingletonBase {

    private Godot activity = null;
    private Integer facebookCallbackId = 0;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    static public Godot.SingletonBase initialize(Activity p_activity) { 
        return new FacebookFF(p_activity); 
    } 

    public FacebookFF(Activity p_activity) {
        registerClass("GodotFacebook", new String[]{"init", "setFacebookCallbackId",
         "getFacebookCallbackId", "login", "logout", "isLoggedIn", "shareLink",
         "shareLinkWithoutQuote"});
        activity = (Godot)p_activity;
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(p_activity);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    AccessToken at = loginResult.getAccessToken();
                    GodotLib.calldeferred(facebookCallbackId, "login_success", new Object[]{at.getToken()});
                }

                @Override
                public void onCancel() {
                    GodotLib.calldeferred(facebookCallbackId, "login_cancelled", new Object[]{});
                }

                @Override
                public void onError(FacebookException exception) {
                    GodotLib.calldeferred(facebookCallbackId, "login_failed", new Object[]{exception.toString()});
                }
            });
            
        // Share Dialog callbacks
        
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                GodotLib.calldeferred(facebookCallbackId, "login_success", new Object[]{result.getPostId()});
            }

            @Override
            public void onCancel() {
                GodotLib.calldeferred(facebookCallbackId, "share_cancelled", new Object[]{});
            }

            @Override
            public void onError(FacebookException error) {
                GodotLib.calldeferred(facebookCallbackId, "share_failed", new Object[]{error.toString()});
            }
        });
        
    }

    // Public methods

    public void init(final String key){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    FacebookSdk.setApplicationId(key);
                    FacebookSdk.sdkInitialize(activity.getApplicationContext());                  
                } catch (FacebookSdkNotInitializedException e) {
                    Log.e("godot", "Failed to initialize FacebookSdk: " + e.getMessage()); 
                } catch (Exception e) {
                    Log.e("godot", "Exception: " + e.getMessage());  
                }
            }
        });
    }

    public void setFacebookCallbackId(int facebookCallbackId) {
		this.facebookCallbackId = facebookCallbackId;
	}

    public int getFacebookCallbackId() {
		return facebookCallbackId;
	}

    public void login() {
        Log.i("godot", "Facebook login");
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null && !accessToken.isExpired()) {
            GodotLib.calldeferred(facebookCallbackId, "login_success", new Object[]{accessToken.getToken()});
        } else {
            LoginManager.getInstance().logInWithPublishPermissions(activity, null);
        }
    }

    public void logout() {
        Log.i("godot", "Facebook logout");
        LoginManager.getInstance().logOut();
    }

    public void isLoggedIn() {
        Log.i("godot", "Facebook isLoggedIn");
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken == null) {
            GodotLib.calldeferred(facebookCallbackId, "login_failed", new Object[]{"No token"});
        } else if(accessToken.isExpired()) {
            GodotLib.calldeferred(facebookCallbackId, "login_failed", new Object[]{"No expired"});
        } else {
            GodotLib.calldeferred(facebookCallbackId, "login_success", new Object[]{accessToken.getToken()});
        }
    }
    
    public void shareLinkWithoutQuote(final String link){
        Log.i("godot", "Facebook shareLink");
        if (ShareDialog.canShow(ShareLinkContent.class)) {
        	ShareLinkContent linkContent = new ShareLinkContent.Builder()
        		.setContentUrl(Uri.parse(link))
        		.build();
        	shareDialog.show(linkContent);
        }
    }
    
    public void shareLink(final String link, final String quote){
        Log.i("godot", "Facebook shareLink");
        if (ShareDialog.canShow(ShareLinkContent.class)) {
        	ShareLinkContent linkContent = new ShareLinkContent.Builder()
        		.setContentUrl(Uri.parse(link))
                .setQuote(quote)
        		.build();
        	shareDialog.show(linkContent);
        }
    }

    // Internal methods

    public void callbackSuccess(String ticket, String signature, String sku) {
		//GodotLib.callobject(facebookCallbackId, "purchase_success", new Object[]{ticket, signature, sku});
        //GodotLib.calldeferred(purchaseCallbackId, "consume_fail", new Object[]{});
	}

    @Override protected void onMainActivityResult (int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
