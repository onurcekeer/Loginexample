package com.project.onur.playerx.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.project.onur.playerx.R;

import net.yslibrary.licenseadapter.LicenseAdapter;
import net.yslibrary.licenseadapter.LicenseEntry;
import net.yslibrary.licenseadapter.Licenses;

import java.util.ArrayList;
import java.util.List;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);


        List<LicenseEntry> licenses = new ArrayList<>();

        // libraries not hosted on GitHub(and you chose not to show license text)
        licenses.add(Licenses.noContent("Android SDK", "Google Inc.",
                "https://developer.android.com/sdk/terms.html"));

        licenses.add(Licenses.noContent("Google Fonts", "Google Inc.", "https://fonts.google.com/specimen/Roboto"));
        licenses.add(Licenses.noContent("Firebase Analytics", "Google Inc.", "https://firebase.google.com/terms/analytics"));
        licenses.add(Licenses.noContent("Facebook Android SDK", "Facebook Inc.", "https://github.com/facebook/facebook-android-sdk/blob/master/LICENSE.txt"));

        licenses.add(Licenses.noContent("Flat Icon", "Flaticon Basic License", "https://profile.flaticon.com/license/free\nhttps://www.flaticon.com/authors/freepik"));
        licenses.add(Licenses.noContent("Other Icon", "roundicons", "https://www.flaticon.com/authors/roundicons"));
        licenses.add(Licenses.noContent("Cinema Icon", "roundicons", "https://www.flaticon.com/authors/roundicons"));
        licenses.add(Licenses.noContent("PC Games Icon", "vectors-market", "https://www.flaticon.com/authors/vectors-market"));
        licenses.add(Licenses.noContent("Calender Icon", "smashicons", "https://www.flaticon.com/authors/smashicons"));
        licenses.add(Licenses.noContent("Clock Icon", "pixel-buddha", "https://www.flaticon.com/authors/pixel-buddha"));



        // this repository does not have license file
        // licenses.add(Licenses.fromGitHub("gabrielemariotti/changeloglib", Licenses.LICENSE_APACHE_V2));

        licenses.add(Licenses.fromGitHubApacheV2("hdodenhof/CircleImageView"));
        licenses.add(Licenses.fromGitHubApacheV2("square/picasso"));
        licenses.add(Licenses.fromGitHubApacheV2("delight-im/Android-SimpleLocation"));
        licenses.add(Licenses.fromGitHubApacheV2("wdullaer/MaterialDateTimePicker"));
        licenses.add(Licenses.fromGitHubApacheV2("oli107/material-range-bar"));
//        licenses.add(Licenses.fromGitHubApacheV2("yshrsmz/KeyboardVisibilityEvent"));
        licenses.add(Licenses.noContent("KeyboardVisibilityEvent", "yshrsmz", "http://www.apache.org/licenses/LICENSE-2.0"));
        licenses.add(Licenses.fromGitHubApacheV2("square/okhttp"));
//        licenses.add(Licenses.fromGitHubApacheV2("greenrobot/EventBus/blob/master/LICENSE.txt"));
        licenses.add(Licenses.noContent("EventBus", "greenrobot", "http://www.apache.org/licenses/LICENSE-2.0"));
        licenses.add(Licenses.fromGitHubApacheV2("google/gson"));
        licenses.add(Licenses.fromGitHubApacheV2("yshrsmz/LicenseAdapter"));
//        licenses.add(Licenses.fromGitHubApacheV2("permissions-dispatcher/PermissionsDispatcher"));
        licenses.add(Licenses.noContent("PermissionsDispatcher", "permissions-dispatcher", "http://www.apache.org/licenses/LICENSE-2.0"));

//        licenses.add(Licenses.fromGitHubMIT("pedant/sweet-alert-dialog/blob/master/README.md"));
        licenses.add(Licenses.noLink("sweet-alert-dialog","pedant",getString(R.string.sweet_alert_dialog_license)));
        licenses.add(Licenses.fromGitHubMIT("jrvansuita/MaterialAbout"));
        licenses.add(Licenses.fromGitHubMIT("lopspower/CircularImageView"));

        LicenseAdapter adapter = new LicenseAdapter(licenses);
        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list.setAdapter(adapter);

        Licenses.load(licenses);


    }
}
