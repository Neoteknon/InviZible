package pan.alexander.tordnscrypt.utils;
/*
    This file is part of InviZible Pro.

    InviZible Pro is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    InviZible Pro is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with InviZible Pro.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2019-2020 by Garmatin Oleksandr invizible.soft@gmail.com
*/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

import pan.alexander.tordnscrypt.MainActivity;
import pan.alexander.tordnscrypt.R;
import pan.alexander.tordnscrypt.TopFragment;

import static pan.alexander.tordnscrypt.utils.RootExecService.LOG_TAG;

public class Registration {
    private final Context context;
    public static boolean wrongRegistrationCode = true;

    public Registration(Context context) {
        this.context = context;
    }

    public void showDonateDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme);
            builder.setMessage(R.string.donate_project)
                    .setTitle(R.string.donate)
                    .setPositiveButton(R.string.enter_code_button, (dialog, which) -> {
                        showEnterCodeDialog();
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss())
                    .setNeutralButton(R.string.donate_button, (dialogInterface, i) -> {
                        String link;
                        if (Locale.getDefault().getLanguage().equalsIgnoreCase("ru")) {
                            link = "https://invizible.net/ru/donate/";
                        } else {
                            link = "https://invizible.net/en/donate/";
                        }
                        Intent donatePage = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        PackageManager packageManager = context.getPackageManager();
                        if (packageManager != null && donatePage.resolveActivity(packageManager) != null) {
                            context.startActivity(donatePage);
                        }
                        dialogInterface.dismiss();
                    })
                    .setCancelable(false);
            if (wrongRegistrationCode) {
                builder.show();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Reg exception " + e.getMessage() + " " + e.getCause());
        }

    }

    public void showEnterCodeDialog() {

        if (context == null || !(context instanceof MainActivity) || ((MainActivity) context).isFinishing()) {
            return;
        }

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        @SuppressLint("InflateParams") final View inputView = inflater.inflate(R.layout.edit_text_for_dialog, null, false);
        final EditText editText = inputView.findViewById(R.id.etForDialog);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme);
        builder .setTitle(R.string.enter_code)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    if (((MainActivity) context).isFinishing()) {
                        dialog.dismiss();
                        return;
                    }

                    new PrefManager(context).setStrPref("registrationCode",editText.getText().toString().trim());

                    wrongRegistrationCode = false;

                    TopFragment topFragment = (TopFragment) ((MainActivity)context).getSupportFragmentManager().findFragmentByTag("topFragmentTAG");
                    if (topFragment!=null) {
                        topFragment.checkNewVer();
                        MainActivity.modernDialog = ((MainActivity)context).modernProgressDialog();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss())
                .setCancelable(false)
                .setView(inputView);
        builder.show();
    }
}
